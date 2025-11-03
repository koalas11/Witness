package org.wdsl.witness.model

import org.jetbrains.compose.resources.StringResource
import witness.composeapp.generated.resources.Res
import witness.composeapp.generated.resources.all_notifications
import witness.composeapp.generated.resources.important_only

enum class NotificationsSetting(
    val label: StringResource,
) {
    IMPORTANT_ONLY(Res.string.important_only),
    ALL_NOTIFICATIONS(Res.string.all_notifications),
}
