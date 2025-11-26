package org.wdsl.witness.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.wdsl.witness.LocalPlatformContext
import org.wdsl.witness.model.DynamicColorMode
import org.wdsl.witness.model.NotificationsSetting
import org.wdsl.witness.model.ThemeMode
import org.wdsl.witness.state.AppSettingsState
import org.wdsl.witness.ui.navigation.ScreenRoute
import org.wdsl.witness.ui.util.fastUIActions
import org.wdsl.witness.ui.util.handleOperationState
import org.wdsl.witness.viewmodel.AppState
import org.wdsl.witness.viewmodel.AppViewModel
import org.wdsl.witness.viewmodel.SettingsViewModel
import org.wdsl.witness.viewmodel.witnessViewModel

@Composable
fun SettingsScreen(
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
                    .align(Alignment.CenterHorizontally),
                text = "Try to enable/disable the dynamic theming options below:",
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
                DynamicColorMode.entries.forEachIndexed { index,  entry ->
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
        }
        val platformContext = LocalPlatformContext.current
        Button(
            modifier = modifier
                .padding(16.dp),
            onClick = {
                fastUIActions.openSystemAppSettings(platformContext)
            },
        ) {
            Text("Fast travel to App Settings")
        }
        val emergencyGesturesStatus by AppSettingsState.accessibilityServiceEnabled.collectAsStateWithLifecycle()
        Text(
            modifier = modifier
                .padding(8.dp),
            text = "Accessibility Service Status: ${emergencyGesturesStatus.name}",
        )
        Button(
            modifier = modifier
                .padding(16.dp),
            onClick = {
                fastUIActions.openAccessibilityServicesSettings(platformContext)
            },
        ) {
            Text("Fast travel to Accessibility Settings")
        }
        Button(
            modifier = modifier
                .padding(16.dp),
            onClick = {
                appViewModel.navigateTo(ScreenRoute.GoogleProfile)
            },
            enabled = enabled,
        ) {
            Text(
                modifier = modifier,
                text = "Go to Google Profile Screen",
            )
        }
    }
}
