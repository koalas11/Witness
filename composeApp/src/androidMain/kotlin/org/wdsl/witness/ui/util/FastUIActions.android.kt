package org.wdsl.witness.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
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
