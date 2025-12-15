package org.wdsl.witness.module.audio

import android.content.Context
import androidx.compose.ui.util.fastCoerceAtLeast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.Result
import org.wdsl.witness.util.ResultError

/**
 * Android implementation of the AudioPlayerModule using ExoPlayer.
 *
 * @param context The Android context.
 */
class AndroidAudioPlayerModule(
    private val context: Context
): AudioPlayerModule {

    private var _player: ExoPlayer? = null

    override fun loadAudio(recordingName: String): Result<Unit> {
        return try {
            val contextAttribute = ContextCompat.createAttributionContext(context, "audioPlayback")
            _player = ExoPlayer.Builder(contextAttribute).build()
            val path = context.filesDir.resolve(AUDIO_RECORDER_FOLDER).resolve(recordingName)
            val mediaItem = MediaItem.fromUri(path.toUri())
            _player!!.setMediaItem(mediaItem)
            _player!!.prepare()
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.d(TAG, "playAudio: Failed to play audio", e)
            Result.Error(ResultError.UnknownError("Failed to play audio: ${e.message}"))
        }
    }

    override fun pauseAudio() {
        requireNotNull(_player) { "Player is not initialized. Call loadAudio() first." }
        _player!!.pause()
    }

    override fun resumeAudio() {
        requireNotNull(_player) { "Player is not initialized. Call loadAudio() first." }
        _player!!.play()
    }

    override fun stopAudio() {
        requireNotNull(_player) { "Player is not initialized. Call loadAudio() first." }
        _player!!.stop()
    }

    override fun releasePlayer() {
        _player!!.release()
        _player = null
    }

    override fun seekTo(milliseconds: Long) {
        requireNotNull(_player) { "Player is not initialized. Call loadAudio() first." }
        _player!!.seekTo(milliseconds)
    }

    override fun isPlaying(): Boolean {
        return _player?.isPlaying ?: false
    }

    override fun getCurrentPosition(): Long {
        return _player?.currentPosition ?: 0L
    }

    override fun getDuration(): Long {
        return _player?.duration?.fastCoerceAtLeast(0L) ?: 0L
    }

    companion object {
        private const val TAG = "AndroidAudioPlayerModule"
    }
}