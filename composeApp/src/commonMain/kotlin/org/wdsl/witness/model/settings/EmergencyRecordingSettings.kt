package org.wdsl.witness.model.settings

/**
 * Settings related to emergency recording features.
 *
 * @param enableVibrationOnEmergencyRegistrationStart Whether to enable vibration when emergency registration starts.
 * @param enableSmsOnEmergency Whether to send SMS notifications during emergencies.
 * @param enableEmailOnEmergency Whether to send email notifications during emergencies.
 */
data class EmergencyRecordingSettings(
    val enableVibrationOnEmergencyRegistrationStart: Boolean = true,
    val enableSmsOnEmergency: Boolean = true,
    val enableEmailOnEmergency: Boolean = true,
    val enableRoutineContactContacts: Boolean = false,
)
