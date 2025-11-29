package org.wdsl.witness.usecase

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.wdsl.witness.PlatformContext
import org.wdsl.witness.model.GoogleProfile
import org.wdsl.witness.model.LocationData
import org.wdsl.witness.repository.EmergencyContactsRepository
import org.wdsl.witness.repository.GoogleAccountRepository
import org.wdsl.witness.service.GoogleDriveService
import org.wdsl.witness.service.GoogleGmailService
import org.wdsl.witness.service.GoogleOAuthService
import org.wdsl.witness.service.GoogleProfileService
import org.wdsl.witness.util.ERROR_NOTIFICATION_CHANNEL_ID
import org.wdsl.witness.util.Log
import kotlin.coroutines.CoroutineContext

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

    fun setOAuthResponseData(state: String, code: String) {
        requireNotNull(_oAuthDataReceivedMutableState)
        if (state == "" || code == "") {
            _oAuthJob!!.cancel()
            _googleIntegrationMutableState.value = GoogleIntegrationState.Error(message = "Invalid OAuth response data")
            return
        }
        _oAuthDataReceivedMutableState!!.value = Pair(state, code)
    }

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

    suspend fun sendEmergencyEmail(subject: String, locationData: LocationData?) {
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

    private fun updateState() {

    }

    companion object {
        private const val TAG = "GoogleIntegrationUseCase"
    }
}

sealed interface GoogleIntegrationState {
    object NoProfile : GoogleIntegrationState
    object OAuthInProgress : GoogleIntegrationState
    object UpdatingProfile : GoogleIntegrationState
    data class OldProfileLoaded(val googleProfile: GoogleProfile) : GoogleIntegrationState
    data class ProfileLoaded(val googleProfile: GoogleProfile) : GoogleIntegrationState
    object NeedToReauthenticate : GoogleIntegrationState
    data class Error(val message: String) : GoogleIntegrationState
}
