package org.wdsl.witness.ui

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.wdsl.witness.viewmodel.EmergencySoundUIState
import org.wdsl.witness.viewmodel.EmergencySoundViewModel
import org.wdsl.witness.viewmodel.witnessViewModel

@Composable
fun EmergencySoundScreen(
    modifier: Modifier = Modifier,
    emergencySoundViewModel: EmergencySoundViewModel = witnessViewModel(factory = EmergencySoundViewModel.Factory),
) {
    LaunchedEffect(Unit) {
        emergencySoundViewModel.initialize()
    }
    val emergencySoundUIState by emergencySoundViewModel.emergencySoundUiState.collectAsStateWithLifecycle()
    if (emergencySoundUIState == EmergencySoundUIState.Loading) {
        Text(
            modifier = modifier,
            text = "Loading Emergency Sound...",
        )
        return
    }
    if (emergencySoundUIState == EmergencySoundUIState.Error) {
        Text(
            modifier = modifier,
            text = "Error loading Emergency Sound.",
        )
        return
    }
    if (emergencySoundUIState == EmergencySoundUIState.NotSupported) {
        Text(
            modifier = modifier,
            text = "Emergency Sound not supported.",
        )
        return
    }
    Button(
        modifier = modifier,
        onClick = {
            if (emergencySoundUIState.isPlaying) {
                emergencySoundViewModel.stopEmergencySound()
            } else {
                emergencySoundViewModel.playEmergencySound()
            }
        }
    ) {
        val text = if (emergencySoundUIState.isPlaying)
            "Stop Emergency Sound"
        else
            "Start Emergency Sound"
        Text(
            modifier = modifier,
            text = text,
        )
    }
}
