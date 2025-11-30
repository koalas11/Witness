package org.wdsl.witness.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.vectorResource
import org.wdsl.witness.platform
import org.wdsl.witness.state.EmergencyServiceState
import org.wdsl.witness.state.EmergencySoundState
import org.wdsl.witness.viewmodel.AppViewModel
import org.wdsl.witness.viewmodel.EmergencySoundViewModel
import witness.composeapp.generated.resources.Res
import witness.composeapp.generated.resources.mobile_sound_2

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
    emergencySoundViewModel: EmergencySoundViewModel,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        val emergencySoundState by EmergencySoundState.emergencySoundState.collectAsStateWithLifecycle()
        when (emergencySoundState) {
            is EmergencySoundState.State.Error -> {
                Icon(
                    modifier = modifier.align(Alignment.TopEnd),
                    imageVector = Icons.Default.AlarmOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                )
            }
            else -> {
                val width = if (platform.isPortrait()) 0.3f else 0.2f
                val height = if (platform.isPortrait()) 0.16f else 0.3f
                IconButton(
                    modifier = modifier
                        .padding(top = 16.dp, end = 16.dp)
                        .align(Alignment.TopEnd)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .fillMaxWidth(width)
                        .fillMaxHeight(height),
                    onClick = {
                        if (emergencySoundState.isPlaying) {
                            emergencySoundViewModel.stopEmergencySound()
                        } else {
                            emergencySoundViewModel.playEmergencySound()
                        }
                    },
                ) {
                    Icon(
                        modifier = modifier
                            .fillMaxSize(0.85f),
                        imageVector = vectorResource(Res.drawable.mobile_sound_2),
                        contentDescription = null,
                        tint = if (emergencySoundState.isPlaying) Color.Red else MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val width = if (platform.isPortrait()) 0.75f else 0.3f
            val emergencyServiceState by EmergencyServiceState.emergencyServiceState.collectAsStateWithLifecycle()
            IconButton(
                modifier = modifier
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .fillMaxWidth(width)
                    .fillMaxHeight(0.55f),
                onClick = {
                    if (emergencyServiceState is EmergencyServiceState.State.Running) {
                        appViewModel.stopAudioRecording()
                    } else {
                        appViewModel.startAudioRecording()
                    }
                },
            ) {
                if (emergencyServiceState is EmergencyServiceState.State.Running) {
                    Icon(
                        modifier = modifier
                            .fillMaxSize(0.9f),
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Stop Emergency Recording",
                        tint = Color.Red,
                    )
                } else {
                    Icon(
                        modifier = modifier
                            .fillMaxSize(0.9f),
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Start Emergency Recording",
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
            val text = when (emergencyServiceState) {
                is EmergencyServiceState.State.Running -> "Stop Emergency Recording"
                is EmergencyServiceState.State.Idle -> "Start Emergency Recording"
                is EmergencyServiceState.State.Error -> "Error in Emergency Service"
            }
            Text(
                modifier = modifier
                    .padding(top = 16.dp),
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
