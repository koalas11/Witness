package org.wdsl.witness.ui.setting

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.wdsl.witness.ui.util.fastUIActions
import org.wdsl.witness.ui.util.handleOperationState
import org.wdsl.witness.viewmodel.AppState
import org.wdsl.witness.viewmodel.AppViewModel
import org.wdsl.witness.viewmodel.SettingsViewModel
import org.wdsl.witness.viewmodel.SmsContactsUiState
import org.wdsl.witness.viewmodel.SmsContactsViewModel
import org.wdsl.witness.viewmodel.witnessViewModel

/**
 * A composable screen for managing SMS emergency contacts settings.
 *
 * @param modifier The modifier to be applied to the SmsSettingsScreen.
 * @param appViewModel The ViewModel managing the overall app state.
 * @param settingsViewModel The ViewModel managing the settings state.
 * @param smsContactsViewModel The ViewModel managing the SMS contacts state.
 */
@Composable
fun SmsSettingsScreen(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel,
    settingsViewModel: SettingsViewModel = witnessViewModel(factory = SettingsViewModel.Factory),
    smsContactsViewModel: SmsContactsViewModel = witnessViewModel(factory = SmsContactsViewModel.Factory),
) {
    fastUIActions.ForceScreenOrientation(1)
    LaunchedEffect(Unit) {
        smsContactsViewModel.initialize()
    }
    val smsContactsState by smsContactsViewModel.smsContactsState.collectAsStateWithLifecycle()
    if (smsContactsState is SmsContactsUiState.Loading) {
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
    if (smsContactsState is SmsContactsUiState.Error) {
        Text(
            modifier = modifier,
            text = "Error loading SMS contacts.",
        )
        return
    }
    val enabledSettings = handleOperationState(
        viewModel = settingsViewModel,
    )
    val enabledSms = handleOperationState(
        viewModel = smsContactsViewModel,
    )
    val enabled = enabledSettings && enabledSms
    val smsContacts = (smsContactsState as SmsContactsUiState.Success).contacts
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = modifier
                .padding(16.dp),
        ) {
            Text(
                modifier = modifier
                    .padding(8.dp),
                text = "SMS Emergency Contacts",
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                val settingsState by appViewModel.settingsState.collectAsStateWithLifecycle()
                if (settingsState !is AppState.Success) {
                    return@Row
                }
                val settings = (settingsState as AppState.Success).settings
                Checkbox(
                    modifier = modifier,
                    checked = settings.enableSmsOnEmergency,
                    onCheckedChange = { isChecked ->
                        settingsViewModel.setEnableSmsOnEmergency(isChecked)
                    },
                    enabled = enabled,
                )
                Text(
                    modifier = modifier,
                    text = "Enable SMS Emergency Contacts",
                    textAlign = TextAlign.Center,
                )
            }
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                var number by rememberSaveable { mutableStateOf("") }
                TextField(
                    modifier = modifier
                        .fillMaxWidth(0.8f)
                        .padding(8.dp),
                    value = number,
                    onValueChange = {
                        number = it
                    },
                    label = { Text("Enter SMS Contact") },
                    enabled = enabled,
                    placeholder = {
                        Text("+39 000 000 0000" )
                    }
                )
                IconButton(
                    modifier = modifier
                        .padding(8.dp),
                    onClick = {
                        smsContactsViewModel.addSmsContact(number.replace(" ", ""))
                        number = ""
                    },
                    enabled = enabled,
                ) {
                    Icon(
                        modifier = modifier,
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add SMS Contact",
                    )
                }
            }
        }
        if (smsContacts.isEmpty()) {
            Text(
                modifier = modifier
                    .padding(16.dp),
                text = "No SMS contacts added.",
                textAlign = TextAlign.Center,
            )
            return@Column
        }
        LazyColumn(
            modifier = modifier
                .padding(horizontal = 8.dp)
                .padding(top = 16.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    modifier = modifier
                        .padding(8.dp),
                    text = "Registered SMS Contacts:",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            }
            items(smsContacts) {
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
                            text = it,
                        )
                        IconButton(
                            modifier = modifier,
                            onClick = {
                                smsContactsViewModel.removeSmsContact(it)
                            },
                            enabled = enabled,
                        ) {
                            Icon(
                                modifier = modifier
                                    .padding(horizontal = 8.dp),
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete SMS Contact",
                            )
                        }
                    }
                }
            }
        }
    }
}
