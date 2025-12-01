package org.wdsl.witness.model

data class EmergencyRecordingSettings(
    val enableVibrationOnEmergencyRegistrationStart: Boolean = true,
    val enableSmsOnEmergency: Boolean = true,
    val enableEmailOnEmergency: Boolean = true,
)