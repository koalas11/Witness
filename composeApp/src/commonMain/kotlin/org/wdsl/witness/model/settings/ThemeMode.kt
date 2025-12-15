package org.wdsl.witness.model.settings

import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import witness.composeapp.generated.resources.Res
import witness.composeapp.generated.resources.dark_mode
import witness.composeapp.generated.resources.light_mode
import witness.composeapp.generated.resources.system_default

/**
 * Enum representing the theme modes available in the application.
 *
 * @property label The string resource associated with the theme mode.
 */
@Serializable
enum class ThemeMode(
    val label: StringResource,
) {
    LIGHT(Res.string.light_mode),
    DARK(Res.string.dark_mode),
    SYSTEM_DEFAULT(Res.string.system_default),
}