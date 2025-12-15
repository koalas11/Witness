package org.wdsl.witness.usecase

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.wdsl.witness.PlatformContext
import org.wdsl.witness.model.LocationData
import org.wdsl.witness.model.google.GoogleProfile
import org.wdsl.witness.repository.EmergencyContactsRepository
import org.wdsl.witness.repository.GoogleAccountRepository
import org.wdsl.witness.service.GoogleDriveService
import org.wdsl.witness.service.GoogleGmailService
import org.wdsl.witness.service.GoogleOAuthService
import org.wdsl.witness.service.GoogleProfileService
import org.wdsl.witness.storage.room.Recording
import org.wdsl.witness.util.ERROR_NOTIFICATION_CHANNEL_ID
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.Result
import org.wdsl.witness.util.ResultError
import kotlin.coroutines.CoroutineContext
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

/**
 * Use case class for managing Google integration functionalities.
 *
 * @property platformContext The platform context for accessing application-level resources.
 * @property emergencyContactsRepository Repository for managing emergency contacts.
 * @property googleAccountRepository Repository for managing Google account data.
 * @property googleOAuthService Service for handling Google OAuth operations.
 * @property googleProfileService Service for retrieving Google profile information.
 * @property googleGmailService Service for sending emails via Gmail.
 * @property googleDriveService Service for uploading files to Google Drive.
 */
