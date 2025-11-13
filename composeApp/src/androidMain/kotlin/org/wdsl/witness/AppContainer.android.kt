package org.wdsl.witness

import android.content.Context
import org.wdsl.witness.module.AndroidSoundAlertModule
import org.wdsl.witness.module.AndroidVibrationModule
import org.wdsl.witness.module.SoundAlertModule
import org.wdsl.witness.module.VibrationModule
import org.wdsl.witness.module.audio.AndroidAudioPlayerModule
import org.wdsl.witness.module.audio.AndroidAudioRecorderModule
import org.wdsl.witness.module.audio.AudioPlayerModule
import org.wdsl.witness.module.audio.AudioRecorderModule
import org.wdsl.witness.util.AndroidCryptoManager
import org.wdsl.witness.util.CryptoManager

/**
 * Android-specific implementation of the AppContainer.
 * @param context The Android context.
 */
class AndroidAppContainer(
    private val context: Context,
): AppContainerImpl(AndroidContext(context)), PlatformAppContainer {

    override val cryptoManager: CryptoManager by lazy {
        AndroidCryptoManager
    }

    override val vibrationModule: VibrationModule by lazy {
        AndroidVibrationModule(context)
    }

    override val soundAlertModule: SoundAlertModule by lazy {
        AndroidSoundAlertModule(context)
    }

    override val audioRecorderModule: AudioRecorderModule by lazy {
        AndroidAudioRecorderModule(context)
    }

    override val audioPlayerModule: AudioPlayerModule by lazy {
        AndroidAudioPlayerModule(context)
    }
}
