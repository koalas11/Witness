package org.wdsl.witness.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.wdsl.witness.storage.room.Recording
import org.wdsl.witness.viewmodel.AudioPlayerState
import org.wdsl.witness.viewmodel.AudioPlayerViewModel
import org.wdsl.witness.viewmodel.witnessViewModel

@Composable
fun AudioPlayerComposable(
    modifier: Modifier = Modifier,
    recording: Recording,
    audioPlayerViewModel: AudioPlayerViewModel = witnessViewModel(factory = AudioPlayerViewModel.Factory),
) {
    LaunchedEffect(recording.id) {
        audioPlayerViewModel.loadRecording(recording)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .weight(0.14f),
    ) {
        val durationMsState by audioPlayerViewModel.audioDurationMsState.collectAsStateWithLifecycle()
        val audioPlayerCurrentPosition by audioPlayerViewModel.audioCurrentPosition.collectAsStateWithLifecycle()
        var isSeeking by remember { mutableStateOf(false) }
        var seekPosition by remember { mutableStateOf(0f) }

        LaunchedEffect(audioPlayerCurrentPosition, durationMsState, isSeeking) {
            if (!isSeeking) {
                seekPosition = minOf(audioPlayerCurrentPosition, durationMsState).toFloat()
            }
        }

        Slider(
            value = seekPosition,
            onValueChange = { newValue ->
                isSeeking = true
                seekPosition = newValue
            },
            onValueChangeFinished = {
                isSeeking = false
                val targetMs = seekPosition.toLong().coerceIn(0L, durationMsState)
                audioPlayerViewModel.seekTo(targetMs)
            },
            valueRange = 0f..durationMsState.toFloat(),
            modifier = modifier.fillMaxWidth(),
        )
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "00:00")
            Text(text = formatMsToTime(audioPlayerCurrentPosition))
            if (durationMsState > 0) {
                Text(text = formatMsToTime(durationMsState))
            } else {
                Text(text = "00:00")
            }
        }

        val audioPlayerState by audioPlayerViewModel.audioPlayerState.collectAsStateWithLifecycle()
        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            if (audioPlayerState == AudioPlayerState.Playing) {
                IconButton(
                    onClick = { audioPlayerViewModel.pauseRecording() },
                    modifier = modifier,
                ) {
                    Icon(
                        modifier = modifier,
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Pause",
                    )
                }
            } else if (audioPlayerState == AudioPlayerState.RecordingReady || audioPlayerState == AudioPlayerState.Paused) {
                IconButton(
                    onClick = { audioPlayerViewModel.playRecording() },
                    modifier = modifier,
                ) {
                    Icon(
                        modifier = modifier,
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                    )
                }
            }
        }
    }
}

private fun formatMsToTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
}
