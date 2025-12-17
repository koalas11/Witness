package org.wdsl.witness.module

/**
 * Common interface for vibration functionality.
 */
interface VibrationModule {
    /**
     * Checks if the device supports vibration.
     * @return True if vibration is supported, false otherwise.
     */
    fun supportVibration(): Boolean

    /**
     * Triggers a vibration for the specified duration in milliseconds.
     *
     * @param durationMs The duration of the vibration in milliseconds.
     */
    fun vibrate(durationMs: Long)

    /**
     * Triggers a vibration pattern.
     *
     * @param pattern An array of longs representing the vibration pattern.
     */
    fun vibratePattern(pattern: LongArray)
}
