package org.wdsl.witness.module.audio

import org.wdsl.witness.util.Result

interface AudioRecorderModule {

    fun startRecording(): Result<String>

    fun stopRecording()
}

internal const val AUDIO_RECORDER_FOLDER = "Recordings"
