package org.wdsl.witness.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.wdsl.witness.storage.room.Recording

@Composable
fun RecordingListItem(
    modifier: Modifier = Modifier,
    recording: Recording,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .padding(horizontal = 8.dp),
        onClick = { onClick() },
    ) {
        Text(
            modifier = modifier,
            text = recording.title,
        )
    }
}
