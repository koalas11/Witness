package org.wdsl.witness.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.vectorResource
import org.wdsl.witness.viewmodel.AppState
import org.wdsl.witness.viewmodel.AppViewModel
import witness.composeapp.generated.resources.Res
import witness.composeapp.generated.resources.robe

/**
 * A composable function that represents the Home Screen of the Witness application.
 *
 * @param modifier The modifier to be applied to the Home Screen.
 * @param appViewModel The ViewModel for the application.
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = modifier,
            text = "This is the Home Screen",
        )
        Icon(
            modifier = modifier,
            imageVector = vectorResource(Res.drawable.robe),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Card(
            modifier = modifier
                .padding(16.dp),

        ) {
            Text(
                modifier = modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
                text = "Try to enable/disable the dynamic theme by clicking the check box below",
            )
            val appState by appViewModel.settingsStateFlow.collectAsStateWithLifecycle()
            val settings = (appState as AppState.Success).settings
            Checkbox(
                modifier = modifier
                    .align(Alignment.CenterHorizontally),
                checked = settings.enableDynamicTheme,
                onCheckedChange = { isChecked ->
                    appViewModel.updateDynamicThemeSetting(isChecked)
                }
            )
        }
    }
}
