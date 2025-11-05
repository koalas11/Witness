package org.wdsl.witness.util

import android.content.Context
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat.createAttributionContext
import androidx.media3.common.MediaItem
import androidx.media3.common.listen
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.wdsl.witness.PlatformContext
import org.wdsl.witness.storage.room.Recording
import kotlin.time.Duration.Companion.milliseconds

class AndroidAudioPlayer : AudioPlayer {
    private var _audioCurrentPosition = MutableStateFlow(0L)
    override val audioCurrentPosition: StateFlow<Long> = _audioCurrentPosition.asStateFlow()

    private var exoPlayer: ExoPlayer? = null

    override fun loadRecording(
        platformContext: PlatformContext,
        recording: Recording
    ) {
        requireNotNull(platformContext.context as Context) { "Context cannot be null" }
        val audioAttributionContext = createAttributionContext(
            platformContext.context as Context,
            "audioPlayback"
        )
        val mediaItem = MediaItem.fromUri("http://localhost/audio")
        val mediaSource = ProgressiveMediaSource.Factory(
            ByteArrayDataSourceFactory(recording.data)
        ).createMediaSource(mediaItem)
        _audioCurrentPosition.value = 0L
        exoPlayer = ExoPlayer.Builder(audioAttributionContext).build()
        exoPlayer!!.setMediaSource(mediaSource)
        exoPlayer!!.prepare()
    }

    override suspend fun observeAudioPlayerState() {
        requireNotNull(exoPlayer)
        exoPlayer!!.listen {
            //_audioCurrentPosition.value = exoPlayer!!.currentPosition
        }
    }

    override suspend fun observeAudioCurrentPosition() {
        requireNotNull(exoPlayer)
        while (true) {
            _audioCurrentPosition.value = exoPlayer!!.currentPosition
            delay(5.milliseconds)
        }
    }

    override fun play() {
        requireNotNull(exoPlayer)
        exoPlayer!!.play()
    }

    override fun pause() {
        requireNotNull(exoPlayer)
        exoPlayer!!.pause()
    }

    override fun release() {
        exoPlayer?.release()
    }
}

@OptIn(UnstableApi::class)
actual fun getAudioPlayer(): AudioPlayer = AndroidAudioPlayer()
