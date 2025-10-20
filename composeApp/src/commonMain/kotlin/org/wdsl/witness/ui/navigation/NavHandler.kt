package org.wdsl.witness.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import org.wdsl.witness.ui.HomeScreen
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
    val backStack = remember { mutableStateListOf<Routes>(Routes.Home) }

    Scaffold(
        modifier = modifier,
        topBar = {
            WitnessTopBar(
                modifier = modifier,
            )
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = modifier.padding(innerPadding),
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = { key ->
                when (key) {
                    is Routes.Home -> NavEntry(key) {
                        HomeScreen(
                            modifier = modifier,
                            appViewModel = appViewModel,
                        )
                    }

                    else -> NavEntry(Routes.Unknown) { Text("Unknown route") }
                }
            }
        )
    }
}
