package org.wdsl.witness.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.wdsl.witness.storage.room.Recording
import org.wdsl.witness.viewmodel.AudioPlayerState
import org.wdsl.witness.viewmodel.AudioPlayerViewModel

@Composable
fun AudioPlayerComposable(
    modifier: Modifier = Modifier,
    recording: Recording,
    audioPlayerViewModel: AudioPlayerViewModel,
) {
    LaunchedEffect(recording.id) {
        audioPlayerViewModel.loadRecording(recording)
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        val audioPlayerCurrentPosition by audioPlayerViewModel.audioCurrentPosition.collectAsStateWithLifecycle()
        LinearProgressIndicator(
            progress = { audioPlayerCurrentPosition.toFloat() / recording.durationMs.toFloat() },
            modifier = modifier.fillMaxWidth(),
        )
        val audioPlayerState by audioPlayerViewModel.audioPlayerState.collectAsStateWithLifecycle()
        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            if (audioPlayerState == AudioPlayerState.Playing) {
                Button(
                    onClick = { audioPlayerViewModel.pauseRecording() },
                    modifier = modifier,
                ) {
                    Text(text = "Pause")
                }
            } else if (audioPlayerState == AudioPlayerState.RecordingReady || audioPlayerState == AudioPlayerState.Paused) {
                Button(
                    onClick = { audioPlayerViewModel.playRecording() },
                    modifier = modifier,
                ) {
                    Text(text = "Play")
                }
            }
        }
    }
}
