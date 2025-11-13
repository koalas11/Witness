package org.wdsl.witness.module.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.Result
import org.wdsl.witness.util.ResultError
import java.io.File

class AndroidAudioRecorderModule(
    private val context: Context
): AudioRecorderModule {

    private var recorder: MediaRecorder? = null

    private fun createRecorder(): MediaRecorder {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            return MediaRecorder()
        }
    }

    override fun startRecording(): Result<String> {
        return try {

            val dir = context.filesDir.resolve(AUDIO_RECORDER_FOLDER)

            val outputFile = File(
                dir,
                "recording_${System.currentTimeMillis()}.m4a"
            )

            val newRecorder = createRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile.absolutePath)

                prepare()
                start()
            }

            recorder = newRecorder
            Result.Success(outputFile.name)
        } catch (e: Exception) {
            Log.d(TAG, "startRecording: Failed to start recording", e)
            Result.Error(ResultError.UnknownError("Failed to start recording: ${e.message}"))
        }
    }

    override fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    companion object {
        private const val TAG = "Audio Recorder Module"
    }
}