package org.wdsl.witness.llm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class representing a segment of LLM (Large Language Model) output.
 *
 * @speaker The speaker associated with the segment.
 * @timestamp The timestamp of the segment.
 * @content The content of the segment.
 * @language The language of the segment.
 * @languageCode The language code of the segment.
 * @translation An optional translation of the segment.
 * @emotion The emotion associated with the segment.
 */
@Serializable
data class LlmSegment(
    val speaker: String,
    val timestamp: String,
    val content: String,
    val language: String,
    @SerialName("language_code") val languageCode: String,
    val translation: String? = null,
    val emotion: Emotion,
)
