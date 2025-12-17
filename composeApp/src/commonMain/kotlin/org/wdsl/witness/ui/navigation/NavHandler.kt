package org.wdsl.witness.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import org.wdsl.witness.ui.DebugScreen
import org.wdsl.witness.ui.GoogleProfileScreen
import org.wdsl.witness.ui.HomeScreen
import org.wdsl.witness.ui.RecordingInfoScreen
import org.wdsl.witness.ui.RecordingSummaryScreen
import org.wdsl.witness.ui.RecordingsScreen
import org.wdsl.witness.ui.SettingsScreen
import org.wdsl.witness.ui.TutorialScreen
import org.wdsl.witness.ui.common.WitnessBottomBar
import org.wdsl.witness.ui.common.WitnessTopBar
import org.wdsl.witness.ui.setting.GeneralSettingsScreen
import org.wdsl.witness.ui.setting.SmsSettingsScreen
import org.wdsl.witness.viewmodel.AppViewModel
import org.wdsl.witness.viewmodel.EmergencySoundViewModel
import org.wdsl.witness.viewmodel.witnessViewModel

/**
 * A composable that handles navigation within the Witness application.
 *
 * @param modifier The modifier to be applied to the NavHandler.
 * @param appViewModel The ViewModel for the application.
 * @param emergencySoundViewModel The ViewModel for emergency sound functionality.
 */
@Composable
fun NavHandler(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel,
    emergencySoundViewModel: EmergencySoundViewModel = witnessViewModel(factory = EmergencySoundViewModel.Factory),
) {
    val backStack by appViewModel.backStack.collectAsStateWithLifecycle()
    val authenticated by appViewModel.authenticated.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            WitnessTopBar(
                modifier = modifier,
                appViewModel = appViewModel,
                emergencySoundViewModel = emergencySoundViewModel,
            )
        },
        bottomBar = {
            WitnessBottomBar(
                modifier = modifier,
                appViewModel = appViewModel,
            )
        },
    ) { contentPadding ->
        NavDisplay(
            modifier = modifier.padding(contentPadding),
            backStack = backStack,
            onBack = { appViewModel.navigateBack() },
            entryProvider = entryProvider {
                entry<ScreenRoute.Tutorial> {
                    TutorialScreen(
                        modifier = modifier,
                        appViewModel = appViewModel,
                    )
                }
                entry<ScreenRoute.Home> {
                    HomeScreen(
                        modifier = modifier,
                        appViewModel = appViewModel,
                        emergencySoundViewModel = emergencySoundViewModel,
                    )
                }
                entry<ScreenRoute.Settings> {
                    SettingsScreen(
                        modifier = modifier,
                        appViewModel = appViewModel,
                    )
                }
                entry<ScreenRoute.Recordings> {
                    AuthenticatedEntry(
                        requiredRoute = it,
                        backStack = backStack,
                        authenticated = authenticated,
                        appViewModel = appViewModel
                    ) {
                        RecordingsScreen(modifier = modifier, appViewModel = appViewModel)
                    }
                }
                entry<ScreenRoute.RecordingInfo> {
                    RecordingInfoScreen(
                        modifier = modifier,
                        recordingId = it.recordingId,
                        appViewModel = appViewModel,
                    )
                }
                entry<ScreenRoute.RecordingSummary> {
                    RecordingSummaryScreen(
                        modifier = modifier,
                        recordingId = it.recordingId,
                    )
                }
                entry<ScreenRoute.GoogleProfile> {
                    AuthenticatedEntry(
                        requiredRoute = it,
                        backStack = backStack,
                        authenticated = authenticated,
                        appViewModel = appViewModel
                    ) {
                        GoogleProfileScreen(
                            modifier = modifier,
                            appViewModel = appViewModel,
                        )
                    }
                }
                entry<ScreenRoute.SmsSettings> {
                    AuthenticatedEntry(
                        requiredRoute = it,
                        backStack = backStack,
                        authenticated = authenticated,
                        appViewModel = appViewModel
                    ) {
                        SmsSettingsScreen(
                            modifier = modifier,
                            appViewModel = appViewModel,
                        )
                    }
                }
                entry<ScreenRoute.GeneralSettings> {
                    GeneralSettingsScreen(
                        modifier = modifier,
                        appViewModel = appViewModel,
                    )
                }
                entry<ScreenRoute.DebugScreen> {
                    DebugScreen(
                        modifier = modifier,
                    )
                }
            }
        )
    }
}
