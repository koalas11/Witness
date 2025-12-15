package org.wdsl.witness.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import org.wdsl.witness.MainActivity
import org.wdsl.witness.WitnessApp
import org.wdsl.witness.WitnessBuildConfig
import org.wdsl.witness.state.EmergencyServiceState
import org.wdsl.witness.state.EmergencySoundState

const val ACTION_WITNESS_EMERGENCY = "${WitnessBuildConfig.PACKAGE_NAME}.action.EMERGENCY"
const val START_RECORDING_URI = "content://${WitnessBuildConfig.PACKAGE_NAME}/startRecording"
const val START_EMERGENCY_ALERT_URI = "content://${WitnessBuildConfig.PACKAGE_NAME}/startEmergencyAlert"

class EmergencyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d(TAG, "Received intent with action: $action")
        if (action !in listOf(ACTION_WITNESS_EMERGENCY)) {
            return
        }

        if (intent.data == null) {
            Log.d(TAG, "No data URI found in intent, ignoring request.")
            return
        }
        when (intent.data) {
            START_RECORDING_URI.toUri() -> {
                if (EmergencyServiceState.emergencyServiceState.value == EmergencyServiceState.State.Running) {
                    Log.d(TAG, "Emergency recording already in progress, ignoring request.")
                    return
                }
                Log.d(TAG, "Starting emergency recording.")
                (context.applicationContext as WitnessApp).appContainer.emergencyRecordingUseCase.startEmergencyRecording()
            }
            START_EMERGENCY_ALERT_URI.toUri() -> {
                if (EmergencySoundState.emergencySoundState.value == EmergencySoundState.State.Playing) {
                    Log.d(TAG, "Emergency alert sound already playing, ignoring request.")
                    return
                }
                Log.d(TAG, "Starting emergency alert sound.")
                (context.applicationContext as WitnessApp).appContainer.soundAlertModule.playAlertSound()
            }
            else -> {
                Log.d(TAG, "Unrecognized data URI in intent, ignoring request.")
                return
            }
        }

        val activityIntent =
            Intent(context.applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        context.startActivity(activityIntent)
    }

    companion object {
        const val TAG = "EmergencyRecordingBR"
    }
}
