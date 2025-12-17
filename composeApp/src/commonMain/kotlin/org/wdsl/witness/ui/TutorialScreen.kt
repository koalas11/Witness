package org.wdsl.witness.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.wdsl.witness.LocalPlatformContext
import org.wdsl.witness.state.AppSettingsState
import org.wdsl.witness.ui.navigation.ScreenRoute
import org.wdsl.witness.ui.util.fastUIActions
import org.wdsl.witness.ui.util.handleOperationState
import org.wdsl.witness.util.AUDIO_RECORDING_PERMISSION
import org.wdsl.witness.util.COARSE_LOCATION_PERMISSION
import org.wdsl.witness.util.READ_CONTACTS_PERMISSION
import org.wdsl.witness.util.SMS_PERMISSION
import org.wdsl.witness.viewmodel.AppViewModel
import org.wdsl.witness.viewmodel.SettingsViewModel
import org.wdsl.witness.viewmodel.witnessViewModel

/**
 * A composable function that displays the tutorial screen.
 *
 * @param modifier The modifier to be applied to the composable.
 * @param appViewModel The ViewModel that holds the application state.
 * @param settingsViewModel The ViewModel that holds the settings state.
 */
@Composable
fun TutorialScreen(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel,
    settingsViewModel: SettingsViewModel = witnessViewModel(factory = SettingsViewModel.Factory)
) {
    var currentPage by rememberSaveable { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            when (currentPage) {
                0 -> {
                    Text(
                        modifier = modifier,
                        text = "Welcome to Witness App!"
                    )
                    Spacer(modifier = modifier.height(8.dp))
                    Text(
                        modifier = modifier,
                        text = "Main features:"
                    )
                    Spacer(modifier = modifier.height(8.dp))
                    Text(
                        modifier = modifier,
                        text = "• Continuous audio and GPS location recording when activated"
                    )
                    Text(
                        modifier = modifier,
                        text = "• Automatic SMS and email alerts to trusted contacts every 5 minutes with updated location"
                    )
                    Text(
                        modifier = modifier,
                        text = "• Immediate loud alarm sound to draw attention and discourage threatening behavior"
                    )
                    Text(modifier = modifier, text = "• Data stored locally and on Google Drive")
                    Text(
                        modifier = modifier,
                        text = "• Map visualization of recorded locations and playback of captured audio"
                    )
                    Text(
                        modifier = modifier,
                        text = "• Audio processing with LLM for segmentation, summarization, emotion detection, and translation"
                    )
                    Spacer(modifier = modifier.height(12.dp))
                    Text(
                        modifier = modifier,
                        text = "Note: Automatic Google Drive upload is completely opt-in. Trusted contacts and email addresses must be added manually in the settings."
                    )
                    Spacer(modifier = modifier.height(12.dp))
                    Text(
                        modifier = modifier,
                        text = "Note: LLM audio processing features require internet connectivity and need to be manually applied to a recording."
                    )
                    Spacer(modifier = modifier.height(24.dp))
                    Text(
                        modifier = modifier,
                        text = "Please proceed to grant the necessary permissions on the next screen."
                    )
                }

                1 -> {
                    Text(text = "Required permissions")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "To work correctly the app requires the following permissions: location, audio recording, and SMS.")
                    Spacer(modifier = Modifier.height(12.dp))
                    val platformContext = LocalPlatformContext.current
                    val appSettingsChanged by AppSettingsState.settingsChanged.collectAsStateWithLifecycle()
                    val permissions = listOf(
                        COARSE_LOCATION_PERMISSION,
                        AUDIO_RECORDING_PERMISSION,
                        SMS_PERMISSION,
                        READ_CONTACTS_PERMISSION,
                    )
                    var permissionsMissing by rememberSaveable(appSettingsChanged) {
                        mutableStateOf(
                            permissions.map {
                                it.id to fastUIActions.checkPermissionsStatus(
                                    platformContext,
                                    it.id,
                                )
                            }
                        )
                    }
                    val allGranted = permissionsMissing.all { it.second }
                    if (allGranted) {
                        Row(
                            modifier = modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 4.dp)
                                .padding(8.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                modifier = modifier
                                    .padding(end = 8.dp),
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                            )
                            Text(
                                modifier = modifier,
                                text = "All required permissions granted.",
                            )
                        }
                    } else {
                        Row(
                            modifier = modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 4.dp)
                                .padding(8.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                modifier = modifier
                                    .padding(end = 8.dp),
                                imageVector = Icons.Default.NotificationImportant,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                            )
                            Text(
                                modifier = modifier,
                                text = "Some required permissions are missing:",
                            )
                        }
                        permissionsMissing.forEach { (permissionId, granted) ->
                            val permission = permissions.first { it.id == permissionId }
                            if (!granted) {
                                Row(
                                    modifier = modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        modifier = modifier
                                            .padding(start = 32.dp),
                                        imageVector = permission.icon,
                                        contentDescription = null,
                                    )
                                    Text(
                                        modifier = modifier
                                            .padding(start = 4.dp, end = 8.dp),
                                        text = permission.name,
                                    )
                                    fastUIActions.PermissionRequestDialog(
                                        modifier = modifier,
                                        permission = permission,
                                    )
                                }
                            }
                        }
                    }
                }

                2 -> {
                    Text(text = "Tutorial complete! We strongly recommend configuring these additional settings:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "• Trusted contacts for SMS and email alerts")
                    Text(text = "• Google Account for Gmail alerts and Google Drive uploads")
                    Text(text = "• Other settings as desired")
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "You can access and modify these settings at any time from the Settings screen.")
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "Tap Finish to complete the tutorial and start using the app.")
                }
            }
        }

        val enabled = handleOperationState(
            viewModel = settingsViewModel,
            onSuccess = {
                appViewModel.navigateTo(ScreenRoute.Home)
            }
        )

        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            if (currentPage > 0) {
                Button(
                    onClick = { currentPage -= 1 },
                    enabled = enabled,
                ) {
                    Text(
                        modifier = modifier,
                        text = "Back",
                    )
                }
                Spacer(modifier = modifier.width(8.dp))
            }

            if (currentPage < 2) {
                Button(
                    modifier = modifier,
                    onClick = { currentPage += 1 },
                    enabled = enabled,
                ) {
                    Text(
                        modifier = modifier,
                        text = "Next",
                    )
                }
            } else {
                Button(
                    modifier = modifier,
                    onClick = {
                        settingsViewModel.setTutorialDone(true)
                    },
                    enabled = enabled,
                ) {
                    Text(
                        modifier = modifier,
                        text = "Finish",
                    )
                }
            }
        }
    }
}
