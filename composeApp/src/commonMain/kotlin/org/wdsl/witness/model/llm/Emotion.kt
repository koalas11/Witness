package org.wdsl.witness.model.llm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents different emotions.
 */
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
