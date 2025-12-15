package org.wdsl.witness.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton object to manage the state of the emergency service.
 */
object EmergencyServiceState {
    private var _emergencyServiceMutableState: MutableStateFlow<State> = MutableStateFlow(State.Idle)

    /**
     * StateFlow representing the current state of the emergency service.
     */
    val emergencyServiceState: StateFlow<State> = _emergencyServiceMutableState.asStateFlow()

    /**
     * Updates the state of the emergency service.
     *
     * @param state The new state to set.
     */
    fun setEmergencyServiceState(state: State) {
        _emergencyServiceMutableState.value = state
    }

    /**
     * Sealed interface representing the possible states of the emergency service.
     */
    sealed interface State {
        object Idle : State
        object Running : State
        data class Error(val message: String) : State
    }
}
