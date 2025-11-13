package org.wdsl.witness.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.wdsl.witness.repository.RecordingsRepository
import org.wdsl.witness.storage.room.Recording

class RecordingsViewModel(
    private val recordingsRepository: RecordingsRepository,
): ViewModel() {
    private var _recordingsUiState = MutableStateFlow<RecordingsUiState>(RecordingsUiState.Loading)
    val recordingsUiState = _recordingsUiState.asStateFlow()

    private var _isInitialized = false

    @MainThread
    fun initialize() {
        if (_isInitialized) return
        _isInitialized = true

        viewModelScope.launch {
            recordingsRepository.getRecordingsFlow().onSuccess { flow ->
                flow.collect {
                    _recordingsUiState.value = RecordingsUiState.Success(recordings = it)
                }
            }.onError {
                _recordingsUiState.value = RecordingsUiState.Error(message = it.message)
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val recordingsRepository = witnessAppContainer().recordingsRepository
                RecordingsViewModel(
                    recordingsRepository = recordingsRepository,
                )
            }
        }
    }
}

sealed interface RecordingsUiState {
    object Loading : RecordingsUiState
    data class Success(val recordings: List<Recording>) : RecordingsUiState
    data class Error(val message: String) : RecordingsUiState
}
