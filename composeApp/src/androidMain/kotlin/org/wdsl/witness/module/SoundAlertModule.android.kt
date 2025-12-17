package org.wdsl.witness.module

import android.content.Context
import android.media.AudioManager
import android.media.audiofx.LoudnessEnhancer
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import org.wdsl.witness.R
import org.wdsl.witness.state.EmergencySoundState
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.Result
import org.wdsl.witness.util.ResultError

/**
 * Android implementation of the SoundAlertModule using ExoPlayer.
 *
 * @param context The Android context.
 */
class AndroidSoundAlertModule(
    private val context: Context
): SoundAlertModule {
    private var player: ExoPlayer? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null

    @OptIn(UnstableApi::class)
    override fun playAlertSound(): Result<Unit> {
        return try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                ?: return Result.Error(ResultError.UnknownError("AudioManager not available"))

            val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, max, 0)

            stopAlertSound()

            player = ExoPlayer.Builder(context).build().apply {
                val uri = "android.resource://${context.packageName}/${R.raw.alarm}".toUri()
                val mediaItem = MediaItem.fromUri(uri)
                setMediaItem(mediaItem)

                val audioAttrs = AudioAttributes.Builder()
                    .setUsage(C.USAGE_ALARM)
                    .setContentType(C.AUDIO_CONTENT_TYPE_SONIFICATION)
                    .build()
                setAudioAttributes(audioAttrs, false)

                repeatMode = Player.REPEAT_MODE_ONE
                volume = 1.0f

                prepare()

                // Apply LoudnessEnhancer to increase perceived loudness beyond player volume.
                try {
                    val sessionId = audioSessionId
                    // targetGain in millibels (1000 mB = ~+10 dB). Increase with caution.
                    val targetGainMb = 1000
                    loudnessEnhancer = LoudnessEnhancer(sessionId).apply {
                        setTargetGain(targetGainMb)
                        setEnabled(true)
                    }
                } catch (le: Throwable) {
                    // Some devices / sessions may not support the effect; ignore and continue.
                    Log.w(TAG, "LoudnessEnhancer not available or failed to enable", le)
                    loudnessEnhancer = null
                }

                playWhenReady = true
            }
            EmergencySoundState.setEmergencySoundState(EmergencySoundState.State.Playing)

            Result.Success(Unit)
        } catch (e: Exception) {
            EmergencySoundState.setEmergencySoundState(EmergencySoundState.State.Error)
            Log.e(TAG, "playAlertSound: Failed to start alert sound", e)
            Result.Error(ResultError.UnknownError("Failed to start alert sound: ${e.message}"))
        }
    }

    override fun stopAlertSound(): Result<Unit> {
        return try {
            loudnessEnhancer?.let {
                try {
                    it.setEnabled(false)
                } catch (_: Exception) { /* ignore */ }
                it.release()
            }
            loudnessEnhancer = null

            player?.let {
                try {
                    it.stop()
                } catch (_: Exception) { /* ignore */ }
                it.release()
            }
            player = null
            EmergencySoundState.setEmergencySoundState(EmergencySoundState.State.Idle)
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
