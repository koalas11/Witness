package org.wdsl.witness.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
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
import org.wdsl.witness.usecase.GoogleIntegrationState
import org.wdsl.witness.usecase.GoogleIntegrationUseCase
import org.wdsl.witness.util.Log
import kotlin.io.encoding.Base64
import kotlin.random.Random

class GoogleOAuthViewModel(
    private val googleIntegrationUseCase: GoogleIntegrationUseCase,
) : ViewModel() {
    private var _googleOAuthUiMutableState = MutableStateFlow<GoogleOAuthUiState>(GoogleOAuthUiState.NoProfile)
    val googleOAuthUiState : StateFlow<GoogleOAuthUiState> = _googleOAuthUiMutableState.asStateFlow()

    private var _isInitialized = false
    @MainThread
    fun initialize() {
        if (_isInitialized) return
        _isInitialized = true

        viewModelScope.launch {
            googleIntegrationUseCase.googleIntegrationState.collect { state ->
                Log.d("GoogleOAuthViewModel", "Current Google OAuth State: $state")
                when (state) {
                    is GoogleIntegrationState.OAuthInProgress -> {
                    }
                    is GoogleIntegrationState.ProfileLoaded -> {
                        _googleOAuthUiMutableState.value = GoogleOAuthUiState.Profile(state.googleProfile)
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

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val googleIntegrationUseCase = witnessAppContainer().googleIntegrationUseCase
                GoogleOAuthViewModel(
                    googleIntegrationUseCase = googleIntegrationUseCase,
                )
            }
        }
    }
}

sealed interface GoogleOAuthUiState {
    object NoProfile : GoogleOAuthUiState
    object Loading : GoogleOAuthUiState
    data class Profile(val googleProfile: GoogleProfile) : GoogleOAuthUiState
    data class Error(val message: String) : GoogleOAuthUiState
}
