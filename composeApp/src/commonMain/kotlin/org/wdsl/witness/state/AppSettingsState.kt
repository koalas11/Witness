package org.wdsl.witness.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.wdsl.witness.model.EmergencyGesturesStatus

/**
 * Singleton object to manage application settings state.
 */
object AppSettingsState {
    private var _settingsChanged: MutableStateFlow<Boolean> = MutableStateFlow(false)

    /**
     * StateFlow to observe changes in settings.
     */
    val settingsChanged: StateFlow<Boolean> = _settingsChanged.asStateFlow()

    private var _accessibilityServiceEnabled: MutableStateFlow<EmergencyGesturesStatus> =
        MutableStateFlow(EmergencyGesturesStatus.NOT_SUPPORTED)

    /**
     * StateFlow to observe the status of the accessibility service.
     */
    val accessibilityServiceEnabled: StateFlow<EmergencyGesturesStatus> =
        _accessibilityServiceEnabled.asStateFlow()

    /**
     * Notify observers that settings have changed by toggling the boolean value.
     */
    fun notifySettingsChanged() {
        _settingsChanged.value = !_settingsChanged.value
    }

    /**
     * Update the status of the accessibility service.
     *
     * @param emergencyGesturesStatus The new status of the accessibility service.
     */
    fun setAccessibilityServiceEnabled(emergencyGesturesStatus: EmergencyGesturesStatus) {
        _accessibilityServiceEnabled.value = emergencyGesturesStatus
    }
}
