package org.wdsl.witness.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.wdsl.witness.repository.RecordingsRepository
import org.wdsl.witness.storage.room.Recording

class RecordingInfoViewModel(
    private val recordingsRepository: RecordingsRepository,
): ViewModel() {
    private var _recordingInfoUiState: MutableStateFlow<RecordingInfoUiState> = MutableStateFlow(
        RecordingInfoUiState.Loading
    )
    val recordingInfoUiState: StateFlow<RecordingInfoUiState> = _recordingInfoUiState.asStateFlow()

    private var _isInitialized = false

    @MainThread
    fun initialize(recordingId: Long) {
        if (_isInitialized)
            return
        _isInitialized = true

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
