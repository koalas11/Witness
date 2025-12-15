package org.wdsl.witness.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LlmSummary(
    val summary: String,
    val segments: List<LlmSegment>,
)

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

@Serializable
enum class Emotion {
    @SerialName("happy")
    HAPPY,

    @SerialName("sad")
    SAD,

    @SerialName("angry")
    ANGRY,

    @SerialName("neutral")
    NEUTRAL,
}
