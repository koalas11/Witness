package org.wdsl.witness.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import org.wdsl.witness.model.settings.DynamicColorMode
import org.wdsl.witness.model.settings.NotificationsSetting
import org.wdsl.witness.model.settings.Settings
import org.wdsl.witness.model.settings.ThemeMode
import org.wdsl.witness.repository.SettingsRepository

/**
 * ViewModel for managing user settings.
 *
 * @property settingsRepository The repository for accessing and updating settings.
 */
class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
) : BaseOperationViewModel() {

    private fun updateSettings(modify: (Settings) -> Settings) {
        startOperation()
        viewModelScope.launch {
            settingsRepository.updateSettings(modify).onSuccess {
                operationUiMutableState.value = OperationUiState.Success(null)
            }.onError { error ->
                operationUiMutableState.value = OperationUiState.Error(error.message)
            }
        }
    }

    fun setDynamicColorMode(dynamicColorMode: DynamicColorMode) {
        updateSettings { currentSettings ->
            currentSettings.copy(
                dynamicColorMode = dynamicColorMode
            )
        }
    }

    fun setThemeMode(themeMode: ThemeMode) {
        updateSettings { currentSettings ->
            currentSettings.copy(
                themeMode = themeMode
            )
        }
    }

    fun setNotificationsSetting(notificationsSetting: NotificationsSetting) {
        updateSettings { currentSettings ->
            currentSettings.copy(
                notificationsSetting = notificationsSetting
            )
        }
    }

    fun setEnableVibrationOnEmergencyRegistrationStart(enable: Boolean) {
        updateSettings { currentSettings ->
            currentSettings.copy(
                enableVibrationOnEmergencyRegistrationStart = enable
            )
        }
    }

    fun setEnableSmsOnEmergency(enable: Boolean) {
        updateSettings { currentSettings ->
            currentSettings.copy(
                enableSmsOnEmergency = enable
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val settingsRepository = witnessAppContainer().settingsRepository
                SettingsViewModel(
                    settingsRepository = settingsRepository,
                )
            }
        }

        /**
         * A sealed interface that represents the success notifications for managing transactions.
         */
        sealed interface SettingsSuccessNotifications : SuccessNotifications {
        }
    }
}
