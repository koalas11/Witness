package org.wdsl.witness.module.audio

import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class AndroidAudioPlayerModule(
    private val context: Context
): AudioPlayerModule {

    private val player: ExoPlayer by lazy {
        ExoPlayer.Builder(context).build()
    }

    override fun playAudio(path: String): Result<Unit> {
        return runCatching {

            if(player.isPlaying) player.stop()

            val mediaItem = MediaItem.fromUri(path.toUri())
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        }
    }


    override fun pauseAudio() {
        if (player.isPlaying) player.pause()
    }

    override fun resumeAudio() {
        player.play()
    }

    override fun stopAudio() {
        player.stop()
    }

    fun releasePlayer() {
        player.release()
    }

    override fun seekTo(milliseconds: Long) {
    player.seekTo(milliseconds)
    }

    override fun isPlaying(): Boolean {
        return player.isPlaying
    }

    override fun getCurrentPosition(): Long {
        return player.currentPosition
    }

    override fun getDuration(): Long {
        return player.duration.coerceAtLeast(0)
    }
}