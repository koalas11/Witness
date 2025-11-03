package org.wdsl.witness.module

import androidx.compose.runtime.Composable
import org.wdsl.witness.model.NotificationType

object IosNotificationHandler : NotificationHandler {

    @Composable
    override fun displayNotification(message: String, notificationType: NotificationType) {
        TODO("Not yet implemented")
    }
}

@Composable
actual fun getNotificationHandler(): NotificationHandler = IosNotificationHandler
