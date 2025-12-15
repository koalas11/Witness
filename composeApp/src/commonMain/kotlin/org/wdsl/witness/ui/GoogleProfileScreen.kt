package org.wdsl.witness.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.AsyncImage
import org.wdsl.witness.LocalPlatformContext
import org.wdsl.witness.ui.util.handleOperationState
import org.wdsl.witness.viewmodel.AppState
import org.wdsl.witness.viewmodel.AppViewModel
import org.wdsl.witness.viewmodel.EmailContactsUiState
import org.wdsl.witness.viewmodel.EmailContactsViewModel
import org.wdsl.witness.viewmodel.GoogleIntegrationUiState
import org.wdsl.witness.viewmodel.GoogleIntegrationViewModel
import org.wdsl.witness.viewmodel.witnessViewModel

/**
 * A composable screen for managing Google profile settings and email contacts.
 *
 * @param modifier The modifier to be applied to the GoogleProfileScreen.
 * @param appViewModel The ViewModel managing the overall app state.
 * @param googleIntegrationViewModel The ViewModel managing Google integration state.
 * @param emailContactsViewModel The ViewModel managing email contacts state.
 */
@Composable
fun GoogleProfileScreen(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel,
    googleIntegrationViewModel: GoogleIntegrationViewModel = witnessViewModel(factory = GoogleIntegrationViewModel.Factory),
    emailContactsViewModel: EmailContactsViewModel = witnessViewModel(factory = EmailContactsViewModel.Factory),
) {
    val platformContext = LocalPlatformContext.current
    LaunchedEffect(Unit) {
        googleIntegrationViewModel.initialize()
    }
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = modifier
                .padding(8.dp),
            text = "Google Profile Settings",
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center
        )
        val googleIntegrationUiState by googleIntegrationViewModel.googleIntegrationUiState.collectAsStateWithLifecycle()
        when (googleIntegrationUiState) {
            is GoogleIntegrationUiState.NoProfile -> {
                Button(
                    modifier = modifier
                        .padding(16.dp),
                    onClick = {
                        googleIntegrationViewModel.startGoogleOAuthFlow(
                            platformContext = platformContext,
                        )
                    },
                ) {
                    Text(
                        modifier = modifier,
                        text = "Start Google OAuth Flow",
                    )
                }
                return
            }

            is GoogleIntegrationUiState.Error -> {
                Text(
                    modifier = modifier
                        .padding(16.dp),
                    text = "Error: ${(googleIntegrationUiState as GoogleIntegrationUiState.Error).message}",
                )
                return
            }

            is GoogleIntegrationUiState.Loading -> {
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
            }

            is GoogleIntegrationUiState.OAuthInProgress -> {
                Row(
                    modifier = modifier
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator(
                        modifier = modifier
                            .padding(vertical = 16.dp)
                            .padding(end = 16.dp),
                    )
                    Text(
                        modifier = modifier
                            .padding(vertical = 16.dp),
                        text = "OAuth In Progress...",
                    )
                }
                return
            }

            else -> {
                val profile = (googleIntegrationUiState as GoogleIntegrationUiState.Profile).googleProfile
                val imageLoader = ImageLoader(platformContext.context as PlatformContext)
                val emailContactsState by emailContactsViewModel.emailContactsState.collectAsStateWithLifecycle()
                val enabled = handleOperationState(
                    viewModel = emailContactsViewModel,
                )

                Card(
                    modifier = modifier
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape),
                            model = profile.picture,
                            contentDescription = "Profile Picture",
                            imageLoader = imageLoader,
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = profile.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = profile.email,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        IconButton(
                            modifier = modifier
                                .padding(8.dp),
                            onClick = {
                                googleIntegrationViewModel.signOut()
                            },
                            enabled = enabled,
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Sign Out",
                            )
                        }
                    }

                    val settingsState = appViewModel.settingsState.collectAsStateWithLifecycle()
                    val settings = (settingsState.value as AppState.Success).settings
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = settings.enableEmailOnEmergency,
                            onCheckedChange = { checked ->
                                googleIntegrationViewModel.setEnableEmailOnEmergency(checked)
                            },
                            enabled = enabled,
                        )
                        Text(
                            modifier = modifier,
                            text = "Enable Email on Emergency Contacts",
                            textAlign = TextAlign.Center
                        )
                    }
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = settings.uploadRecordingToDriveOnEnd,
                            onCheckedChange = { checked ->
                                googleIntegrationViewModel.setUploadRecordingToDriveOnEnd(checked)
                            },
                            enabled = enabled,
                        )
                        Text(
                            modifier = modifier,
                            text = "Upload Recording to Google Drive When Recording Ends",
                            textAlign = TextAlign.Center
                        )
                    }
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        var email by rememberSaveable { mutableStateOf("") }
                        TextField(
                            modifier = modifier
                                .fillMaxWidth(0.8f)
                                .padding(8.dp),
                            value = email,
                            onValueChange = {
                                email = it
                            },
                            label = { Text("Enter Email Contact") },
                            enabled = enabled,
                            placeholder = {
                                Text("name.surname@gmail.com" )
                            }
                        )
                        IconButton(
                            modifier = modifier
                                .padding(8.dp),
                            onClick = {
                                emailContactsViewModel.addEmailContact(email.trim())
                                email = ""
                            },
                            enabled = enabled,
                        ) {
                            Icon(
                                modifier = modifier,
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Email Contact",
                            )
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    emailContactsViewModel.initialize()
                }
                if (emailContactsState is EmailContactsUiState.Loading) {
                    Text(
                        modifier = modifier
                            .padding(8.dp),
                        text = "Loading email contacts...",
                    )
                    return@Column
                }
                if (emailContactsState is EmailContactsUiState.Error) {
                    Text(
                        modifier = modifier
                            .padding(8.dp),
                        text = "Error loading email contacts.",
                    )
                    return@Column
                }
                val emailContacts = (emailContactsState as EmailContactsUiState.Success).contacts
                if (emailContacts.isEmpty()) {
                    Text(
                        modifier = modifier
                            .padding(16.dp),
                        text = "No Email contacts added.",
                        textAlign = TextAlign.Center,
                    )
                    return@Column
                }
                LazyColumn(
                    modifier = modifier
                        .padding(8.dp),
                ) {
                    item {
                        Text(
                            modifier = modifier
                                .padding(8.dp),
                            text = "Email Contacts:",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )
                    }
                    items(emailContacts) { contact ->
                        Card(
                            modifier = modifier
                                .fillMaxWidth(),
                        ) {
                            Row(
                                modifier = modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    modifier = modifier
                                        .padding(horizontal = 8.dp),
                                    text = contact,
                                )
                                IconButton(
                                    modifier = modifier,
                                    onClick = {
                                        emailContactsViewModel.removeEmailContact(contact)
                                    },
                                    enabled = enabled,
                                ) {
                                    Icon(
                                        modifier = modifier
                                            .padding(horizontal = 8.dp),
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Email Contact",
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
