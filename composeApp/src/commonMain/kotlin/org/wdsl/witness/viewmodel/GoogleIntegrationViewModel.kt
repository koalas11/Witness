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
import org.wdsl.witness.model.GoogleProfile
import org.wdsl.witness.storage.room.Recording
import org.wdsl.witness.usecase.GoogleIntegrationState
import org.wdsl.witness.usecase.GoogleIntegrationUseCase
import org.wdsl.witness.util.Log
import kotlin.io.encoding.Base64
import kotlin.random.Random

class GoogleIntegrationViewModel(
    private val googleIntegrationUseCase: GoogleIntegrationUseCase,
) : BaseOperationViewModel() {
    private var _googleIntegrationUiMutableState = MutableStateFlow<GoogleIntegrationUiState>(GoogleIntegrationUiState.NoProfile)
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
                    }
                    is GoogleIntegrationState.ProfileLoaded -> {
                        _googleIntegrationUiMutableState.value = GoogleIntegrationUiState.Profile(state.googleProfile)
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

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val googleIntegrationUseCase = witnessAppContainer().googleIntegrationUseCase
                GoogleIntegrationViewModel(
                    googleIntegrationUseCase = googleIntegrationUseCase,
                )
            }
        }
    }
}

sealed interface GoogleIntegrationUiState {
    object NoProfile : GoogleIntegrationUiState
    object Loading : GoogleIntegrationUiState
    data class Profile(val googleProfile: GoogleProfile) : GoogleIntegrationUiState
    data class Error(val message: String) : GoogleIntegrationUiState
}
