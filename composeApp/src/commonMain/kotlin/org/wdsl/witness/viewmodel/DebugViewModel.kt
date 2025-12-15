package org.wdsl.witness.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import org.wdsl.witness.PlatformContext
import org.wdsl.witness.repository.RecordingsRepository
import org.wdsl.witness.util.deleteRecordingFile

/**
 * ViewModel for debugging purposes, providing operations such as clearing all recordings.
 *
 * @param recordingsRepository Repository for managing recordings.
 */
class DebugViewModel(
    private val recordingsRepository: RecordingsRepository,
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

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val recordingsRepository = witnessAppContainer().recordingsRepository
                DebugViewModel(
                    recordingsRepository = recordingsRepository
                )
            }
        }
    }
}
