package org.wdsl.witness.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.wdsl.witness.ui.common.RecordingListItem
import org.wdsl.witness.ui.navigation.ScreenRoute
import org.wdsl.witness.viewmodel.AppViewModel
import org.wdsl.witness.viewmodel.RecordingsUiState
import org.wdsl.witness.viewmodel.RecordingsViewModel
import org.wdsl.witness.viewmodel.witnessViewModel

/**
 * A composable screen that displays a list of recordings.
 *
 * @param modifier The modifier to be applied to the RecordingsScreen.
 * @param recordingsViewModel The ViewModel managing the recordings state.
 * @param appViewModel The ViewModel managing the overall app state.
 */
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
        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                modifier = modifier
                    .size(48.dp),
            )
        }
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
                .padding(32.dp),
            textAlign = TextAlign.Center,
        )
        return
    }
    LazyColumn(
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(
                modifier = modifier
                    .padding(8.dp),
                text = "Recordings:",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
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
