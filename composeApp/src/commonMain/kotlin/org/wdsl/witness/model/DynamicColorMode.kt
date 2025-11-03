package org.wdsl.witness.model

import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import witness.composeapp.generated.resources.Res
import witness.composeapp.generated.resources.disabled
import witness.composeapp.generated.resources.enabled

@Serializable
enum class DynamicColorMode(
    val label: StringResource,
) {
    ENABLED(Res.string.enabled),
    DISABLED(Res.string.disabled),
}
