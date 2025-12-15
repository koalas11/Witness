package org.wdsl.witness.llm

import kotlinx.serialization.Serializable

/**
 * Represents the response from a large language model (LLM) summarization.
 *
 * @property summary The overall summary text.
 * @property segments The list of detailed segments in the summary.
 */
@Serializable
data class LlmSummary(
    val summary: String,
    val segments: List<LlmSegment>,
)
