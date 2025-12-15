package org.wdsl.witness.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.painterResource
import org.wdsl.witness.LocalPlatformContext
import org.wdsl.witness.model.Emotion
import org.wdsl.witness.ui.util.handleOperationState
import org.wdsl.witness.viewmodel.RecordingSummaryUiState
import org.wdsl.witness.viewmodel.RecordingSummaryViewModel
import org.wdsl.witness.viewmodel.witnessViewModel
import witness.composeapp.generated.resources.Res
import witness.composeapp.generated.resources.gemini
import witness.composeapp.generated.resources.sentiment_extremely_dissatisfied
import witness.composeapp.generated.resources.sentiment_sad

@Composable
fun RecordingSummaryScreen(
    modifier: Modifier = Modifier,
    recordingId: Long,
    recordingSummaryViewModel: RecordingSummaryViewModel = witnessViewModel(factory = RecordingSummaryViewModel.Factory),
) {
    LaunchedEffect(recordingId) {
        recordingSummaryViewModel.initialize(recordingId)
    }
    val recordingSummaryUiState by recordingSummaryViewModel.recordingSummaryUiState.collectAsStateWithLifecycle()
    if (recordingSummaryUiState is RecordingSummaryUiState.Loading) {
        CircularProgressIndicator(
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth(),
        )
        return
    } else if (recordingSummaryUiState is RecordingSummaryUiState.Error) {
        Text(
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth(),
            text = (recordingSummaryUiState as RecordingSummaryUiState.Error).message,
            textAlign = TextAlign.Center,
        )
        return
    }
    val selectedRecording = (recordingSummaryUiState as RecordingSummaryUiState.Loaded).recording
    val enabled = handleOperationState(
        viewModel = recordingSummaryViewModel,
    )
    Card(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                text = selectedRecording.title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge,
            )

            val text = if (selectedRecording.llmSummary != null) {
                "Summary: ${selectedRecording.llmSummary.summary}"
            } else {
                "No Summary Available"
            }
            Text(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                text = text,
                style = MaterialTheme.typography.bodyMedium,
            )

            val segments = selectedRecording.llmSummary?.segments
            if (segments != null) {
                var selectedIdx by rememberSaveable { mutableStateOf(0) }
                Card(
                    modifier = modifier
                        .padding(8.dp)
                        .fillMaxSize(),
                    colors = CardDefaults.cardColors().copy(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                ) {
                    Row(
                        modifier = modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            modifier = modifier
                                .padding(4.dp),
                            onClick = {
                                selectedIdx = (selectedIdx - 1).coerceAtLeast(0)
                            },
                            enabled = enabled,
                        ) {
                            Icon(
                                modifier = modifier,
                                imageVector = Icons.AutoMirrored.Default.ArrowLeft,
                                contentDescription = null,
                            )
                        }

                        Text(
                            modifier = modifier
                                .padding(4.dp)
                                .weight(1f),
                            text = "Segment ${selectedIdx + 1} / ${segments.size}",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                        )

                        IconButton(
                            modifier = modifier
                                .padding(4.dp),
                            onClick = {
                                selectedIdx = (selectedIdx + 1).coerceAtMost(segments.size - 1)
                            },
                            enabled = enabled,
                        ) {
                            Icon(
                                modifier = modifier,
                                imageVector = Icons.AutoMirrored.Default.ArrowRight,
                                contentDescription = null,
                            )
                        }
                    }

                    val segment = segments[selectedIdx]
                    Text(
                        modifier = modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .fillMaxWidth(),
                        text = "Speaker: ${segment.speaker}",
                    )
                    Text(
                        modifier = modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .fillMaxWidth(),
                        text = "Timestamp: ${segment.timestamp}",
                    )
                    Text(
                        modifier = modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .fillMaxWidth(),
                        text = "Content: ${segment.content}",
                    )
                    Text(
                        modifier = modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .fillMaxWidth(),
                        text = "Language: ${segment.language} (${segment.languageCode})",
                    )
                    if (segment.translation != null) {
                        Text(
                            modifier = modifier
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .fillMaxWidth(),
                            text = "Translation: ${segment.translation}",
                        )
                    }
                    val emotionIcon = when (segment.emotion) {
                        Emotion.HAPPY -> rememberVectorPainter(Icons.Default.SentimentSatisfied)
                        Emotion.SAD -> painterResource(Res.drawable.sentiment_sad)
                        Emotion.ANGRY -> painterResource(Res.drawable.sentiment_extremely_dissatisfied)
                        Emotion.NEUTRAL -> rememberVectorPainter(Icons.Default.SentimentNeutral)
                    }
                    Row(
                        modifier = modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = modifier,
                            text = "Emotion: ${segment.emotion}",
                        )
                        Icon(
                            modifier = modifier
                                .padding(start = 4.dp),
                            painter = emotionIcon,
                            contentDescription = null,
                        )
                    }
                }
            }

            Row(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
            ) {
                val platformContext = LocalPlatformContext.current
                Button(
                    modifier = modifier
                        .padding(4.dp)
                        .weight(0.5f),
                    onClick = {
                        recordingSummaryViewModel.createRecordingSummary(
                            platformContext = platformContext,
                        )
                    },
                    enabled = enabled,
                ) {
                    Row(
                        modifier = modifier,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val icon = painterResource(Res.drawable.gemini)
                        Icon(
                            modifier = modifier
                                .size(24.dp)
                                .aspectRatio(1f),
                            painter = icon,
                            contentDescription = null,
                        )
                        Text(
                            modifier = modifier.padding(start = 8.dp),
                            text = "Generate Summary"
                        )
                    }
                }
                if (selectedRecording.llmSummary == null) {
                    return@Row
                }
                Button(
                    modifier = modifier
                        .padding(4.dp)
                        .weight(0.5f),
                    onClick = {
                        recordingSummaryViewModel.deleteRecordingSummary()
                    },
                    enabled = enabled,
                ) {
                    Row(
                        modifier = modifier,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            modifier = modifier,
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                        )
                        Text(
                            modifier = modifier.padding(start = 8.dp),
                            text = "Delete Summary"
                        )
                    }
                }
            }
        }
    }
}