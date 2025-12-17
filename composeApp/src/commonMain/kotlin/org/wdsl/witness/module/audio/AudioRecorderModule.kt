package org.wdsl.witness.module.audio

import org.wdsl.witness.util.Result

/**
 * Common interface for audio recording functionality.
 */
interface AudioRecorderModule {
    /**
     * Starts audio recording and returns the name of the recorded file.
     *
     * @return Result containing the recording file name on success, or an error on failure.
     */
    fun startRecording(): Result<String>

    /**
     * Stops the ongoing audio recording.
     *
     * @return Result indicating success or failure of stopping the recording.
     */
    fun stopRecording(): Result<Unit>
}

internal const val AUDIO_RECORDER_FOLDER = "recordings"
