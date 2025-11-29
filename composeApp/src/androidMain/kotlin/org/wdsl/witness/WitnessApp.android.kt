package org.wdsl.witness

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.wdsl.witness.broadcastreceiver.AppSettingsChangedBroadcastReceiver
import org.wdsl.witness.model.EmergencyGesturesStatus
import org.wdsl.witness.service.EmergencyGesturesAccessibilityService
import org.wdsl.witness.state.AppSettingsState
import org.wdsl.witness.util.EMERGENCY_NOTIFICATION_CHANNEL_ID
import org.wdsl.witness.util.ERROR_NOTIFICATION_CHANNEL_ID

/**
 * Android-specific implementation of the WitnessApp.
 */
class WitnessAppAndroid: WitnessApp, Application() {
    override lateinit var appContainer: PlatformAppContainer

    override val appScope: CoroutineScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        appContainer = AndroidAppContainer(this)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    EMERGENCY_NOTIFICATION_CHANNEL_ID,
                    "Emergency Recording",
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description = "Notifications for emergency recording status"
                }
            )
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    ERROR_NOTIFICATION_CHANNEL_ID,
                    "Error Notifications",
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description = "Notifications for application errors"
                }
            )
        }

        val appSettingsChangedBroadcastReceiver = AppSettingsChangedBroadcastReceiver()
        ContextCompat.registerReceiver(
            this,
            appSettingsChangedBroadcastReceiver,
            IntentFilter(
                Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED,
            ),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        updateAccessibilityServiceStatus()

        val accessibilityManager =
            this.getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        accessibilityManager.addAccessibilityStateChangeListener {
            Log.d(TAG, "Accessibility state changed")
            updateAccessibilityServiceStatus()
        }
    }

    private fun updateAccessibilityServiceStatus() {
        val am = this.getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices =
            am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        var status = EmergencyGesturesStatus.DISABLED
        for (serviceInfo in enabledServices) {
            if (serviceInfo.resolveInfo.serviceInfo.packageName == this.packageName
                && serviceInfo.resolveInfo.serviceInfo.name == EmergencyGesturesAccessibilityService::class.java.canonicalName
            ) {
                status = EmergencyGesturesStatus.ENABLED
                break
            }
        }
        AppSettingsState.setAccessibilityServiceEnabled(status)
    }

    companion object {
        const val TAG = "WitnessAppAndroid"
    }
}
