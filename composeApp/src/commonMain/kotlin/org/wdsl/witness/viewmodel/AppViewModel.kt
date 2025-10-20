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
import org.wdsl.witness.model.Settings
import org.wdsl.witness.repository.RepositoryError
import org.wdsl.witness.repository.SettingsRepository

/**
 * ViewModel for managing application settings and state.
 *
 * @property settingsRepository The repository for accessing and updating settings.
 */
class AppViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private var _dynamicThemeMutableStateFlow : MutableStateFlow<Boolean> = MutableStateFlow(false)
    val dynamicThemeStateFlow: StateFlow<Boolean> = _dynamicThemeMutableStateFlow.asStateFlow()

    private var _settingsMutableStateFlow: MutableStateFlow<AppState> = MutableStateFlow(AppState.Loading)
    val settingsStateFlow: StateFlow<AppState> = _settingsMutableStateFlow.asStateFlow()

    private var _isInitialized = false

    @MainThread
    fun initialize() {
        if (_isInitialized)
            return
        _isInitialized = true
        viewModelScope.launch {
            settingsRepository.getSettingsFlow()
                .onSuccess { flow ->
                    flow.collect { settings ->
                        _dynamicThemeMutableStateFlow.value = settings.enableDynamicTheme
                        _settingsMutableStateFlow.value = AppState.Success(settings)
                    }
                }
                .onError { error ->
                    _settingsMutableStateFlow.value = AppState.Error(error)
                }
        }
    }

    fun updateDynamicThemeSetting(enableDynamicTheme: Boolean) {
        viewModelScope.launch {
            val settings = Settings(enableDynamicTheme = enableDynamicTheme)
            settingsRepository.updateSettings(settings).onSuccess {
                _dynamicThemeMutableStateFlow.value = settings.enableDynamicTheme
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val settingsRepository = witnessAppContainer().settingsRepository
                AppViewModel(
                    settingsRepository = settingsRepository,
                )
            }
        }
    }
}

/**
 * Sealed interface representing the state of the application settings.
 */
sealed interface AppState {
    object Loading: AppState
    data class Success(
        val settings: Settings
    ): AppState
    data class Error(
        val error: RepositoryError
    ): AppState
}
