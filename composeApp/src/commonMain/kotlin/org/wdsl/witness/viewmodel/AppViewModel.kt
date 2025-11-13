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
import org.wdsl.witness.model.DynamicColorMode
import org.wdsl.witness.model.NotificationsSetting
import org.wdsl.witness.model.Settings
import org.wdsl.witness.model.ThemeMode
import org.wdsl.witness.module.audio.AudioRecorderModule
import org.wdsl.witness.repository.RecordingsRepository
import org.wdsl.witness.repository.SettingsRepository
import org.wdsl.witness.storage.room.Recording
import org.wdsl.witness.ui.navigation.ScreenRoute
import org.wdsl.witness.util.ResultError
import kotlin.time.Clock

/**
 * ViewModel for managing application settings and state.
 *
 * @property settingsRepository The repository for accessing and updating settings.
 */
class AppViewModel(
    private val settingsRepository: SettingsRepository,
    private val recordingsRepository: RecordingsRepository,
    private val audioRecorderModule: AudioRecorderModule,
) : ViewModel() {
    private var _themeSettingsMutableStateFlow: MutableStateFlow<Pair<DynamicColorMode, ThemeMode>> = MutableStateFlow(
        Pair(DynamicColorMode.ENABLED, ThemeMode.SYSTEM_DEFAULT)
    )
    val themeSettingsStateFlow: StateFlow<Pair<DynamicColorMode, ThemeMode>> = _themeSettingsMutableStateFlow.asStateFlow()

    private var _notificationsSettingMutableStateFlow: MutableStateFlow<NotificationsSetting> = MutableStateFlow(NotificationsSetting.ALL_NOTIFICATIONS)
    val notificationsSettingStateFlow: StateFlow<NotificationsSetting> = _notificationsSettingMutableStateFlow.asStateFlow()

    private var _settingsMutableStateFlow: MutableStateFlow<AppState> = MutableStateFlow(AppState.Loading)
    val settingsStateFlow: StateFlow<AppState> = _settingsMutableStateFlow.asStateFlow()

    private var _backStack: MutableStateFlow<MutableList<ScreenRoute>> = MutableStateFlow(mutableListOf(
        ScreenRoute.Home))
    val backStack: StateFlow<List<ScreenRoute>> = _backStack.asStateFlow()

    private var _recordingUiMutableState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val recordingUiState: StateFlow<Boolean> = _recordingUiMutableState.asStateFlow()

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
                    if (settings.dynamicColorMode != _themeSettingsMutableStateFlow.value.first ||
                        settings.themeMode != _themeSettingsMutableStateFlow.value.second
                    ) {
                        _themeSettingsMutableStateFlow.value =
                            Pair(settings.dynamicColorMode, settings.themeMode)
                    }
                    if (settings.notificationsSetting != _notificationsSettingMutableStateFlow.value) {
                        _notificationsSettingMutableStateFlow.value = settings.notificationsSetting
                    }
                    _settingsMutableStateFlow.value = AppState.Success(settings)
                }
            }
            .onError { error ->
                _settingsMutableStateFlow.value = AppState.Error(error)
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
    }

    fun navigateBack() {
        val newBackStack = _backStack.value.toMutableList()
        newBackStack.removeLast()
        if (newBackStack.isEmpty()) {
            newBackStack.add(ScreenRoute.Home)
        }
        _backStack.value = newBackStack
    }

    private var _recordingFileName: String? = null

    fun startAudioRecording() {
        _recordingUiMutableState.value = true
        viewModelScope.launch {
            audioRecorderModule.startRecording().onSuccess {
                _recordingFileName = it
            }
        }
    }

    fun stopAudioRecording() {
        requireNotNull(_recordingFileName) { "No recording in progress" }
        _recordingUiMutableState.value = false
        viewModelScope.launch {
            audioRecorderModule.stopRecording()
            val recording = Recording(
                id = 0,
                title = "New Recording",
                recordingFileName = _recordingFileName!!,
                durationMs = 0L,
            )
            recordingsRepository.insertRecording(recording)
            _recordingFileName = null
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val settingsRepository = witnessAppContainer().settingsRepository
                val recordingsRepository = witnessAppContainer().recordingsRepository
                val audioRecorderModule = witnessAppContainer().audioRecorderModule
                AppViewModel(
                    settingsRepository = settingsRepository,
                    recordingsRepository = recordingsRepository,
                    audioRecorderModule = audioRecorderModule,
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
