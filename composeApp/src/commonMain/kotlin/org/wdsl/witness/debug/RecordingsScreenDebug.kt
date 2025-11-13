package org.wdsl.witness.debug

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import org.wdsl.witness.storage.room.Recording
import org.wdsl.witness.ui.common.RecordingListItem
import kotlin.math.PI
import kotlin.math.sin


fun writeIntLE(array: ByteArray, offset: Int, value: Int) {
    array[offset] = (value and 0xFF).toByte()
    array[offset + 1] = ((value shr 8) and 0xFF).toByte()
    array[offset + 2] = ((value shr 16) and 0xFF).toByte()
    array[offset + 3] = ((value shr 24) and 0xFF).toByte()
}

fun writeShortLE(array: ByteArray, offset: Int, value: Short) {
    array[offset] = (value.toInt() and 0xFF).toByte()
    array[offset + 1] = ((value.toInt() shr 8) and 0xFF).toByte()
}

fun pcmToWav(
    pcmData: ByteArray,
    sampleRate: Int = 44100,
    channels: Int = 1,
    bitsPerSample: Int = 16
): ByteArray {
    val byteRate = sampleRate * channels * (bitsPerSample / 8)
    val dataSize = pcmData.size
    val totalSize = 44 + dataSize

    val header = ByteArray(44)

    // RIFF header
    header[0] = 'R'.code.toByte()
    header[1] = 'I'.code.toByte()
    header[2] = 'F'.code.toByte()
    header[3] = 'F'.code.toByte()
    writeIntLE(header, 4, totalSize - 8) // ChunkSize
    header[8] = 'W'.code.toByte()
    header[9] = 'A'.code.toByte()
    header[10] = 'V'.code.toByte()
    header[11] = 'E'.code.toByte()

    // fmt subchunk
    header[12] = 'f'.code.toByte()
    header[13] = 'm'.code.toByte()
    header[14] = 't'.code.toByte()
    header[15] = ' '.code.toByte()
    writeIntLE(header, 16, 16) // Subchunk1Size
    writeShortLE(header, 20, 1) // AudioFormat = PCM
    writeShortLE(header, 22, channels.toShort())
    writeIntLE(header, 24, sampleRate)
    writeIntLE(header, 28, byteRate)
    writeShortLE(header, 32, (channels * bitsPerSample / 8).toShort()) // BlockAlign
    writeShortLE(header, 34, bitsPerSample.toShort())

    // data subchunk
    header[36] = 'd'.code.toByte()
    header[37] = 'a'.code.toByte()
    header[38] = 't'.code.toByte()
    header[39] = 'a'.code.toByte()
    writeIntLE(header, 40, dataSize)

    return header + pcmData
}

fun generateSineWave(
    frequency: Double = 440.0,
    durationSeconds: Int = 5,
    sampleRate: Int = 44100,
    amplitude: Int = 32767 // Max for 16-bit PCM
): ByteArray {
    val samples = durationSeconds * sampleRate
    val buffer = ByteArray(samples * 2) // 2 bytes per sample (16-bit)

    for (i in 0 until samples) {
        val angle = 2.0 * PI * i * frequency / sampleRate
        val value = (amplitude * sin(angle)).toInt()
        buffer[i * 2] = (value and 0xFF).toByte()
        buffer[i * 2 + 1] = ((value shr 8) and 0xFF).toByte()
    }

    return buffer
}

fun LazyListScope.debugRecordings(
    modifier: Modifier = Modifier,
    onClick: (Recording) -> Unit,
) {
    val durationSeconds = 5
    val sampleRate = 44100
    val channels = 1
    val bytesPerSample = 2 // 16-bit PCM

    val totalBytes = durationSeconds * sampleRate * channels * bytesPerSample
    val silentAudio = ByteArray(totalBytes)
    val audio = generateSineWave(
        frequency = 440.0,
        durationSeconds = durationSeconds,
        sampleRate = sampleRate,
        amplitude = 32767
    )
    val silentRecording = Recording(
        id = 0,
        title = "Silent Audio",
        durationMs = durationSeconds * 1000L,
        recordingFileName = "silent_audio.wav",
        //data = pcmToWav(silentAudio)
    )
    val recording = Recording(
        id = 0,
        title = "Audio Tone 440Hz",
        durationMs = durationSeconds * 1000L,
        recordingFileName = "audio_tone_440hz.wav",
        //data = pcmToWav(audio)
    )
    item {
        RecordingListItem(
            modifier = modifier,
            recording = silentRecording,
            onClick = { onClick(silentRecording) },
        )
        RecordingListItem(
            modifier = modifier,
            recording = recording,
            onClick = { onClick(recording) },
        )
    }
}
