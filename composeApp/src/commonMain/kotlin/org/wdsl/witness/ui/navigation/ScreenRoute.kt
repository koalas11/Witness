package org.wdsl.witness.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import witness.composeapp.generated.resources.Res
import witness.composeapp.generated.resources.home_screen_label
import witness.composeapp.generated.resources.recordings_screen_label
import witness.composeapp.generated.resources.settings_screen_label

/**
 * Marker interface indicating that a screen should show a back button in the top app bar.
 */
interface ShowBackButton

/**
 * Marker interface indicating that a screen should be removed from the back stack when left.
 */
interface RemoveOnLeave

/**
 * Sealed interface representing the different routes in the Witness application.
 */
interface ScreenRoute : NavKey {
    @Serializable
    object Tutorial: ScreenRoute, RemoveOnLeave

    @Serializable
    object Home: ScreenRoute

    @Serializable
    object Settings: ScreenRoute

    @Serializable
    object Recordings: ScreenRoute

    @Serializable
    object GeneralSettings: ScreenRoute, ShowBackButton, RemoveOnLeave

    @Serializable
    object GoogleProfile: ScreenRoute, ShowBackButton, RemoveOnLeave

    @Serializable
    object SmsSettings: ScreenRoute, ShowBackButton, RemoveOnLeave

    @Serializable
    object DebugScreen: ScreenRoute, ShowBackButton, RemoveOnLeave

    @Serializable
    data class RecordingInfo(val recordingId: Long): ScreenRoute, ShowBackButton

    @Serializable
    data class RecordingSummary(val recordingId: Long): ScreenRoute, ShowBackButton, RemoveOnLeave
}

/**
 * Enum class representing the main routes in the bottom navigation bar.
 *
 * @property route The corresponding [ScreenRoute] for the navigation item.
 * @property label The label resource for the navigation item.
 * @property icon The icon to be displayed for the navigation item.
 * @property contentDescription The content description resource for accessibility, if any.
 */
enum class MainRoute(
    val route: ScreenRoute,
    val label: StringResource,
    val icon: ImageVector,
    val contentDescription: StringResource?,
) {
    HOME(ScreenRoute.Home, Res.string.home_screen_label, Icons.Default.Home, null),
    RECORDINGS(ScreenRoute.Recordings, Res.string.recordings_screen_label, Icons.Default.LibraryMusic, null),
    SETTINGS(ScreenRoute.Settings, Res.string.settings_screen_label, Icons.Default.Settings, null),
}
