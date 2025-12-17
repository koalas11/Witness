package org.wdsl.witness.ui.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import org.wdsl.witness.PlatformContext
import org.wdsl.witness.model.NotificationType
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.Permission

/**
 * Android implementation of FastUIActions.
 */
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
    override fun PermissionRequestDialog(
        modifier: Modifier,
        permission: Permission,
    ) {
        val context = LocalContext.current
        val activity = LocalActivity.current
        var showRationale by remember { mutableStateOf(false) }

        val launcher = rememberLauncherForActivityResult(RequestPermission()) { granted ->
            if (granted) {
                Toast.makeText(context, "${permission.name} permission granted", Toast.LENGTH_SHORT).show()
            } else {
                val shouldShow = try {
                    activity?.let { ActivityCompat.shouldShowRequestPermissionRationale(it, permission.id) } ?: true
                } catch (_: Throwable) {
                    true
                }

                if (!shouldShow) {
                    Toast.makeText(context, "$${permission.name} permission permanently denied. Open app settings to enable.", Toast.LENGTH_LONG).show()
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    } catch (_: Exception) { /* ignore */ }
                } else {
                    Toast.makeText(context, "${permission.name} permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }

        Button(
            onClick = {
                showRationale = true
            }
        ) {
            Text(text = "Grant")
        }
        if (showRationale) {
            AlertDialog(
                onDismissRequest = {
                    showRationale = false
                },
                confirmButton = {
                    Button(onClick = {
                        showRationale = false
                        launcher.launch(permission.id)
                    }) {
                        Text(text = "Allow")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showRationale = false
                    }) {
                        Text(text = "Deny")
                    }
                },
                title = { Text(text = "${permission.name} permission") },
                text = {
                    Text(
                        modifier = modifier.padding(8.dp),
                        text = permission.rationate
                    )
                }
            )
        }
    }

    @Composable
    override fun ShowBiometricPrompt(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val context = LocalContext.current
        val activity = LocalActivity.current as FragmentActivity

        LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
            val bm = BiometricManager.from(context)
            val pm = context.packageManager

            val canStrong = try {
                bm.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
            } catch (_: Throwable) { false }

            val canWeak = try {
                bm.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS
            } catch (_: Throwable) { false }

            val canDevice = try {
                bm.canAuthenticate(BiometricManager.Authenticators.DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS
            } catch (_: Throwable) { false }

            val hasFingerprintHardware = try {
                pm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
            } catch (_: Throwable) { false }

            var authenticators = 0
            if (canStrong) authenticators = authenticators or BiometricManager.Authenticators.BIOMETRIC_STRONG
            if (canWeak && hasFingerprintHardware) authenticators = authenticators or BiometricManager.Authenticators.BIOMETRIC_WEAK
            if (canDevice) authenticators = authenticators or BiometricManager.Authenticators.DEVICE_CREDENTIAL

            if (authenticators == 0) {
                Toast.makeText(context, "Biometric authentication is not available, skipping.", Toast.LENGTH_LONG).show()
                onSuccess()
                return@LifecycleEventEffect
            }

            val executor = ContextCompat.getMainExecutor(context)
            val callback = object : BiometricPrompt.AuthenticationCallback() {
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

            val promptBuilder = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authentication")
                .setSubtitle("Please authenticate to proceed")

            try {
                promptBuilder.setAllowedAuthenticators(authenticators)
                if (authenticators and BiometricManager.Authenticators.DEVICE_CREDENTIAL == 0) {
                    promptBuilder.setNegativeButtonText("Cancel")
                }
            } catch (t: Throwable) {
                try { promptBuilder.setNegativeButtonText("Cancel") } catch (_: Throwable) { /* ignore */ }
            }

            val promptInfo = promptBuilder.build()
            val biometricPrompt = BiometricPrompt(activity, executor, callback)
            biometricPrompt.authenticate(promptInfo)
        }
    }

    @Composable
    override fun SelectPhoneContact(
        onContactSelected: (String, String) -> Unit,
        onError: (String) -> Unit
    ) {
        val context = LocalContext.current
        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.PickContact()
        ) { uri ->
            if (uri == null) {
                onError("No contact selected")
                return@rememberLauncherForActivityResult
            }

            try {
                val resolver = context.contentResolver
                val contactId = uri.lastPathSegment
                if (contactId.isNullOrEmpty()) {
                    onError("Invalid contact")
                    return@rememberLauncherForActivityResult
                }

                val phoneCursor = resolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    ),
                    "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID}=?",
                    arrayOf(contactId),
                    null
                )

                phoneCursor?.use { pc ->
                    if (pc.moveToFirst()) {
                        val name = pc.getString(pc.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                        val number = pc.getString(pc.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        onContactSelected(name, number)
                    } else {
                        onError("Phone number not found for contact")
                    }
                } ?: onError("Failed to query contact")
            } catch (se: SecurityException) {
                Log.e(TAG, "SelectPhoneContact: Permission denied", se)
                onError("Permission denied to read contacts")
            } catch (e: Exception) {
                Log.e(TAG, "SelectPhoneContact: Error reading contact", e)
                onError("Error reading contact")
            }
        }

        var launched by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            if (!launched) {
                launcher.launch(null)
                launched = true
            }
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

    const val TAG: String = "AndroidFastUIActions"
}

/**
 * Actual instance of FastUIActions for Android.
 */
actual val fastUIActions: FastUIActions = AndroidFastUIActions
