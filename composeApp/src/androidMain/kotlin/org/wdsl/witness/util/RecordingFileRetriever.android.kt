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
        Result.Success(recording.readBytes())
    } else {
        Result.Error(ResultError.UnknownError("Recording file not found or unreadable"))
    }
}

actual fun deleteRecordingFile(
    platformContext: PlatformContext,
    fileName: String,
): Result<Unit> {
    val context = platformContext.context as Context
    val recordingDir = context.filesDir.resolve(AUDIO_RECORDER_FOLDER)
    val recording = recordingDir.resolve(fileName)
    return if (recording.isFile && recording.canRead()) {
        val deleted = recording.delete()
        if (deleted) {
            Result.Success(Unit)
        } else {
            Result.Error(ResultError.UnknownError("Failed to delete recording file"))
        }
    } else {
        Result.Error(ResultError.UnknownError("Recording file not found or unreadable"))
    }
}
