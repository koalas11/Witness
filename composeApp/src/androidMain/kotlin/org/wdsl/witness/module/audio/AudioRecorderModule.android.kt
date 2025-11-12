package org.wdsl.witness.module.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
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

    override fun startRecording(customDirPath: String?): Result<String> {
        return runCatching {

            val dir =
                if (customDirPath != null)
                    File(customDirPath)
                else context.filesDir

            val outputFile = File(
                dir,
                "recording_${System.currentTimeMillis()}.m4a")

            val newRecorder = createRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile.absolutePath)

                prepare()
                start()
            }

            recorder = newRecorder
            outputFile.absolutePath

        }.onFailure { recorder?.release() }
    }

    override fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }
}