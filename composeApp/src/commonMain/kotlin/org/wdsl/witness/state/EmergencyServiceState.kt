package org.wdsl.witness.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object EmergencyServiceState {
    private var _emergencyServiceMutableState: MutableStateFlow<State> = MutableStateFlow(State.Idle)
    val emergencyServiceState: StateFlow<State> = _emergencyServiceMutableState.asStateFlow()

    fun setEmergencyServiceState(state: State) {
        _emergencyServiceMutableState.value = state
    }

    sealed interface State {
        object Idle : State
        object Running : State
        data class Error(val message: String) : State
    }
}
