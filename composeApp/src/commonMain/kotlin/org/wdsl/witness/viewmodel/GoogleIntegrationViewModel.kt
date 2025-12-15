package org.wdsl.witness.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.wdsl.witness.PlatformContext
import org.wdsl.witness.model.google.GoogleProfile
import org.wdsl.witness.repository.SettingsRepository
import org.wdsl.witness.storage.room.Recording
import org.wdsl.witness.usecase.GoogleIntegrationState
import org.wdsl.witness.usecase.GoogleIntegrationUseCase
import org.wdsl.witness.util.Log
import kotlin.io.encoding.Base64
import kotlin.random.Random

/**
 * ViewModel for managing Google Integration functionality.
 *
 * @param googleIntegrationUseCase Use case for Google Integration operations.
 */
class GoogleIntegrationViewModel(
    private val googleIntegrationUseCase: GoogleIntegrationUseCase,
    private val settingsRepository: SettingsRepository,
) : BaseOperationViewModel() {
    private var _googleIntegrationUiMutableState = MutableStateFlow<GoogleIntegrationUiState>(GoogleIntegrationUiState.Loading)
    val googleIntegrationUiState : StateFlow<GoogleIntegrationUiState> = _googleIntegrationUiMutableState.asStateFlow()

    private var _isInitialized = false
    @MainThread
    fun initialize() {
        if (_isInitialized) return
        _isInitialized = true

        viewModelScope.launch {
            googleIntegrationUseCase.initialize()
            googleIntegrationUseCase.googleIntegrationState.collect { state ->
                Log.d("GoogleOAuthViewModel", "Current Google OAuth State: $state")
                when (state) {
                    is GoogleIntegrationState.OAuthInProgress -> {
                        _googleIntegrationUiMutableState.value = GoogleIntegrationUiState.OAuthInProgress
                    }
                    is GoogleIntegrationState.ProfileLoaded -> {
                        _googleIntegrationUiMutableState.value = GoogleIntegrationUiState.Profile(state.googleProfile)
                    }
                    is GoogleIntegrationState.NoProfile -> {
                        _googleIntegrationUiMutableState.value = GoogleIntegrationUiState.NoProfile
                    }
                    else -> {
                    }
                }
            }
        }
    }

    fun startGoogleOAuthFlow(platformContext: PlatformContext) {
        viewModelScope.launch {
            val bytes = Random.nextBytes(64)
            val codeVerifier = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT).encode(bytes)
            val state = Base64.encode(Random.nextBytes(32))
            googleIntegrationUseCase.startGoogleOAuthFlow(this.coroutineContext, platformContext, codeVerifier, state)
        }
    }

    fun uploadRecordingToGoogleDrive(
        recording: Recording,
    ) {
        startOperation()
        viewModelScope.launch {
            googleIntegrationUseCase.uploadRecordingToGoogleDrive(
                recording = recording,
            ).onSuccess {
                Log.d("GoogleIntegrationViewModel", "Successfully uploaded recording to Google Drive")
                operationUiMutableState.value = OperationUiState.Success("Recording uploaded to Google Drive successfully.")
            }.onError { error ->
                Log.e("GoogleIntegrationViewModel", "Failed to upload recording to Google Drive: ${error.message}")
                operationUiMutableState.value =
                    OperationUiState.Error("Failed to upload recording to Google Drive: ${error.message}")
            }
        }
    }

    fun signOut() {
        startOperation()
        viewModelScope.launch {
            googleIntegrationUseCase.signOutAndDeleteAccount()
                .onSuccess {
                    Log.d("GoogleIntegrationViewModel", "Successfully signed out and deleted Google account integration")
                    operationUiMutableState.value = OperationUiState.Success("Signed out from Google successfully.")
                }
                .onError { error ->
                    Log.e("GoogleIntegrationViewModel", "Failed to sign out from Google: ${error.message}")
                    operationUiMutableState.value =
                        OperationUiState.Error("Failed to sign out from Google: ${error.message}")
                }
        }
    }

    fun setEnableEmailOnEmergency(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateSettings { settings ->
                settings.copy(
                    enableEmailOnEmergency = enabled
                )
            }
        }
    }

    fun setUploadRecordingToDriveOnEnd(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateSettings { settings ->
                settings.copy(
                    uploadRecordingToDriveOnEnd = enabled
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val googleIntegrationUseCase = witnessAppContainer().googleIntegrationUseCase
                val settingsRepository = witnessAppContainer().settingsRepository
                GoogleIntegrationViewModel(
                    googleIntegrationUseCase = googleIntegrationUseCase,
                    settingsRepository = settingsRepository,
                )
            }
        }
    }
}

sealed interface GoogleIntegrationUiState {
    object NoProfile : GoogleIntegrationUiState
    object Loading : GoogleIntegrationUiState
    object OAuthInProgress : GoogleIntegrationUiState
    data class Profile(val googleProfile: GoogleProfile) : GoogleIntegrationUiState
    data class Error(val message: String) : GoogleIntegrationUiState
}
