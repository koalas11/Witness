package org.wdsl.witness

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.accessibility.AccessibilityManager
import androidx.core.content.ContextCompat
import org.wdsl.witness.broadcastreceiver.AppSettingsChangedBroadcastReceiver
import org.wdsl.witness.model.EmergencyGesturesStatus
import org.wdsl.witness.service.EmergencyGesturesAccessibilityService
import org.wdsl.witness.state.AppSettingsState

/**
 * Android-specific implementation of the WitnessApp.
 */
class WitnessAppAndroid: WitnessApp, Application() {
    override lateinit var appContainer: PlatformAppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AndroidAppContainer(this)

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
