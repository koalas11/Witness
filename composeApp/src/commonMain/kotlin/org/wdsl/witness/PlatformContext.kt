package org.wdsl.witness

/**
 * Common interface representing platform-specific context.
 */
interface PlatformContext {
    /**
     * The underlying platform-specific context object.
     */
    val context: Any

    /**
     * The Witness application instance for platform-specific operations.
     */
    val witnessApp: WitnessApp

    /**
     * Sends a notification using the platform-specific notification system.
     *
     * @param channelId The ID of the notification channel.
     * @param title The title of the notification.
     * @param message The message content of the notification.
     * @param priority The priority level of the notification.
     */
    fun sendNotification(channelId: String, title: String, message: String, priority: Int)

    /**
     * Checks if the required permissions for emergency recording are granted.
     *
     * @return True if all required permissions are granted, false otherwise.
     */
    fun checkRequiredPermissionsForEmergencyRecording(): Boolean
}
