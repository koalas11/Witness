package org.wdsl.witness.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import org.wdsl.witness.PlatformContext

object AndroidFastUIActions: FastUIActions {
    override fun openAccessibilityServicesSettings(platformContext: PlatformContext) {
        val context = platformContext.context as Context
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    override fun openSystemAppSettings(platformContext: PlatformContext) {
        val context = platformContext.context as Context
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }
}

actual val fastUIActions: FastUIActions = AndroidFastUIActions
