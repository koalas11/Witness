package org.wdsl.witness.util

import android.content.Context
import org.wdsl.witness.PlatformContext
import org.wdsl.witness.module.audio.AUDIO_RECORDER_FOLDER

actual fun getRecordingFile(
    platformContext: PlatformContext,
    fileName: String,
): Result<ByteArray> {
    val context = platformContext.context as Context
    val recordingDir = context.filesDir.resolve(AUDIO_RECORDER_FOLDER)
    val recording = recordingDir.resolve(fileName)
    return if (recording.isFile && recording.canRead()) {
        Result.Success(recording.absolutePath.toByteArray())
    } else {
        Result.Error(ResultError.UnknownError("Recording file not found or unreadable"))
    }
}
