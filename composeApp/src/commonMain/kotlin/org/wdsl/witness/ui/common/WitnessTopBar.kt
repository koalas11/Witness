package org.wdsl.witness.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.wdsl.witness.ui.navigation.ScreenRoute
import org.wdsl.witness.viewmodel.AppViewModel
import witness.composeapp.generated.resources.Res
import witness.composeapp.generated.resources.app_name
import witness.composeapp.generated.resources.mobile_sound_2

/**
 * A top app bar composable for the Witness application.
 *
 * @param modifier The modifier to be applied to the top app bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WitnessTopBar(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                modifier = modifier,
                text = stringResource(Res.string.app_name),
            )
        },
        actions = {
            IconButton(
                modifier = modifier,
                onClick = {
                    appViewModel.navigateTo(ScreenRoute.EmergencySound)
                },
            ) {
                Icon(
                    modifier = modifier,
                    imageVector = vectorResource(Res.drawable.mobile_sound_2),
                    contentDescription = "Emergency Sound",
                )
            }
        },
        navigationIcon = {
            if (appViewModel.shouldShowBackButton()) {
                IconButton(
                    modifier = modifier,
                    onClick = { appViewModel.navigateBack() },
                ) {
                    Icon(
                        modifier = modifier,
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back",
                    )
                }
            }
        },
    )
}
