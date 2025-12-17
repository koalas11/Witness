package org.wdsl.witness.module.audio

import org.wdsl.witness.util.Result

interface AudioPlayerModule {
    fun loadAudio(recordingName: String): Result<Long?>
    fun pauseAudio()
    fun resumeAudio()
    fun stopAudio()
    fun seekTo(milliseconds: Long)
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Long
    fun releasePlayer()
}