package org.wdsl.witness.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.wdsl.witness.storage.room.Recording

/**
 * A composable that represents a single item in a list of recordings.
 *
 * @param modifier The modifier to be applied to the RecordingListItem.
 * @param recording The recording data to be displayed.
 * @param onClick The callback to be invoked when the item is clicked.
 */
@Composable
fun RecordingListItem(
    modifier: Modifier = Modifier,
    recording: Recording,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        onClick = { onClick() },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = recording.title,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
