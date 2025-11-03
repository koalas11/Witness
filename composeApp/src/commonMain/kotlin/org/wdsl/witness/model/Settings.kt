package org.wdsl.witness.model

import kotlinx.serialization.Serializable

/**
 * Data class representing user settings.
 */
@Serializable
data class Settings(
    var tutorialDone: Boolean = false,
    var dynamicColorMode: DynamicColorMode = DynamicColorMode.ENABLED,
    var themeMode: ThemeMode = ThemeMode.SYSTEM_DEFAULT,
    var notificationsSetting: NotificationsSetting = NotificationsSetting.ALL_NOTIFICATIONS,
)
