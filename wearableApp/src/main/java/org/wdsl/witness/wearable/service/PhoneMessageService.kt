package org.wdsl.witness.wearable.service

import android.os.VibrationEffect
import android.util.Log
import android.widget.Toast
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import org.wdsl.witness.wearable.util.EmergencyRecordingMessageState
import org.wdsl.witness.wearable.util.VibrationUtil
import org.wdsl.witness.shared.WearableMessageConstants
import org.wdsl.witness.wearable.R

class PhoneMessageService: WearableListenerService() {
    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        Log.d(
            "WearMessageService",
            "Message received from phone with path: ${messageEvent.path}"
        )

        when(messageEvent.path) {

            WearableMessageConstants.HELP_CONFIRMATION_PATH -> {
                VibrationUtil.vibrate(this, 1000)
                EmergencyRecordingMessageState.setIsEmergencyRecording(true)
                Toast.makeText(this, R.string.help_confirmation_message, Toast.LENGTH_LONG).show()
            }

            WearableMessageConstants.HELP_STOP_PATH -> {
                VibrationUtil.vibrate(this, 500)
                EmergencyRecordingMessageState.setIsEmergencyRecording(false)
                Toast.makeText(this, R.string.help_stop_message, Toast.LENGTH_LONG).show()
            }

            WearableMessageConstants.WHISTLE_CONFIRMATION_PATH -> {
                val waveTimings = longArrayOf(0, 100, 50, 100, 50, 100, 50, 100, 50, 100)
                val waveAmplitudes = intArrayOf(0, 255, 0, 255, 0, 255, 0, 255, 0, 255)
                val waveEffect = VibrationEffect.createWaveform(waveTimings, waveAmplitudes, -1)

                VibrationUtil.vibrate(this, waveEffect)
            }
        }
    }
}