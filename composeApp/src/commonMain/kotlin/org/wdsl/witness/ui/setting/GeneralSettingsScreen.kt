package org.wdsl.witness.ui.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.wdsl.witness.model.settings.DynamicColorMode
import org.wdsl.witness.model.settings.NotificationsSetting
import org.wdsl.witness.model.settings.ThemeMode
import org.wdsl.witness.ui.util.fastUIActions
import org.wdsl.witness.ui.util.handleOperationState
import org.wdsl.witness.viewmodel.AppState
import org.wdsl.witness.viewmodel.AppViewModel
import org.wdsl.witness.viewmodel.SettingsViewModel
import org.wdsl.witness.viewmodel.witnessViewModel

/**
 * A composable function that displays the general settings screen.
 *
 * @param modifier The modifier to be applied to the composable.
 * @param appViewModel The ViewModel that holds the application state.
 * @param settingsViewModel The ViewModel that holds the settings state.
 */
@Composable
fun GeneralSettingsScreen(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel,
    settingsViewModel: SettingsViewModel = witnessViewModel(factory = SettingsViewModel.Factory),
) {
    fastUIActions.ForceScreenOrientation(1)
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val enabled = handleOperationState(
            viewModel = settingsViewModel,
        )
        Card(
            modifier = modifier
                .padding(16.dp),

        ) {
            Text(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                text = "General Settings",
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center
            )
            val appState by appViewModel.settingsState.collectAsStateWithLifecycle()
            val settings = (appState as AppState.Success).settings

            Text(
                modifier = modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
                text = "Dynamic Color Mode:",
            )
            SingleChoiceSegmentedButtonRow(
                modifier = modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
            ) {
                DynamicColorMode.entries.forEachIndexed { index, entry ->
                    SegmentedButton(
                        modifier = modifier,
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = DynamicColorMode.entries.size
                        ),
                        onClick = {
                            settingsViewModel.setDynamicColorMode(entry)
                        },
                        selected = settings.dynamicColorMode == entry,
                        label = {
                            Text(
                                modifier = modifier,
                                text = stringResource(entry.label),
                            )
                        },
                        enabled = enabled,
                    )
                }
            }

            Text(
                modifier = modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
                text = "Theme Mode:",
            )
            SingleChoiceSegmentedButtonRow(
                modifier = modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
            ) {
                ThemeMode.entries.forEachIndexed { index, entry ->
                    SegmentedButton(
                        modifier = modifier,
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = ThemeMode.entries.size
                        ),
                        onClick = {
                            settingsViewModel.setThemeMode(entry)
                        },
                        selected = settings.themeMode == entry,
                        label = {
                            Text(
                                modifier = modifier,
                                text = stringResource(entry.label),
                            )
                        },
                        enabled = enabled,
                    )
                }
            }

            Text(
                modifier = modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
                text = "Notifications Setting:",
            )
            SingleChoiceSegmentedButtonRow(
                modifier = modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
            ) {
                NotificationsSetting.entries.forEachIndexed { index, entry ->
                    SegmentedButton(
                        modifier = modifier,
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = NotificationsSetting.entries.size
                        ),
                        onClick = {
                            settingsViewModel.setNotificationsSetting(entry)
                        },
                        selected = settings.notificationsSetting == entry,
                        label = {
                            Text(
                                modifier = modifier,
                                text = stringResource(entry.label),
                            )
                        },
                        enabled = enabled,
                    )
                }
            }
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    modifier = modifier,
                    checked = settings.enableVibrationOnEmergencyRegistrationStart,
                    onCheckedChange = { isChecked ->
                        settingsViewModel.setEnableVibrationOnEmergencyRegistrationStart(isChecked)
                    },
                    enabled = enabled,
                )
                Text(
                    modifier = modifier,
                    text = "Enable Vibration On Registration Start",
                )
            }

            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    modifier = modifier,
                    checked = settings.enableRoutineContactContacts,
                    onCheckedChange = { isChecked ->
                        settingsViewModel.setEnableRoutineContactContacts(isChecked)
                    },
                    enabled = enabled,
                )
                Text(
                    modifier = modifier,
                    text = "Send Message to Contacts every 5 minutes during Emergency",
                )
            }

            Spacer(modifier = modifier.padding(4.dp))
            Text(
                modifier = modifier
                    .padding(8.dp),
                text = "Show Tutorial (Requires App Restart)",
                textAlign = TextAlign.Center,
            )
            Button(
                modifier = modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = {
                    settingsViewModel.setTutorialDone(false)
                },
                enabled = enabled,
            ) {
                Text(
                    modifier = modifier,
                    text = "Show Tutorial",
                )
            }
        }
    }
}
