package org.wdsl.witness

import org.wdsl.witness.module.SoundAlertModule
import org.wdsl.witness.module.VibrationModule

/**
 * Platform-specific AppContainer interface.
 */
interface PlatformAppContainer: AppContainer {

    /**
     * Platform-specific VibrationModule instance.
     */
    val vibrationModule: VibrationModule

    /**
     * Platform-specific SoundAlertModule instance.
     */
    val soundAlertModule: SoundAlertModule
}
