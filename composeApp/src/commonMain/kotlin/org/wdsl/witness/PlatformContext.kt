package org.wdsl.witness

/**
 * Common interface representing platform-specific context.
 */
interface PlatformContext {
    val context: Any
    val witnessApp: WitnessApp

    fun sendNotification(channelId: String, title: String, message: String, priority: Int)
}
