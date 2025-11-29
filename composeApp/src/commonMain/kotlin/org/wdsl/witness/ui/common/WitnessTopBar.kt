package org.wdsl.witness.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.wdsl.witness.state.EmergencySoundState
import org.wdsl.witness.ui.navigation.ScreenRoute
import org.wdsl.witness.viewmodel.AppViewModel
import org.wdsl.witness.viewmodel.EmergencySoundViewModel
import witness.composeapp.generated.resources.Res
import witness.composeapp.generated.resources.app_name
import witness.composeapp.generated.resources.mobile_sound_2
import witness.composeapp.generated.resources.robe

/**
 * A top app bar composable for the Witness application.
 *
 * @param modifier The modifier to be applied to the top app bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WitnessTopBar(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel,
    emergencySoundViewModel: EmergencySoundViewModel,
) {
    val shouldShowBackButton by appViewModel.shouldShowBackButton.collectAsStateWithLifecycle()
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = modifier
                        .size(48.dp),
                    imageVector = vectorResource(Res.drawable.robe),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    modifier = modifier,
                    text = stringResource(Res.string.app_name),
                )
            }
        },
        actions = {
            val emergencySoundState by EmergencySoundState.emergencySoundState.collectAsStateWithLifecycle()
            when (emergencySoundState) {
                is EmergencySoundState.State.Error -> {
                    Icon(
                        modifier = modifier,
                        imageVector = Icons.Default.AlarmOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    )
                }
                else -> {
                    val backstack by appViewModel.backStack.collectAsStateWithLifecycle()
                    if (backstack.last() is ScreenRoute.Home) {
                        return@CenterAlignedTopAppBar
                    }
                    IconButton(
                        modifier = modifier
                            .padding(end = 8.dp)
                            .fillMaxSize(0.15f),
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
                            tint = if (emergencySoundState.isPlaying) Color.Red else MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        },
        navigationIcon = {
            if (shouldShowBackButton) {
                IconButton(
                    modifier = modifier,
                    onClick = { appViewModel.navigateBack() },
                ) {
                    Icon(
                        modifier = modifier,
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back",
                    )
                }
            }
        },
    )
}
