package org.wdsl.witness.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.wdsl.witness.model.EmergencyGesturesStatus

object AppSettingsState {
    private var _settingsChanged: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val settingsChanged: StateFlow<Boolean> = _settingsChanged.asStateFlow()

    private var _accessibilityServiceEnabled: MutableStateFlow<EmergencyGesturesStatus> =
        MutableStateFlow(EmergencyGesturesStatus.NOT_SUPPORTED)
    val accessibilityServiceEnabled: StateFlow<EmergencyGesturesStatus> =
        _accessibilityServiceEnabled.asStateFlow()

    fun notifySettingsChanged() {
        _settingsChanged.value = !_settingsChanged.value
    }

    fun setAccessibilityServiceEnabled(emergencyGesturesStatus: EmergencyGesturesStatus) {
        _accessibilityServiceEnabled.value = emergencyGesturesStatus
    }
}
