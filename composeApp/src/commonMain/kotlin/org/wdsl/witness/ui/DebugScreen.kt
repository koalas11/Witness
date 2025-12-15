package org.wdsl.witness.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.wdsl.witness.LocalPlatformContext
import org.wdsl.witness.ui.util.handleOperationState
import org.wdsl.witness.viewmodel.DebugViewModel
import org.wdsl.witness.viewmodel.witnessViewModel

/**
 * A composable function that displays the debug screen.
 *
 * @param modifier The modifier to be applied to the composable.
 * @param debugViewModel The ViewModel that holds the debug state.
 */
@Composable
fun DebugScreen(
    modifier: Modifier = Modifier,
    debugViewModel: DebugViewModel = witnessViewModel(factory = DebugViewModel.Factory),
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val enabled = handleOperationState(
            viewModel = debugViewModel,
        )
        Card(
            modifier = modifier
                .padding(16.dp),

        ) {
            val platformContext = LocalPlatformContext.current
            Row(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(0.9f)
                    .align(Alignment.Start)
                    .clip(CircleShape)
                    .clickable(
                        enabled = enabled,
                        onClick = {
                            debugViewModel.clearAllRecordings(platformContext)
                        },
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = modifier
                        .padding(8.dp),
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                )
                Text(
                    modifier = modifier
                        .padding(8.dp),
                    text = "Delete all recordings",
                )
            }
        }
    }
}
