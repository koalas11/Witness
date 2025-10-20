package org.wdsl.witness.ui.common

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import witness.composeapp.generated.resources.Res
import witness.composeapp.generated.resources.app_name

/**
 * A top app bar composable for the Witness application.
 *
 * @param modifier The modifier to be applied to the top app bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WitnessTopBar(
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                modifier = modifier,
                text = stringResource(Res.string.app_name),
            )
        }
    )
}
