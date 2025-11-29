package org.wdsl.witness.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.wdsl.witness.WitnessBuildConfig
import org.wdsl.witness.ui.common.RecordingListItem
import org.wdsl.witness.ui.navigation.ScreenRoute
import org.wdsl.witness.viewmodel.AppViewModel
import org.wdsl.witness.viewmodel.RecordingsUiState
import org.wdsl.witness.viewmodel.RecordingsViewModel
import org.wdsl.witness.viewmodel.witnessViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordingsScreen(
    modifier: Modifier = Modifier,
    recordingsViewModel: RecordingsViewModel = witnessViewModel(factory = RecordingsViewModel.Factory),
    appViewModel: AppViewModel,
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
    if (recordings.isEmpty()) {
        Text(
            text = "No recordings found.",
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            textAlign = TextAlign.Center,
        )
        return
    }
    LazyColumn(
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize(),
    ) {
        if (WitnessBuildConfig.DEBUG_MODE) {
        }
        items(recordings, {it.id}) { recording ->
            RecordingListItem(
                modifier = modifier,
                recording = recording,
                onClick = {
                    appViewModel.navigateTo(ScreenRoute.RecordingInfo(recordingId = recording.id))
                },
            )
        }
    }
}
