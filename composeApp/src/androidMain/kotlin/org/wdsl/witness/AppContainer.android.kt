package org.wdsl.witness

import android.content.Context
import org.wdsl.witness.module.AndroidSoundAlertModule
import org.wdsl.witness.module.AndroidVibrationModule
import org.wdsl.witness.module.SoundAlertModule
import org.wdsl.witness.module.VibrationModule

/**
 * Android-specific implementation of the AppContainer.
 * @param context The Android context.
 */
class AndroidAppContainer(
    private val context: Context,
): AppContainerImpl(AndroidContext(context)), PlatformAppContainer {

    override val vibrationModule: VibrationModule by lazy {
        AndroidVibrationModule(context)
    }

    override val soundAlertModule: SoundAlertModule by lazy {
        AndroidSoundAlertModule(context)
    }
}
