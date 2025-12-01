package org.wdsl.witness.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.wdsl.witness.PlatformContext
import org.wdsl.witness.model.NotificationType

interface FastUIActions {
    fun openAccessibilityServicesSettings(platformContext: PlatformContext)

    fun openSystemAppSettings(platformContext: PlatformContext)

    @Composable
    fun ForceScreenOrientation(
        orientation: Int,
    )

    fun checkPermissionsStatus(platformContext: PlatformContext, permission: String): Boolean

    @Composable
    fun LocationPermissionRequestDialog(
        modifier: Modifier = Modifier,
        visible: Boolean,
        onDismiss: () -> Unit,
        onGranted: () -> Unit
    )

    @Composable
    fun RecordAudioPermissionRequestDialog(
        modifier: Modifier = Modifier,
        visible: Boolean,
        onDismiss: () -> Unit,
        onGranted: () -> Unit
    )

    @Composable
    fun ShowBiometricPrompt(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    @Composable
    fun DisplayNotification(message: String, notificationType: NotificationType)

    fun DisplayNotification(platformContext: PlatformContext, message: String, notificationType: NotificationType)
}

expect val fastUIActions: FastUIActions
