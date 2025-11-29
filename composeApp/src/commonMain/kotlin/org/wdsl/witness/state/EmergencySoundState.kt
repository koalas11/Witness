package org.wdsl.witness.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object EmergencySoundState {
    private var _emergencySoundMutableState: MutableStateFlow<State> = MutableStateFlow(
        State.Idle)
    val emergencySoundState: StateFlow<State> = _emergencySoundMutableState.asStateFlow()

    fun setEmergencySoundState(state: State) {
        _emergencySoundMutableState.value = state
    }

    sealed interface State {
        object Idle : State
        object Playing : State
        object Error : State

        val isPlaying
            get() = this is Playing
    }
}