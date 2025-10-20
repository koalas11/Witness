package org.wdsl.witness.model

import kotlinx.serialization.Serializable

/**
 * Data class representing user settings.
 */
@Serializable
data class Settings(
    var tutorialDone: Boolean = false,
    var enableDynamicTheme: Boolean = true,
)
