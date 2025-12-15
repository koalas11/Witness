package org.wdsl.witness

import kotlinx.coroutines.CoroutineScope

/**
 * Common interface for the Witness application across platforms.
 */
interface WitnessApp {
    /**
     * The platform-specific application container.
     */
    val appContainer: PlatformAppContainer

    /**
     * The application-wide CoroutineScope.
     */
    val appScope: CoroutineScope
}
