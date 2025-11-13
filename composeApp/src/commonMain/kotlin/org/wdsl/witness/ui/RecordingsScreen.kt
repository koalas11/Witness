package org.wdsl.witness.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.wdsl.witness.WitnessBuildConfig
import org.wdsl.witness.debug.debugRecordings
import org.wdsl.witness.storage.room.Recording
import org.wdsl.witness.ui.common.AudioPlayerComposable
import org.wdsl.witness.ui.common.RecordingListItem
import org.wdsl.witness.viewmodel.AudioPlayerViewModel
import org.wdsl.witness.viewmodel.RecordingsUiState
import org.wdsl.witness.viewmodel.RecordingsViewModel
import org.wdsl.witness.viewmodel.witnessViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordingsScreen(
    modifier: Modifier = Modifier,
    recordingsViewModel: RecordingsViewModel = witnessViewModel(factory = RecordingsViewModel.Factory),
    audioPlayerViewModel: AudioPlayerViewModel = witnessViewModel(factory = AudioPlayerViewModel.Factory),
) {
    LaunchedEffect(Unit) {
        recordingsViewModel.initialize()
    }
    val recordingsUiState by recordingsViewModel.recordingsUiState.collectAsStateWithLifecycle()
    if (recordingsUiState is RecordingsUiState.Loading) {
        CircularProgressIndicator(
            modifier = modifier,
        )
        return
    } else if (recordingsUiState is RecordingsUiState.Error) {
        // Show error state
        return
    }
    val recordings = (recordingsUiState as RecordingsUiState.Success).recordings
    var selectedRecording: Recording? by rememberSaveable { mutableStateOf(null) }
    if (selectedRecording != null) {
        ModalBottomSheet(
            modifier = modifier,
            onDismissRequest = { selectedRecording = null },
        ) {
             AudioPlayerComposable(
                 modifier = modifier,
                 recording = selectedRecording!!,
                 audioPlayerViewModel = audioPlayerViewModel,
             )
        }
    }
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
    ) {
        if (WitnessBuildConfig.DEBUG_MODE) {
        }
        items(recordings, {it.id}) { recording ->
            RecordingListItem(
                modifier = modifier,
                recording = recording,
                onClick = {
                    selectedRecording = recording
                },
            )
        }
    }
}
