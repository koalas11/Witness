package org.wdsl.witness.module

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator

class AndroidSoundAlertModule(
    private val context: Context
): SoundAlertModule {
    private var toneGenerator: ToneGenerator? = null

    override fun playAlertSound() {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        audioManager.setStreamVolume(
            AudioManager.STREAM_ALARM,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM),
            AudioManager.FLAG_PLAY_SOUND)

        toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
        toneGenerator!!.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD)
    }

    override fun stopAlertSound() {
        toneGenerator?.stopTone()
        toneGenerator?.release()
        toneGenerator = null
    }
}
