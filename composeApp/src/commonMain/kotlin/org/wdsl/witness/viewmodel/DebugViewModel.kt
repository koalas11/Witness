package org.wdsl.witness.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import org.wdsl.witness.PlatformContext
import org.wdsl.witness.repository.RecordingsRepository
import org.wdsl.witness.repository.SettingsRepository
import org.wdsl.witness.util.deleteRecordingFile

/**
 * ViewModel for debugging purposes, providing operations such as clearing all recordings.
 *
 * @param recordingsRepository Repository for managing recordings.
 */
class DebugViewModel(
    private val recordingsRepository: RecordingsRepository,
    private val settingsRepository: SettingsRepository,
): BaseOperationViewModel() {

    fun clearAllRecordings(platformContext: PlatformContext) {
        startOperation()
        viewModelScope.launch  {
            recordingsRepository.clearAllRecordings()
                .onSuccess {
                    operationUiMutableState.value = OperationUiState.Success("All recordings cleared.")
                    recordingsRepository.getAllRecordingFileNames().onSuccess {
                        it.forEach { fileName ->
                            deleteRecordingFile(
                                platformContext = platformContext,
                                fileName = fileName,
                            )
                        }
                    }
                }
                .onError { error ->
                    operationUiMutableState.value =
                        OperationUiState.Error("Failed to clear recordings: ${error.message}")
                }
        }
    }

    fun resetTutorialState() {
        startOperation()
        viewModelScope.launch {
            settingsRepository.updateSettings {
                it.copy(
                    tutorialDone = false
                )
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val recordingsRepository = witnessAppContainer().recordingsRepository
                val settingsRepository = witnessAppContainer().settingsRepository
                DebugViewModel(
                    recordingsRepository = recordingsRepository,
                    settingsRepository = settingsRepository,
                )
            }
        }
    }
}
