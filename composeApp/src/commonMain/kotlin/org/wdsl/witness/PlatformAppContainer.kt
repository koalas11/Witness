package org.wdsl.witness

import org.wdsl.witness.module.SoundAlertModule
import org.wdsl.witness.module.VibrationModule
import org.wdsl.witness.module.audio.AudioPlayerModule
import org.wdsl.witness.module.audio.AudioRecorderModule
import org.wdsl.witness.util.CryptoManager

/**
 * Platform-specific AppContainer interface.
 */
interface PlatformAppContainer: AppContainer {
    /**
     * Platform-specific CryptoManager instance.
     */
    val cryptoManager: CryptoManager

    /**
     * Platform-specific VibrationModule instance.
     */
    val vibrationModule: VibrationModule

    /**
     * Platform-specific SoundAlertModule instance.
     */
    val soundAlertModule: SoundAlertModule

    /**
     * Add other platform-specific modules or services here.
     */
    val audioRecorderModule: AudioRecorderModule

    /**
     * Platform-specific AudioPlayerModule instance.
     */
    val audioPlayerModule: AudioPlayerModule
}
