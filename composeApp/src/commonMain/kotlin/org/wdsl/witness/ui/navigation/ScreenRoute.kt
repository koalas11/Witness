package org.wdsl.witness.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import witness.composeapp.generated.resources.Res
import witness.composeapp.generated.resources.home_screen_label
import witness.composeapp.generated.resources.settings_screen_label

/**
 * Sealed interface representing the different routes in the Witness application.
 */
interface ScreenRoute : NavKey {
    @Serializable
    object Home: ScreenRoute

    @Serializable
    object Settings: ScreenRoute
}

enum class MainRoute(
    val route: ScreenRoute,
    val label: StringResource,
    val icon: ImageVector,
    val contentDescription: StringResource?,
) {
    HOME(ScreenRoute.Home, Res.string.home_screen_label, Icons.Default.Home, null),
    SETTINGS(ScreenRoute.Settings, Res.string.settings_screen_label, Icons.Default.Settings, null),
}