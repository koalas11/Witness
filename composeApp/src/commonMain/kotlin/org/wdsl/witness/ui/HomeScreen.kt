package org.wdsl.witness.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.vectorResource
import org.wdsl.witness.viewmodel.AppViewModel
import witness.composeapp.generated.resources.Res
import witness.composeapp.generated.resources.robe

/**
 * A composable function that represents the Home Screen of the Witness application.
 *
 * @param modifier The modifier to be applied to the Home Screen.
 * @param appViewModel The ViewModel for the application.
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = modifier,
            text = "This is the Home Screen",
        )
        Icon(
            modifier = modifier,
            imageVector = vectorResource(Res.drawable.robe),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        val recordingUiState by appViewModel.recordingUiState.collectAsStateWithLifecycle()
        Button(
            modifier = modifier,
            onClick = {
                if (recordingUiState) {
                    appViewModel.stopAudioRecording()
                } else {
                    appViewModel.startAudioRecording()
                }
            },
        ) {
            Text(if (recordingUiState) "Stop Recording" else "Start Recording")
        }
    }
}
