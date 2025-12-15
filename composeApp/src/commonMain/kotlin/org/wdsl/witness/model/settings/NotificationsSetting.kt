package org.wdsl.witness.model.settings

import org.jetbrains.compose.resources.StringResource
import witness.composeapp.generated.resources.Res
import witness.composeapp.generated.resources.all_notifications
import witness.composeapp.generated.resources.important_only

/**
 * Enumeration representing the notification settings available in the application.
 *
 * @property label A string resource representing the label for the notification setting.
 */
enum class NotificationsSetting(
    val label: StringResource,
) {
    IMPORTANT_ONLY(Res.string.important_only),
    ALL_NOTIFICATIONS(Res.string.all_notifications),
}
