package org.wdsl.witness.util

import kotlinx.coroutines.flow.StateFlow
import org.wdsl.witness.PlatformContext
import org.wdsl.witness.storage.room.Recording

interface AudioPlayer {
    val audioCurrentPosition: StateFlow<Long>
    fun loadRecording(platformContext: PlatformContext, recording: Recording)
    suspend fun observeAudioPlayerState()
    suspend fun observeAudioCurrentPosition()
    fun play()
    fun pause()
    fun release()
}

expect fun getAudioPlayer() : AudioPlayer
