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
import org.wdsl.witness.model.settings.DynamicColorMode
import org.wdsl.witness.model.settings.NotificationsSetting
import org.wdsl.witness.model.settings.Settings
import org.wdsl.witness.model.settings.ThemeMode
import org.wdsl.witness.repository.SettingsRepository
import org.wdsl.witness.ui.navigation.ScreenRoute
import org.wdsl.witness.ui.navigation.ShowBackButton
import org.wdsl.witness.usecase.EmergencyRecordingUseCase
import org.wdsl.witness.util.ResultError

/**
 * ViewModel for managing application settings and state.
 *
 * @property settingsRepository The repository for accessing and updating settings.
 * @property emergencyRecordingUseCase The use case for managing emergency recording.
 */
class AppViewModel(
    private val settingsRepository: SettingsRepository,
    private val emergencyRecordingUseCase: EmergencyRecordingUseCase,
) : ViewModel() {
    private var _themeSettingsMutableState: MutableStateFlow<Pair<DynamicColorMode, ThemeMode>> = MutableStateFlow(
        Pair(DynamicColorMode.ENABLED, ThemeMode.SYSTEM_DEFAULT)
    )
    val themeSettingsState: StateFlow<Pair<DynamicColorMode, ThemeMode>> = _themeSettingsMutableState.asStateFlow()

    private var _notificationsSettingMutableState: MutableStateFlow<NotificationsSetting> = MutableStateFlow(NotificationsSetting.ALL_NOTIFICATIONS)
    val notificationsSettingState: StateFlow<NotificationsSetting> = _notificationsSettingMutableState.asStateFlow()

    private var _settingsMutableState: MutableStateFlow<AppState> = MutableStateFlow(AppState.Loading)
    val settingsState: StateFlow<AppState> = _settingsMutableState.asStateFlow()

    private var _backStack: MutableStateFlow<MutableList<ScreenRoute>> = MutableStateFlow(mutableListOf(
        ScreenRoute.Home))
    val backStack: StateFlow<List<ScreenRoute>> = _backStack.asStateFlow()

    private var _authenticated: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val authenticated: StateFlow<Boolean> = _authenticated.asStateFlow()

    private var _shouldShowBackButton: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val shouldShowBackButton: StateFlow<Boolean> = _shouldShowBackButton.asStateFlow()

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
                    if (settings.dynamicColorMode != _themeSettingsMutableState.value.first ||
                        settings.themeMode != _themeSettingsMutableState.value.second
                    ) {
                        _themeSettingsMutableState.value =
                            Pair(settings.dynamicColorMode, settings.themeMode)
                    }
                    if (settings.notificationsSetting != _notificationsSettingMutableState.value) {
                        _notificationsSettingMutableState.value = settings.notificationsSetting
                    }
                    _settingsMutableState.value = AppState.Success(settings)
                }
            }
            .onError { error ->
                _settingsMutableState.value = AppState.Error(error)
            }
        }
    }

    fun navigateTo(route: ScreenRoute) {
        if (_backStack.value.lastOrNull() == route) {
            return
        }
        val newBackStack = _backStack.value.toMutableList()
        val oldIndex = newBackStack.indexOf(route)
        if (oldIndex != -1) {
            newBackStack.removeAt(oldIndex)
        }
        newBackStack.add(route)
        _backStack.value = newBackStack
        _shouldShowBackButton.value = newBackStack.last() is ShowBackButton
    }

    fun navigateBack() {
        val newBackStack = _backStack.value.toMutableList()
        newBackStack.removeLast()
        if (newBackStack.isEmpty()) {
            newBackStack.add(ScreenRoute.Home)
        }
        _backStack.value = newBackStack
        _shouldShowBackButton.value = newBackStack.last() is ShowBackButton
    }

    fun authenticate() {
        _authenticated.value = true
    }

    fun startAudioRecording() {
        viewModelScope.launch {
            emergencyRecordingUseCase.startEmergencyRecording()
        }
    }

    fun stopAudioRecording() {
        viewModelScope.launch {
            emergencyRecordingUseCase.stopEmergencyRecording()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val settingsRepository = witnessAppContainer().settingsRepository
                val emergencyRecordingUseCase = witnessAppContainer().emergencyRecordingUseCase
                AppViewModel(
                    settingsRepository = settingsRepository,
                    emergencyRecordingUseCase = emergencyRecordingUseCase,
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
        val error: ResultError
    ): AppState
}
