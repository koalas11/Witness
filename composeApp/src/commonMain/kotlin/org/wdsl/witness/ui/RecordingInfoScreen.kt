package org.wdsl.witness.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.painterResource
import org.wdsl.witness.LocalPlatformContext
import org.wdsl.witness.ui.common.AudioPlayerComposable
import org.wdsl.witness.ui.common.MapComposable
import org.wdsl.witness.ui.navigation.ScreenRoute
import org.wdsl.witness.ui.util.handleOperationState
import org.wdsl.witness.viewmodel.AppViewModel
import org.wdsl.witness.viewmodel.GoogleIntegrationUiState
import org.wdsl.witness.viewmodel.GoogleIntegrationViewModel
import org.wdsl.witness.viewmodel.RecordingInfoUiState
import org.wdsl.witness.viewmodel.RecordingInfoViewModel
import org.wdsl.witness.viewmodel.witnessViewModel
import witness.composeapp.generated.resources.Res
import witness.composeapp.generated.resources.gemini

/**
 * A composable screen that displays information about a specific recording.
 *
 * @param modifier The modifier to be applied to the RecordingInfoScreen.
 * @param recordingId The ID of the recording to display information for.
 * @param appViewModel The ViewModel managing the overall app state.
 * @param recordingInfoViewModel The ViewModel managing the recording info state.
 * @param googleIntegrationViewModel The ViewModel managing Google integration state.
 */
@Composable
fun RecordingInfoScreen(
    modifier: Modifier = Modifier,
    recordingId: Long,
    appViewModel: AppViewModel,
    recordingInfoViewModel: RecordingInfoViewModel = witnessViewModel(factory = RecordingInfoViewModel.Factory),
    googleIntegrationViewModel: GoogleIntegrationViewModel = witnessViewModel(factory = GoogleIntegrationViewModel.Factory)
) {
    LaunchedEffect(recordingId) {
        recordingInfoViewModel.initialize(recordingId)
        googleIntegrationViewModel.initialize()
    }
    val recordingInfoUiState by recordingInfoViewModel.recordingInfoUiState.collectAsStateWithLifecycle()
    if (recordingInfoUiState is RecordingInfoUiState.Loading) {
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
    } else if (recordingInfoUiState is RecordingInfoUiState.Error) {
        Text(
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth(),
            text = (recordingInfoUiState as RecordingInfoUiState.Error).message,
            textAlign = TextAlign.Center,
        )
        return
    }
    val selectedRecording = (recordingInfoUiState as RecordingInfoUiState.Loaded).recording
    Column(
        modifier = modifier
            .padding(8.dp),
    ) {
        Card(
            modifier = modifier
                .weight(0.30f),
        ) {
            Column(
                modifier = modifier,
            ) {
                Text(
                    modifier = modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .weight(0.06f),
                    text = "Title: ${selectedRecording.title}",
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                AudioPlayerComposable(
                    modifier = modifier,
                    recording = selectedRecording,
                )
                val googleIntegrationUiState by googleIntegrationViewModel.googleIntegrationUiState.collectAsStateWithLifecycle()
                val enabledRec = handleOperationState(
                    viewModel = recordingInfoViewModel,
                )

                val enabledGoogle = handleOperationState(
                    viewModel = googleIntegrationViewModel,
                )
                val enabled = enabledRec && enabledGoogle
                var openConfirm by remember { mutableStateOf(false) }

                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .weight(0.1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (googleIntegrationUiState is GoogleIntegrationUiState.Profile) {
                        Button(
                            modifier = modifier
                                .padding(horizontal = 8.dp)
                                .align(Alignment.CenterVertically),
                            onClick = {
                                googleIntegrationViewModel.uploadRecordingToGoogleDrive(
                                    recording = selectedRecording,
                                )
                            },
                            enabled = enabled,
                        ) {
                            Row(
                                modifier = modifier,
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Row(
                                    modifier = modifier,
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    Icon(
                                        modifier = modifier
                                            .padding(end = 8.dp),
                                        imageVector = Icons.Default.UploadFile,
                                        contentDescription = "Upload Icon",
                                    )
                                    Text(
                                        modifier = modifier,
                                        text = "Upload to Drive",
                                    )
                                }
                            }
                        }
                    }
                    IconButton(
                        modifier = modifier
                            .padding(start = 16.dp),
                        onClick = {
                            appViewModel.navigateTo(ScreenRoute.RecordingSummary(recordingId))
                        },
                    ) {
                        val icon = painterResource(Res.drawable.gemini)
                        Icon(
                            modifier = modifier
                                .size(24.dp)
                                .aspectRatio(1f),
                            painter = icon,
                            contentDescription = "Summarize Recording",
                        )
                    }
                    IconButton(
                        modifier = modifier
                            .padding(start = 16.dp),
                        onClick = {
                            openConfirm = true
                        },
                        enabled = enabled,
                    ) {
                        Icon(
                            modifier = modifier,
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Recording",
                        )
                    }
                }
                if (!openConfirm) {
                    return@Column
                }
                AlertDialog(
                    onDismissRequest = {
                        openConfirm = false
                    },
                    title = {
                        Text(text = "Confirm Deletion")
                    },
                    text = {
                        Text("Are you sure you want to delete this recording? This action cannot be undone.")
                    },
                    confirmButton = {
                        val platformContext = LocalPlatformContext.current
                        Button(
                            onClick = {
                                recordingInfoViewModel.deleteRecording(
                                    platformContext = platformContext,
                                    recording = selectedRecording,
                                )
                                openConfirm = false
                            },
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                openConfirm = false
                            },
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
        MapComposable(
            modifier = modifier,
            recording = selectedRecording,
        )
    }
}
