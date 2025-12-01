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

    private lateinit var emergencyRecordingTimeWindow: LongArray
    private lateinit var emergencyAlertSoundTimeWindow: LongArray
    private var emergencyRecordingTimeWindowIndex = 0
    private var emergencyAlertSoundTimeWindowIndex = 0

    override fun onCreate() {
        super.onCreate()
        emergencyRecordingTimeWindow = longArrayOf(0, 0, 0)
        emergencyAlertSoundTimeWindow = longArrayOf(0, 0, 0)
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
                    emergencyRecordingTimeWindowIndex = (emergencyRecordingTimeWindowIndex + 1) % 3
                    emergencyRecordingTimeWindow[emergencyRecordingTimeWindowIndex] = event.eventTime
                    // Checks for 3 presses within 2 seconds
                    if (emergencyRecordingTimeWindow[2] - emergencyRecordingTimeWindow[0] <= 2.seconds.inWholeMilliseconds) {
                        emergencyRecordingTimeWindowIndex = 0
                        emergencyRecordingTimeWindow = longArrayOf(0, 0, 0) // Reset time window
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
                    Log.d(TAG, "Volume Down pressed at ${event.eventTime}")
                    emergencyAlertSoundTimeWindowIndex = (emergencyAlertSoundTimeWindowIndex + 1) % 3
                    emergencyAlertSoundTimeWindow[emergencyAlertSoundTimeWindowIndex] = event.eventTime
                    // Checks for 3 presses within 2 seconds
                    if (emergencyAlertSoundTimeWindow[2] - emergencyAlertSoundTimeWindow[0] <= 2.seconds.inWholeMilliseconds) {
                        emergencyAlertSoundTimeWindowIndex = 0
                        emergencyAlertSoundTimeWindow = longArrayOf(0, 0, 0) // Reset time window
                        // Launch MainActivity with emergency info
                        val activityIntent =
                            Intent(this.applicationContext, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                putExtra("IS_EMERGENCY", true)
                            }
                        this.startActivity(activityIntent)
                        // Trigger emergency alert sound
                        (application as WitnessApp).appContainer.soundAlertModule.playAlertSound()
                    }
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