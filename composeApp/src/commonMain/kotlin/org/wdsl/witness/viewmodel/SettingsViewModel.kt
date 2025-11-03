package org.wdsl.witness.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import org.wdsl.witness.model.DynamicColorMode
import org.wdsl.witness.model.NotificationsSetting
import org.wdsl.witness.model.Settings
import org.wdsl.witness.model.ThemeMode
import org.wdsl.witness.repository.SettingsRepository

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

        /**
         * A sealed interface that represents the error notifications for managing transactions.
         */
        sealed interface ManageTransactionErrorNotifications: ErrorNotifications {
            object TransactionNotFound : ManageTransactionErrorNotifications {
                override val message: String = "Transaction not found"
            }

            object TitleEmpty : ManageTransactionErrorNotifications {
                override val message: String = "Title cannot be empty"
            }

            object AmountInvalid : ManageTransactionErrorNotifications {
                override val message: String = "Amount must be greater than 0"
            }
        }
    }
}
