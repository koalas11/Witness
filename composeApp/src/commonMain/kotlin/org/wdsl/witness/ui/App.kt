package org.wdsl.witness.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.wdsl.witness.LocalNotificationsSetting
import org.wdsl.witness.ui.navigation.NavHandler
import org.wdsl.witness.ui.theme.WitnessTheme
import org.wdsl.witness.viewmodel.AppState
import org.wdsl.witness.viewmodel.AppViewModel
import org.wdsl.witness.viewmodel.witnessViewModel

/**
 * The main composable function for the Witness application.
 *
 * @param modifier The modifier to be applied to the App composable.
 * @param appViewModel The ViewModel for the application.
 */
@Composable
fun App(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel = witnessViewModel(factory = AppViewModel.Factory),
) {
    LaunchedEffect(Unit) {
        appViewModel.initialize()
    }

    val settingsState by appViewModel.settingsState.collectAsStateWithLifecycle()

    when (settingsState) {
        is AppState.Loading -> {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(
                    modifier = modifier
                        .size(48.dp)
                        .padding(bottom = 16.dp),
                )
                Text(
                    modifier = modifier,
                    text = "Loading...",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
        is AppState.Error -> {
            val errorMsg = (settingsState as AppState.Error).error.message
            Text(
                modifier = modifier,
                text = "Error: $errorMsg"
            )
        }
        is AppState.Success -> {
            WitnessTheme(
                appViewModel = appViewModel,
            ) {
                val notificationsSetting by appViewModel.notificationsSettingState.collectAsStateWithLifecycle()
                CompositionLocalProvider(LocalNotificationsSetting provides notificationsSetting) {
                    NavHandler(
                        modifier = modifier,
                        appViewModel = appViewModel,
                    )
                }
            }
        }
    }
}
