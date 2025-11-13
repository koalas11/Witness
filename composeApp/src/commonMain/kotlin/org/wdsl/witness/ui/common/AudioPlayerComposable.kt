package org.wdsl.witness.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.wdsl.witness.getPlatformContext
import org.wdsl.witness.storage.room.Recording
import org.wdsl.witness.viewmodel.AudioPlayerState
import org.wdsl.witness.viewmodel.AudioPlayerViewModel
import org.wdsl.witness.viewmodel.RecordingsViewModel

@Composable
fun AudioPlayerComposable(
    modifier: Modifier = Modifier,
    recording: Recording,
    audioPlayerViewModel: AudioPlayerViewModel,
) {
    val platformContext = getPlatformContext()
    LaunchedEffect(recording.id) {
        audioPlayerViewModel.loadRecording(platformContext, recording)
        audioPlayerViewModel.observeAudioPlayerState()
    }
    Column(
        modifier = modifier,
    ) {
        val audioPlayerCurrentPosition by audioPlayerViewModel.audioCurrentPosition.collectAsStateWithLifecycle()
        LinearProgressIndicator(
            progress = { audioPlayerCurrentPosition.toFloat() / recording.durationMs.toFloat() },
            modifier = modifier,
        )
        val audioPlayerState by audioPlayerViewModel.audioPlayerState.collectAsStateWithLifecycle()
        Row(
            modifier = modifier,
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
