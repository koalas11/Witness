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
import org.wdsl.witness.usecase.GeminiApiUseCase

/**
 * ViewModel for managing recording summaries.
 *
 * @param recordingsRepository Repository for accessing recordings.
 * @param geminiApiUseCase Use case for interacting with the Gemini API.
 */
class RecordingSummaryViewModel(
    private val recordingsRepository: RecordingsRepository,
    private val geminiApiUseCase: GeminiApiUseCase,
): BaseOperationViewModel() {
    private var _recordingSummaryUiState: MutableStateFlow<RecordingSummaryUiState> = MutableStateFlow(
        RecordingSummaryUiState.Loading
    )
    val recordingSummaryUiState: StateFlow<RecordingSummaryUiState> = _recordingSummaryUiState.asStateFlow()

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
                        _recordingSummaryUiState.value = RecordingSummaryUiState.Loaded(
                            recording = recording,
                        )
                    } else {
                        _recordingSummaryUiState.value = RecordingSummaryUiState.Error(
                            message = "Recording not found",
                        )
                    }
                }
            }
                .onError {
                    _recordingSummaryUiState.value = RecordingSummaryUiState.Error(
                        message = "Error loading recording: ${it.message}",
                    )
                }
        }
    }

    fun createRecordingSummary(
        platformContext: PlatformContext,
    ) {
        require(_isInitialized != -1L)
        require(_recordingSummaryUiState.value is RecordingSummaryUiState.Loaded)
        startOperation()
        viewModelScope.launch {
            val recording = (_recordingSummaryUiState.value as RecordingSummaryUiState.Loaded).recording
            geminiApiUseCase.getAudioSummary(
                platformContext = platformContext,
                recording = recording
            ).onSuccess { summary ->
                recordingsRepository.createRecordingSummary(
                    recordingId = _isInitialized,
                    summary = summary
                ).onSuccess {
                    operationUiMutableState.value = OperationUiState.Success(null)
                }.onError { error ->
                    operationUiMutableState.value = OperationUiState.Error(error.message)
                }
            }
            .onError { error ->
                operationUiMutableState.value = OperationUiState.Error(error.message)
            }
        }
    }

    fun deleteRecordingSummary(
    ) {
        require(_isInitialized != -1L)
        startOperation()
        viewModelScope.launch {
            recordingsRepository.deleteRecordingSummary(
                recordingId = _isInitialized,
            ).onSuccess {
                operationUiMutableState.value = OperationUiState.Success(null)
            }.onError { error ->
                operationUiMutableState.value = OperationUiState.Error(error.message)
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val recordingsRepository = witnessAppContainer().recordingsRepository
                val geminiApiUseCase = witnessAppContainer().geminiApiUseCase
                RecordingSummaryViewModel(
                    recordingsRepository = recordingsRepository,
                    geminiApiUseCase = geminiApiUseCase,
                )
            }
        }
    }
}

sealed interface RecordingSummaryUiState {
    object Loading : RecordingSummaryUiState
    data class Loaded(
        val recording: Recording,
    ) : RecordingSummaryUiState
    data class Error(val message: String) : RecordingSummaryUiState
}
