package org.wdsl.witness.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import org.wdsl.witness.ui.GoogleProfileScreen
import org.wdsl.witness.ui.HomeScreen
import org.wdsl.witness.ui.SettingsScreen
import org.wdsl.witness.ui.common.WitnessBottomBar
import org.wdsl.witness.ui.common.WitnessTopBar
import org.wdsl.witness.viewmodel.AppViewModel

/**
 * A composable that handles navigation within the Witness application.
 *
 * @param modifier The modifier to be applied to the NavHandler.
 * @param appViewModel The ViewModel for the application.
 */
@Composable
fun NavHandler(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel,
) {
    val backStack by appViewModel.backStack.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            WitnessTopBar(
                modifier = modifier,
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
                entry<ScreenRoute.Home> {
                    HomeScreen(
                        modifier = modifier,
                        appViewModel = appViewModel,
                    )
                }
                entry<ScreenRoute.Settings> {
                    SettingsScreen(
                        modifier = modifier,
                        appViewModel = appViewModel,
                    )
                }
                entry<ScreenRoute.GoogleProfile> {
                    GoogleProfileScreen(
                        modifier = modifier,
                    )
                }
            }
        )
    }
}
