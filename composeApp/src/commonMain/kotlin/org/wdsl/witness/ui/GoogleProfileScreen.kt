package org.wdsl.witness.ui

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import org.wdsl.witness.viewmodel.EmailContactsUiState
import org.wdsl.witness.viewmodel.EmailContactsViewModel
import org.wdsl.witness.viewmodel.GoogleIntegrationUiState
import org.wdsl.witness.viewmodel.GoogleIntegrationViewModel
import org.wdsl.witness.viewmodel.witnessViewModel

@Composable
fun GoogleProfileScreen(
    modifier: Modifier = Modifier,
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
                Text(
                    modifier = modifier
                        .padding(16.dp),
                    text = "Loading...",
                )
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
                    AsyncImage(
                        modifier = modifier
                            .clip(CircleShape)
                            .size(128.dp)
                            .padding(8.dp)
                            .align(Alignment.CenterHorizontally),
                        model = profile.picture,
                        contentDescription = null,
                        imageLoader = imageLoader,
                    )
                    Text(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        text = profile.name,
                        textAlign = TextAlign.Center,
                    )
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
