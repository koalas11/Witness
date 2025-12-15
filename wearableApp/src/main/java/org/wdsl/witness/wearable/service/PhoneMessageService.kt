package org.wdsl.witness.wearable.service

import android.os.VibrationEffect
import android.util.Log
import android.widget.Toast
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import org.wdsl.witness.shared.WearableMessageConstants
import org.wdsl.witness.wearable.R
import org.wdsl.witness.wearable.util.EmergencyRecordingMessageState
import org.wdsl.witness.wearable.util.VibrationUtil

/**
 * Service responsible for handling messages received
 * from the phone
 */
class PhoneMessageService: WearableListenerService() {
    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        Log.d(
            "WearMessageService",
            "Message received from phone with path: ${messageEvent.path}"
        )

        when(messageEvent.path) {

            // Triggered when emergency recording has been
            // successfully started (both from phone and wearable)
            // on the phone
            WearableMessageConstants.HELP_CONFIRMATION_PATH -> {
                VibrationUtil.vibrate(this, 1000)
                EmergencyRecordingMessageState.setIsEmergencyRecording(true)
                Toast.makeText(this, R.string.help_confirmation_message, Toast.LENGTH_LONG).show()
            }

            // Triggered when emergency recording has been
            // stopped on the phone
            WearableMessageConstants.HELP_STOP_PATH -> {
                VibrationUtil.vibrate(this, 500)
                EmergencyRecordingMessageState.setIsEmergencyRecording(false)
                Toast.makeText(this, R.string.help_stop_message, Toast.LENGTH_LONG).show()
            }

            // Triggered when the whistle feature has been
            // successfully activated on the phone
            WearableMessageConstants.WHISTLE_CONFIRMATION_PATH -> {

                // Custom vibration pattern to distinguish
                // whistle activation from other events

                // Millisecond
                val waveTimings = longArrayOf(0, 100, 50, 100, 50, 100, 50, 100, 50, 100)

                // Intensity
                val waveAmplitudes = intArrayOf(0, 255, 0, 255, 0, 255, 0, 255, 0, 255)

                val waveEffect = VibrationEffect.createWaveform(waveTimings, waveAmplitudes, -1)

                VibrationUtil.vibrate(this, waveEffect)
            }
        }
    }
}