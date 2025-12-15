package org.wdsl.witness.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Object to manage the state of emergency sound playback.
 */
object EmergencySoundState {
    private var _emergencySoundMutableState: MutableStateFlow<State> = MutableStateFlow(
        State.Idle)

    /**
     * StateFlow representing the current state of emergency sound playback.
     */
    val emergencySoundState: StateFlow<State> = _emergencySoundMutableState.asStateFlow()

    /**
     * Sets the current state of emergency sound playback.
     *
     * @param state The new state to set.
     */
    fun setEmergencySoundState(state: State) {
        _emergencySoundMutableState.value = state
    }

    /**
     * Sealed interface representing the possible states of emergency sound playback.
     */
    sealed interface State {
        object Idle : State
        object Playing : State
        object Error : State

        val isPlaying
            get() = this is Playing
    }
}
