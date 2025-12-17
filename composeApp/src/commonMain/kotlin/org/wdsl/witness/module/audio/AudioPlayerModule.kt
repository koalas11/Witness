package org.wdsl.witness.module.audio

import org.wdsl.witness.util.Result
import org.wdsl.witness.util.ResultError

/**
 * Common interface for audio playback functionality.
 */
interface AudioPlayerModule {
    /**
     * Loads an audio recording by its name.
     *
     * @param recordingName The name of the recording file.
     * @return A [Result] containing the duration of the audio in milliseconds on success, or null if duration is unavailable.
     * Returns a [ResultError] on failure.
     */
    fun loadAudio(recordingName: String): Result<Long?>

    /**
     * Pauses the currently playing audio.
     *
     * @throws IllegalStateException if the player is not initialized.
     */
    fun pauseAudio()

    /**
     * Resumes the currently paused audio.
     *
     * @throws IllegalStateException if the player is not initialized.
     */
    fun resumeAudio()

    /**
     * Stops the currently playing audio.
     */
    fun stopAudio()

    /**
     * Seeks to a specific position in the audio.
     *
     * @param milliseconds The position to seek to, in milliseconds.
     */
    fun seekTo(milliseconds: Long)

    /**
     * Checks if the audio is currently playing.
     *
     * @return True if the audio is playing, false otherwise.
     */
    fun isPlaying(): Boolean

    /**
     * Gets the current playback position of the audio.
     *
     * @return The current position in milliseconds.
     */
    fun getCurrentPosition(): Long

    /**
     * Releases the audio player resources.
     */
    fun releasePlayer()
}