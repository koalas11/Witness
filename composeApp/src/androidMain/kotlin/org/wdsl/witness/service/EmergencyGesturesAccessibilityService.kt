package org.wdsl.witness.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import org.wdsl.witness.MainActivity
import org.wdsl.witness.WitnessApp
import kotlin.time.Duration.Companion.seconds

class EmergencyGesturesAccessibilityService : AccessibilityService() {

    private lateinit var timeWindow: LongArray
    private var timeWindowIndex = 0

    override fun onCreate() {
        super.onCreate()
        timeWindow = longArrayOf(0, 0, 0)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // Not used in this service

    }

    override fun onInterrupt() {
        // Called when the system interrupts your service (e.g. another service takes over)
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        when (event.keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                Log.d(TAG, "Volume Up key event: $event")
                if (event.action == KeyEvent.ACTION_DOWN) {
                    Log.d(TAG, "Volume Up pressed at ${event.eventTime}")
                    timeWindowIndex = (timeWindowIndex + 1) % 3
                    timeWindow[timeWindowIndex] = event.eventTime
                    // Checks for 3 presses within 2 seconds
                    if (timeWindow[2] - timeWindow[0] <= 2.seconds.inWholeMilliseconds) {
                        timeWindowIndex = 0
                        timeWindow = longArrayOf(0, 0, 0) // Reset time window
                        // Trigger emergency alert
                        // Launch MainActivity with emergency info
                        val activityIntent =
                            Intent(this.applicationContext, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            putExtra("IS_EMERGENCY", true)
                        }
                        this.startActivity(activityIntent)

                        (application as WitnessApp).appContainer.emergencyRecordingUseCase.startEmergencyRecording()
                    }
                }
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                if (event.action == KeyEvent.ACTION_DOWN) {
                    // React to Volume Down
                }
            }
        }
        // Return false to let the system still handle volume change
        return false
    }

    companion object {
        const val TAG = "EmergencyAlertForegroundService"
    }
}