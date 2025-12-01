package org.wdsl.witness.ui.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import org.wdsl.witness.PlatformContext
import org.wdsl.witness.model.NotificationType

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

    @Composable
    override fun ForceScreenOrientation(orientation: Int) {
        val activity = LocalActivity.current

        DisposableEffect(Unit) {
            val originalOrientation = activity!!.requestedOrientation
            activity.requestedOrientation = orientation

            onDispose {
                activity.requestedOrientation = originalOrientation
            }
        }
    }

    override fun checkPermissionsStatus(platformContext: PlatformContext, permission: String): Boolean {
        val context = platformContext.context as Context
        return ContextCompat.checkSelfPermission(context, permission) ==
                    PackageManager.PERMISSION_GRANTED

    }

    @Composable
    override fun LocationPermissionRequestDialog(
        modifier: Modifier,
        visible: Boolean,
        onDismiss: () -> Unit,
        onGranted: () -> Unit
    ) {
        if (!visible) return

        val context = LocalContext.current
        val launcher = rememberLauncherForActivityResult(
            RequestPermission()
        ) { granted ->
            if (granted) {
                onGranted()
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    modifier = modifier,
                    text = "Location Permission",
                )
            },
            text = {
                Text(
                    modifier = modifier,
                    text = "The App require this permission",
                )
            },
            confirmButton = {
                Button(
                    modifier = modifier,
                    onClick = {
                        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        onDismiss()
                    },
                ) {
                    Text(
                        modifier = modifier,
                        text = "Grant it",
                    )
                }
            },
            dismissButton = {
                Button(
                    modifier = modifier,
                    onClick = {
                        onDismiss()
                    },
                ) {
                    Text(
                        modifier = modifier,
                        text = "Cancel",
                    )
                }
            }
        )
    }

    @Composable
    override fun RecordAudioPermissionRequestDialog(
        modifier: Modifier,
        visible: Boolean,
        onDismiss: () -> Unit,
        onGranted: () -> Unit
    ) {
        if (!visible) return

        val context = LocalContext.current
        val launcher = rememberLauncherForActivityResult(
            RequestPermission()
        ) { granted ->
            if (granted) {
                onGranted()
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    modifier = modifier,
                    text = "Location Permission",
                )
            },
            text = {
                Text(
                    modifier = modifier,
                    text = "The App require this permission",
                )
            },
            confirmButton = {
                Button(
                    modifier = modifier,
                    onClick = {
                        launcher.launch(Manifest.permission.RECORD_AUDIO)
                        onDismiss()
                    },
                ) {
                    Text(
                        modifier = modifier,
                        text = "Grant it",
                    )
                }
            },
            dismissButton = {
                Button(
                    modifier = modifier,
                    onClick = {
                        onDismiss()
                    },
                ) {
                    Text(
                        modifier = modifier,
                        text = "Cancel",
                    )
                }
            }
        )
    }

    @Composable
    override fun ShowBiometricPrompt(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val context = LocalContext.current
        val activity = LocalActivity.current as? FragmentActivity ?: return
        LifecycleEventEffect(
            event = Lifecycle.Event.ON_RESUME
        ) {
            if (BiometricManager.from(context)
                    .canAuthenticate(
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                                BiometricManager.Authenticators.DEVICE_CREDENTIAL or
                                BiometricManager.Authenticators.BIOMETRIC_WEAK
                    )
                != BiometricManager.BIOMETRIC_SUCCESS
            ) {
                    Toast.makeText(context, "Biometric authentication is not available, skipping.", Toast.LENGTH_LONG).show()
                    onSuccess()
                    return@LifecycleEventEffect
            }

            val executor = ContextCompat.getMainExecutor(context)

            val callback =
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        onSuccess()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        onError("Authentication error: $errString")
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        onError("Authentication failed")
                    }
                }

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authentication")
                .setSubtitle("Please authenticate to proceed")
                .setNegativeButtonText("Cancel")
                .build()

            val biometricPrompt = BiometricPrompt(activity, executor, callback)
            biometricPrompt.authenticate(promptInfo)
        }
    }

    @Composable
    override fun DisplayNotification(message: String, notificationType: NotificationType) {
        val context = LocalContext.current
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun DisplayNotification(
        platformContext: PlatformContext,
        message: String,
        notificationType: NotificationType
    ) {
        val context = platformContext.context as Context
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

actual val fastUIActions: FastUIActions = AndroidFastUIActions
