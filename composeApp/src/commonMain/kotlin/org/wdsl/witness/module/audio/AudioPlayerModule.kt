package org.wdsl.witness.module.audio

interface AudioPlayerModule {
    fun playAudio(path: String): Result<Unit>
    fun pauseAudio()
    fun resumeAudio()
    fun stopAudio()
    fun seekTo(milliseconds: Long)
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Long
    fun getDuration(): Long
}