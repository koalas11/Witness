package org.wdsl.witness.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.wdsl.witness.ui.common.AudioPlayerComposable
import org.wdsl.witness.ui.common.MapComposable
import org.wdsl.witness.viewmodel.RecordingInfoUiState
import org.wdsl.witness.viewmodel.RecordingInfoViewModel
import org.wdsl.witness.viewmodel.witnessViewModel

@Composable
fun RecordingInfoScreen(
    modifier: Modifier = Modifier,
    recordingId: Long,
    recordingInfoViewModel: RecordingInfoViewModel = witnessViewModel(factory = RecordingInfoViewModel.Factory),
) {
    LaunchedEffect(Unit) {
        recordingInfoViewModel.initialize(recordingId)
    }
    val recordingInfoUiState by recordingInfoViewModel.recordingInfoUiState.collectAsStateWithLifecycle()
    if (recordingInfoUiState is RecordingInfoUiState.Loading) {
        // Show loading state
        return
    } else if (recordingInfoUiState is RecordingInfoUiState.Error) {
        // Show error state
        return
    }
    val selectedRecording = (recordingInfoUiState as RecordingInfoUiState.Loaded).recording
    Column(
        modifier = modifier,
    ) {
        Text(
            text = "Recording: ${selectedRecording.title}",
            modifier = modifier,
        )
        AudioPlayerComposable(
            modifier = modifier,
            recording = selectedRecording,
        )
        MapComposable(
            modifier = modifier,
            recording = selectedRecording,
        )
    }
}
