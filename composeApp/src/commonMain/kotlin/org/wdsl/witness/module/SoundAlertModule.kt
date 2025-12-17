package org.wdsl.witness.module

import org.wdsl.witness.util.Result

/**
 * Common interface for sound alert functionality.
 */
interface SoundAlertModule {
    /**
     * Plays an alert sound.
     *
     * @return Result indicating success or failure.
     */
    fun playAlertSound(): Result<Unit>

    /**
     * Stops the alert sound if it is playing.
     *
     * @return Result indicating success or failure.
     */
    fun stopAlertSound(): Result<Unit>
}