class GoogleIntegrationUseCase(
    private val platformContext: PlatformContext,
    private val emergencyContactsRepository: EmergencyContactsRepository,
    private val googleAccountRepository: GoogleAccountRepository,
    private val googleOAuthService: GoogleOAuthService,
    private val googleProfileService: GoogleProfileService,
    private val googleGmailService: GoogleGmailService,
    private val googleDriveService: GoogleDriveService,
) {
    private var _googleIntegrationMutableState: MutableStateFlow<GoogleIntegrationState> =
        MutableStateFlow(GoogleIntegrationState.NoProfile)
    val googleIntegrationState: StateFlow<GoogleIntegrationState> = _googleIntegrationMutableState.asStateFlow()

    private var _oAuthJob: CoroutineScope? = null
    private var _oAuthTempData: Pair<String, String>? = null
    private var _oAuthDataReceivedMutableState: MutableStateFlow<Pair<String, String>?>? = null

    private var _isInitialized = false

    /**
     * Initializes the Google integration use case by loading the Google profile if available.
     */
    @OptIn(ExperimentalTime::class)
    fun initialize() {
        if (_isInitialized) return
        _isInitialized = true

        platformContext.witnessApp.appScope.launch {
            Log.d(TAG, "Initializing Google Integration Use Case")
            googleAccountRepository.getProfileFlow()
                .onError {
                    Log.d(TAG, "No Google profile found during initialization")
                    _googleIntegrationMutableState.value = GoogleIntegrationState.NoProfile
                }
                .onSuccess { flow ->
                    flow.collect { profile ->
                        if (profile != null) {
                            Log.d(TAG, "Google profile found during initialization: $profile")
                            _googleIntegrationMutableState.value = GoogleIntegrationState.ProfileLoaded(profile)
                            if (profile.lastUpdated < Clock.System.now().epochSeconds - 1.days.inWholeMilliseconds) {
                                updateProfileInfo()
                            }
                        } else {
                            Log.d(TAG, "No Google profile found during initialization")
                            _googleIntegrationMutableState.value = GoogleIntegrationState.NoProfile
                        }
                    }
                }
        }
    }

    /**
     * Starts the Google OAuth flow by launching a coroutine to wait for the OAuth response.
     *
     * @param coroutineContext The coroutine context to launch the OAuth flow in.
     * @param platformContext The platform-specific context for opening the custom tab.
     * @param codeVerifier The code verifier for PKCE.
     * @param state The state parameter to prevent CSRF attacks.
     */
    fun startGoogleOAuthFlow(coroutineContext: CoroutineContext, platformContext: PlatformContext, codeVerifier: String, state: String) {
        _oAuthTempData = Pair(codeVerifier, state)
        _googleIntegrationMutableState.value = GoogleIntegrationState.OAuthInProgress
        _oAuthJob = CoroutineScope(coroutineContext)
        _oAuthJob!!.launch {
            try {
                Log.d(TAG, "Starting to wait for OAuth response")
                waitForOAuthResponse()
            } catch (_: CancellationException) {
                Log.d(TAG, "OAuth process was cancelled")
            } catch (e: Exception) {
                Log.e(TAG, "Error during OAuth process", e)
                _googleIntegrationMutableState.value = GoogleIntegrationState.Error(
                    message = e.message ?: "Unknown error during OAuth"
                )
            } finally {
                _oAuthTempData = null
                _oAuthDataReceivedMutableState = null
                _oAuthJob = null
            }
        }
        googleOAuthService.startGoogleOAuthFlow(platformContext, codeVerifier, state)
    }

    /**
     * Sets the OAuth response data received from the OAuth redirect.
     *
     * @param state The state value received in the OAuth response.
     * @param code The authorization code received in the OAuth response.
     */
    fun setOAuthResponseData(state: String, code: String) {
        requireNotNull(_oAuthDataReceivedMutableState)
        if (state == "" || code == "") {
            _oAuthJob!!.cancel()
            _googleIntegrationMutableState.value = GoogleIntegrationState.Error(message = "Invalid OAuth response data")
            return
        }
        _oAuthDataReceivedMutableState!!.value = Pair(state, code)
    }

    /**
     * Waits for the OAuth response data to be received and handles it accordingly.
     */
    suspend fun waitForOAuthResponse() {
        _oAuthDataReceivedMutableState = MutableStateFlow(null)
        _oAuthDataReceivedMutableState!!.collect {
            Log.d(TAG, "OAuth response data received: $it")
            if (it != null) {
                val (codeVerifier, oldState) = _oAuthTempData!!
                val (state, code) = it
                handleGoogleOAuthResponse(codeVerifier, oldState, state, code)
                _oAuthJob!!.cancel()
            }
        }
    }

    /**
     * Handles the Google OAuth response by validating the state and exchanging the code for tokens.
     *
     * @param codeVerifier The code verifier used in the OAuth flow.
     * @param oldState The original state value sent in the OAuth request.
     * @param state The state value received in the OAuth response.
     * @param code The authorization code received in the OAuth response.
     */
    suspend fun handleGoogleOAuthResponse(
        codeVerifier: String,
        oldState: String,
        state: String,
        code: String
    ) {
        Log.d(TAG, "Handling Google OAuth response")
        if (oldState != state) {
            Log.d(TAG, "State mismatch: expected $oldState but got $state")
            // State mismatch, possible CSRF attack
            _googleIntegrationMutableState.value = GoogleIntegrationState.Error(message = "State mismatch in OAuth response")
            return
        }

        googleOAuthService.handleGoogleOAuthResponse(
            codeVerifier = codeVerifier,
            code = code
        ).onSuccess { googleOAuth ->
            Log.d(TAG, "Google OAuth response handled successfully")
            googleAccountRepository.updateGoogleOAuth(googleOAuth)
            updateProfileInfo()
        }.onError {
            Log.d(TAG, "Error handling Google OAuth response: ${it.message}")
            _googleIntegrationMutableState.value = GoogleIntegrationState.Error(message = it.message)
        }
    }

    /**
     * Updates the Google profile information by retrieving it from the GoogleProfileService.
     */
    suspend fun updateProfileInfo() {
        googleAccountRepository.getGoogleOAuth()
            .onError {
                Log.d(TAG, "No Google OAuth available to update profile info")
                _googleIntegrationMutableState.value = GoogleIntegrationState.NoProfile
            }
            .onSuccess {
                Log.d(TAG, "Updating Google profile info")
                if (it == null) {
                    Log.d(TAG, "No Google OAuth available to update profile info")
                    return@onSuccess
                }
                googleProfileService.getProfileInfo(it)
                    .onSuccess { googleProfile ->
                        Log.d(TAG, "Google profile info retrieved successfully")
                        googleAccountRepository.updateProfile(googleProfile)
                        _googleIntegrationMutableState.value = GoogleIntegrationState.ProfileLoaded(googleProfile)
                    }
            }
    }

    /**
     * Sends emergency emails to all configured emergency contacts.
     *
     * @param subject The subject of the emergency email.
     * @param locationData Optional location data to include in the email.
     */
    suspend fun sendEmergencyEmail(subject: String, locationData: LocationData?) {
        if (_googleIntegrationMutableState.value !is GoogleIntegrationState.ProfileLoaded) {
            Log.d(TAG, "No Google profile loaded, attempting to update profile info")
            updateProfileInfo()
            if (_googleIntegrationMutableState.value !is GoogleIntegrationState.ProfileLoaded) {
                Log.d(TAG, "No Google profile loaded after update, cannot send emergency email")
                return
            }
        }
        emergencyContactsRepository.getEmailEmergencyContacts()
        .onError {
            platformContext.sendNotification(
                ERROR_NOTIFICATION_CHANNEL_ID,
                "Error Sending Emergency Email",
                "Failed to retrieve emergency contacts: ${it.message}",
                2,
            )
        }
        .onSuccess { contacts ->
            googleGmailService.sendEmergencyEmails(contacts, subject, locationData)
        }
    }

    /**
     * Uploads a recording to Google Drive.
     *
     * @param recording The recording to be uploaded.
     * @return A Result indicating success or failure of the upload operation.
     */
    suspend fun uploadRecordingToGoogleDrive(recording: Recording): Result<Unit> {
        return try {
            if (_googleIntegrationMutableState.value !is GoogleIntegrationState.ProfileLoaded) {
                Log.e(TAG, "No Google profile loaded, cannot upload recording")
                return Result.Error(ResultError.UnknownError("No Google profile loaded"))
            }
            return googleDriveService.uploadRecordingToDrive(platformContext, recording)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to upload recording to Google Drive", e)
            Result.Error(ResultError.UnknownError("Failed to upload recording to Google Drive: ${e.message}"))
        }
    }

    /**
     * Signs out the user and deletes the Google account data locally.
     */
    suspend fun signOutAndDeleteAccount(): Result<Unit> {
        val googleOAuth = googleAccountRepository.getGoogleOAuth().getSuccessOrNull()
        if (googleOAuth == null) {
            Log.d(TAG, "No Google Account To SignOut.")
            googleAccountRepository.clearAllData()
            _googleIntegrationMutableState.value = GoogleIntegrationState.NoProfile
            return Result.Success(Unit)
        }

        googleOAuthService.revokeToken(googleOAuth.accessToken, googleOAuth.refreshToken)
            .onSuccess {
                Log.d(TAG, "Successfully revoked Google OAuth token.")
            }
            .onError {
                Log.w(TAG, "Failed to revoke Google OAuth token: ${it.message}")
            }

        googleAccountRepository.clearAllData()
            .onSuccess {
                Log.d(TAG, "Successfully cleared Google account data from repository.")
            }
            .onError {
                Log.e(TAG, "Failed to clear Google account data from repository: ${it.message}")
            }

        emergencyContactsRepository.removeAllEmailEmergencyContacts()
            .onSuccess {
                Log.d(TAG, "Successfully removed all email emergency contacts.")
            }
            .onError {
                Log.e(TAG, "Failed to remove email emergency contacts: ${it.message}")
            }

        _googleIntegrationMutableState.value = GoogleIntegrationState.NoProfile
        return Result.Success(Unit)
    }

    companion object {
        private const val TAG = "GoogleIntegrationUseCase"
    }
}

/**
 * Represents the state of Google integration within the application.
 */
sealed interface GoogleIntegrationState {
    object NoProfile : GoogleIntegrationState
    object OAuthInProgress : GoogleIntegrationState
    data class ProfileLoaded(val googleProfile: GoogleProfile) : GoogleIntegrationState
    object NeedToReauthenticate : GoogleIntegrationState
    data class Error(val message: String) : GoogleIntegrationState
}
