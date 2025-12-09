package org.wdsl.witness.wearable.service

import android.os.VibrationEffect
import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import org.wdsl.witness.wearable.util.VibrationUtil

class PhoneMessageService: WearableListenerService() {
    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        Log.d(
            "WearMessageService",
            "Message received from phone with path: ${messageEvent.path}"
        )

        when(messageEvent.path) {

            "/WitnessHelpConfirmationMessage" -> {
                VibrationUtil.vibrate(this, 1000)
            }

            "/WitnessWhistleConfirmationMessage" -> {
                val waveTimings = longArrayOf(0, 100, 50, 100, 50, 100, 50, 100, 50, 100)
                val waveAmplitudes = intArrayOf(0, 255, 0, 255, 0, 255, 0, 255, 0, 255)
                val waveEffect = VibrationEffect.createWaveform(waveTimings, waveAmplitudes, -1)

                VibrationUtil.vibrate(this, waveEffect)
            }
        }
    }
}