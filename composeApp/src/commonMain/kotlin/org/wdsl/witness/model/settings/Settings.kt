package org.wdsl.witness.model.settings

import kotlinx.serialization.Serializable

/**
 * Data class representing user settings.
 *
 * @param tutorialDone Indicates if the tutorial has been completed.
 * @param dynamicColorMode The dynamic color mode setting.
 * @param themeMode The theme mode setting.
 * @param notificationsSetting The notifications setting.
 * @param enableVibrationOnEmergencyRegistrationStart Whether to enable vibration on emergency registration start.
 * @param enableSmsOnEmergency Whether to enable SMS notifications on emergency.
 * @param enableEmailOnEmergency Whether to enable email notifications on emergency.
 */
@Serializable
data class Settings(
    val tutorialDone: Boolean = false,
    val dynamicColorMode: DynamicColorMode = DynamicColorMode.ENABLED,
    val themeMode: ThemeMode = ThemeMode.SYSTEM_DEFAULT,
    val notificationsSetting: NotificationsSetting = NotificationsSetting.ALL_NOTIFICATIONS,
    val enableVibrationOnEmergencyRegistrationStart: Boolean = true,
    val enableSmsOnEmergency: Boolean = true,
    val enableEmailOnEmergency: Boolean = true,
    val enableRoutineContactContacts: Boolean = true,
    val uploadRecordingToDriveOnEnd: Boolean = false,
)
