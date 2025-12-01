package org.wdsl.witness.ui.common

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.wdsl.witness.ui.navigation.MainRoute
import org.wdsl.witness.viewmodel.AppViewModel

@Composable
fun WitnessBottomBar(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel,
) {
    val backStack by appViewModel.backStack.collectAsStateWithLifecycle()
    val shouldShowBackButton by appViewModel.shouldShowBackButton.collectAsStateWithLifecycle()
    if (shouldShowBackButton) {
        return
    }
    NavigationBar(
        modifier = modifier,
    ) {
        MainRoute.entries.forEach { entry ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = entry.icon,
                        contentDescription = if (entry.contentDescription != null) stringResource(entry.contentDescription) else null,
                    )
                },
                label = {
                    Text(
                        text = stringResource(entry.label),
                    )
                },
                selected = backStack.last() == entry.route,
                onClick = {
                    appViewModel.navigateTo(entry.route)
                }
            )
        }
    }
}
