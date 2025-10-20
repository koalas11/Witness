package org.wdsl.witness.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Sealed interface representing the different routes in the Witness application.
 */
interface Routes : NavKey {
    @Serializable
    object Home: Routes
    object Unknown: Routes
}
