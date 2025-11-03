package org.wdsl.witness.module

import androidx.compose.runtime.Composable
import org.wdsl.witness.model.NotificationType

interface NotificationHandler {
    @Composable
    fun displayNotification(message: String, notificationType: NotificationType)
}

/**
 * Remember the notification handler
 */
@Composable
expect fun getNotificationHandler() : NotificationHandler
