package org.wdsl.witness.debug

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import org.wdsl.witness.storage.room.Recording
import org.wdsl.witness.ui.common.RecordingListItem

fun LazyListScope.debugRecordings(
    modifier: Modifier = Modifier,
    onClick: (Recording) -> Unit,
) {
    val silentRecording = Recording(
        id = 0,
        title = "Silent Audio",
        recordingFileName = "silent_audio.wav",
        gpsPositions = emptyList()
    )
    val recording = Recording(
        id = 0,
        title = "Audio Tone 440Hz",
        recordingFileName = "audio_tone_440hz.wav",
        gpsPositions = emptyList()
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
