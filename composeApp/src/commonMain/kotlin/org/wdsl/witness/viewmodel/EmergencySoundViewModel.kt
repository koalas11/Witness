package org.wdsl.witness.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import org.wdsl.witness.module.SoundAlertModule
import org.wdsl.witness.state.EmergencySoundState

/**
 * ViewModel for managing emergency sound operations.
 *
 * @property soundAlertModule The module responsible for sound alert functionalities.
 */
class EmergencySoundViewModel(
    private val soundAlertModule: SoundAlertModule,
) : BaseOperationViewModel() {

    fun playEmergencySound() {
        require(EmergencySoundState.emergencySoundState.value is EmergencySoundState.State.Idle) {
            "Cannot play emergency sound when another operation is in progress."
        }
        viewModelScope.launch {
            soundAlertModule.playAlertSound()
                .onError {

                }
        }
    }

    fun stopEmergencySound() {
        require(EmergencySoundState.emergencySoundState.value is EmergencySoundState.State.Playing) {
            "Cannot stop emergency sound when it is not playing."
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
