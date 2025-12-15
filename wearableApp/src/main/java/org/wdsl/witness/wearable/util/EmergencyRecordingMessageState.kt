package org.wdsl.witness.wearable.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/***
 * Knows if the recording on the phone is currently active
 */
object EmergencyRecordingMessageState {
    private val _isEmergencyRecording = MutableStateFlow(false)
    val isEmergencyRecording = _isEmergencyRecording.asStateFlow()

    fun setIsEmergencyRecording(confirmed: Boolean) {
        _isEmergencyRecording.value = confirmed
    }
}