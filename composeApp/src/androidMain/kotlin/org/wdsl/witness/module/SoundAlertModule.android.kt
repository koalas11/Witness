package org.wdsl.witness.module

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.Result
import org.wdsl.witness.util.ResultError

class AndroidSoundAlertModule(
    private val context: Context
): SoundAlertModule {
    private var toneGenerator: ToneGenerator? = null

    override fun isSoundAlertSupported(): Result<Boolean> {
        return try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                ?: return Result.Error(ResultError.UnknownError("Sound alert not supported: AudioManager not available"))

            val isSupported = !audioManager.isVolumeFixed &&
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM) > 0
            Result.Success(isSupported)
        } catch (e: Exception) {
            Log.e(TAG, "isSoundAlertSupported: Unknown error", e)
            Result.Error(ResultError.UnknownError("isSoundAlertSupported: ${e.message}"))
        }
    }

    override fun playAlertSound(): Result<Unit> {
        return try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            audioManager.setStreamVolume(
                AudioManager.STREAM_ALARM,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM),
                AudioManager.FLAG_PLAY_SOUND
            )

            toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
            toneGenerator!!.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD)
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "playAlertSound: Failed to start alert sound", e)
            Result.Error(ResultError.UnknownError("Failed to start alert sound: ${e.message}"))
        }
    }

    override fun stopAlertSound(): Result<Unit> {
        return try {
            toneGenerator?.stopTone()
            toneGenerator?.release()
            toneGenerator = null
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "stopAlertSound: Failed to stop alert sound", e)
            Result.Error(ResultError.UnknownError("Failed to stop alert sound: ${e.message}"))
        }
    }

    companion object {
        private const val TAG = "Sound Alert Module"
    }
}
