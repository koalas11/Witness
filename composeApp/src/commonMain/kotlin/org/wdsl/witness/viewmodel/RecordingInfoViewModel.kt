package org.wdsl.witness.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.wdsl.witness.PlatformContext
import org.wdsl.witness.repository.RecordingsRepository
import org.wdsl.witness.storage.room.Recording
import org.wdsl.witness.util.deleteRecordingFile

/**
 * ViewModel for managing recording information and operations.
 *
 * @param recordingsRepository Repository for accessing recordings.
 */
class RecordingInfoViewModel(
    private val recordingsRepository: RecordingsRepository,
): BaseOperationViewModel() {
    private var _recordingInfoUiState: MutableStateFlow<RecordingInfoUiState> = MutableStateFlow(
        RecordingInfoUiState.Loading
    )
    val recordingInfoUiState: StateFlow<RecordingInfoUiState> = _recordingInfoUiState.asStateFlow()

    private var _isInitialized = -1L

    @MainThread
    fun initialize(recordingId: Long) {
        if (_isInitialized == recordingId)
            return
        _isInitialized = recordingId

        viewModelScope.launch {
            recordingsRepository.getRecordingFlowById(recordingId).onSuccess { flow ->
                flow.collect { recording ->
                    if (recording != null) {
                        _recordingInfoUiState.value = RecordingInfoUiState.Loaded(
                            recording = recording,
                        )
                    } else {
                        _recordingInfoUiState.value = RecordingInfoUiState.Error(
                            message = "Recording not found",
                        )
                    }
                }
            }
            .onError {
                _recordingInfoUiState.value = RecordingInfoUiState.Error(
                    message = "Error loading recording: ${it.message}",
                )
            }
        }
    }

    fun deleteRecording(platformContext: PlatformContext, recording: Recording) {
        startOperation()
        viewModelScope.launch {
            recordingsRepository.deleteRecording(recording)
                .onSuccess {
                    operationUiMutableState.value = OperationUiState.Success("Recording deleted successfully.")
                    deleteRecordingFile(platformContext, recording.recordingFileName)
                }
                .onError {
                    operationUiMutableState.value = OperationUiState.Error("Failed to delete recording: ${it.message}")
                }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val recordingsRepository = witnessAppContainer().recordingsRepository
                RecordingInfoViewModel(
                    recordingsRepository = recordingsRepository,
                )
            }
        }
    }
}

sealed interface RecordingInfoUiState {
    object Loading : RecordingInfoUiState
    data class Loaded(
        val recording: Recording,
    ) : RecordingInfoUiState
    data class Error(val message: String) : RecordingInfoUiState
}
