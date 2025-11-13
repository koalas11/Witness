package org.wdsl.witness

import androidx.compose.runtime.Composable

/**
 * Common interface representing platform-specific context.
 */
interface PlatformContext {
    val context: Any
}

@Composable
expect fun getPlatformContext(): PlatformContext
