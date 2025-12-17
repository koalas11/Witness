package org.wdsl.witness.module

/**
 * Common interface for handling the emergency recording service.
 */
interface RecordingServiceHandler {
    /**
     * Starts the emergency recording service.
     */
    fun startEmergencyRecordingService()

    /**
     * Stops the emergency recording service.
     */
    fun stopEmergencyRecordingService()
}
