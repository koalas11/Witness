package org.wdsl.witness.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.wdsl.witness.PlatformContext
import org.wdsl.witness.model.NotificationType

/**
 * An interface defining various UI actions that can be performed in the application.
 */
interface FastUIActions {
    /**
     * Opens the accessibility services settings screen.
     *
     * @param platformContext The platform-specific context.
     */
    fun openAccessibilityServicesSettings(platformContext: PlatformContext)

    /**
     * Opens the system application settings screen.
     *
     * @param platformContext The platform-specific context.
     */
    fun openSystemAppSettings(platformContext: PlatformContext)

    /**
     * Forces the screen orientation to the specified value.
     *
     * @param orientation The desired screen orientation.
     */
    @Composable
    fun ForceScreenOrientation(
        orientation: Int,
    )

    /**
     * Checks the status of a specific permission.
     *
     * @param platformContext The platform-specific context.
     * @param permission The permission to check.
     */
    fun checkPermissionsStatus(platformContext: PlatformContext, permission: String): Boolean

    /**
     * Displays a dialog requesting location permission from the user.
     *
     * @param modifier The modifier to be applied to the dialog.
     * @param visible Whether the dialog is visible.
     * @param onDismiss The callback to be invoked when the dialog is dismissed.
     * @param onGranted The callback to be invoked when the permission is granted.
     */
    @Composable
    fun LocationPermissionRequestDialog(
        modifier: Modifier = Modifier,
        visible: Boolean,
        onDismiss: () -> Unit,
        onGranted: () -> Unit
    )

    /**
     * Displays a dialog requesting record audio permission from the user.
     *
     * @param modifier The modifier to be applied to the dialog.
     * @param visible Whether the dialog is visible.
     * @param onDismiss The callback to be invoked when the dialog is dismissed.
     * @param onGranted The callback to be invoked when the permission is granted.
     */
    @Composable
    fun RecordAudioPermissionRequestDialog(
        modifier: Modifier = Modifier,
        visible: Boolean,
        onDismiss: () -> Unit,
        onGranted: () -> Unit
    )

    /**
     * Shows a biometric authentication prompt to the user.
     *
     * @param onSuccess The callback to be invoked upon successful authentication.
     * @param onError The callback to be invoked upon authentication error, with an error message.
     */
    @Composable
    fun ShowBiometricPrompt(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    /**
     * Displays a notification to the user.
     *
     * @param message The message to be displayed.
     * @param notificationType The type of notification.
     */
    @Composable
    fun DisplayNotification(message: String, notificationType: NotificationType)

    /**
     * Displays a notification to the user with platform context.
     *
     * @param platformContext The platform-specific context.
     * @param message The message to be displayed.
     * @param notificationType The type of notification.
     */
    fun DisplayNotification(platformContext: PlatformContext, message: String, notificationType: NotificationType)
}

/**
 * Expect declaration for accessing FastUIActions implementation.
 */
expect val fastUIActions: FastUIActions
