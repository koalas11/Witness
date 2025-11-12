package org.wdsl.witness.module.audio

interface AudioRecorderModule {

    fun startRecording(customDirPath: String? = null): Result<String>

    fun stopRecording()
}