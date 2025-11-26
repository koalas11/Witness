package org.wdsl.witness

import kotlinx.coroutines.CoroutineScope

/**
 * Common interface for the Witness application across platforms.
 */
interface WitnessApp {
    val appContainer: PlatformAppContainer
    val appScope: CoroutineScope
}
