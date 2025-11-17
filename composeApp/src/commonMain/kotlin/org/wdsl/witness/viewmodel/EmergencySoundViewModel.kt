package org.wdsl.witness.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.wdsl.witness.module.SoundAlertModule

class EmergencySoundViewModel(
    private val soundAlertModule: SoundAlertModule,
) : BaseOperationViewModel() {
    private var _emergencySoundUiMutableState: MutableStateFlow<EmergencySoundUIState> =
        MutableStateFlow(EmergencySoundUIState.Loading)
    val emergencySoundUiState: StateFlow<EmergencySoundUIState> =
        _emergencySoundUiMutableState.asStateFlow()

    private var isInitialized = false

    @MainThread
    fun initialize() {
        if (isInitialized) return
        isInitialized = true

        viewModelScope.launch {
            soundAlertModule.isSoundAlertSupported().onError {
                _emergencySoundUiMutableState.value = EmergencySoundUIState.Error
            }.onSuccess { isSupported ->
                if (isSupported) {
                    _emergencySoundUiMutableState.value = EmergencySoundUIState.Idle
                } else {
                    _emergencySoundUiMutableState.value = EmergencySoundUIState.NotSupported
                }
            }
        }
    }

    fun playEmergencySound() {
        require(_emergencySoundUiMutableState.value == EmergencySoundUIState.Idle) {
            "Emergency sound can only be played from Idle state."
        }
        viewModelScope.launch {
            soundAlertModule.playAlertSound()
                .onError {

                }
        }
    }

    fun stopEmergencySound() {
        require(_emergencySoundUiMutableState.value == EmergencySoundUIState.Playing) {
            "Emergency sound can only be stopped from Playing state."
        }
        viewModelScope.launch {
            soundAlertModule.stopAlertSound()
                .onError {

                }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val soundAlertModule = witnessAppContainer().soundAlertModule
                EmergencySoundViewModel(
                    soundAlertModule = soundAlertModule,
                )
            }
        }
    }
}

sealed interface EmergencySoundUIState {
    object Loading : EmergencySoundUIState
    object NotSupported : EmergencySoundUIState
    object Idle : EmergencySoundUIState
    object Playing : EmergencySoundUIState
    object Error : EmergencySoundUIState

    val isPlaying
        get() = this is Playing
}
