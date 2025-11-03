package org.wdsl.witness.model

import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import witness.composeapp.generated.resources.Res
import witness.composeapp.generated.resources.dark_mode
import witness.composeapp.generated.resources.light_mode
import witness.composeapp.generated.resources.system_default

@Serializable
enum class ThemeMode(
    val label: StringResource,
) {
    LIGHT(Res.string.light_mode),
    DARK(Res.string.dark_mode),
    SYSTEM_DEFAULT(Res.string.system_default),
}
