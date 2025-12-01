package org.wdsl.witness.ui.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import org.wdsl.witness.model.DynamicColorMode
import org.wdsl.witness.model.NotificationsSetting
import org.wdsl.witness.model.ThemeMode
import org.wdsl.witness.ui.util.handleOperationState
import org.wdsl.witness.viewmodel.AppState
import org.wdsl.witness.viewmodel.AppViewModel
import org.wdsl.witness.viewmodel.SettingsViewModel
import org.wdsl.witness.viewmodel.witnessViewModel

@Composable
fun GeneralSettingsScreen(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel,
    settingsViewModel: SettingsViewModel = witnessViewModel(factory = SettingsViewModel.Factory),
) {
    Column(
        modifier = modifier,
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
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    modifier = modifier,
                    text = "Enable Vibration:",
                    textAlign = TextAlign.Center,
                )
                Checkbox(
                    modifier = modifier,
                    checked = settings.enableVibrationOnEmergencyRegistrationStart,
                    onCheckedChange = { isChecked ->
                        settingsViewModel.setEnableVibrationOnEmergencyRegistrationStart(isChecked)
                    },
                    enabled = enabled,
                )
            }
        }
    }
}
