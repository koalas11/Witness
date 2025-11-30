package org.wdsl.witness.model

import kotlinx.serialization.Serializable

/**
 * Data class representing user settings.
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
)
