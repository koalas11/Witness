package org.wdsl.witness.module

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import org.wdsl.witness.model.NotificationType

object AndroidNotificationHandler: NotificationHandler {

    @Composable
    override fun displayNotification(message: String, notificationType: NotificationType) {
        val context = LocalContext.current
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

/**
 * Remember the notification handler
 */
@Composable
actual fun getNotificationHandler() : NotificationHandler = AndroidNotificationHandler
