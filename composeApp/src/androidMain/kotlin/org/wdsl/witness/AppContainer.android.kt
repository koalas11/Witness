package org.wdsl.witness

import android.content.Context
import org.wdsl.witness.module.AndroidEmergencyContactModule
import org.wdsl.witness.module.AndroidGeoRecordingModule
import org.wdsl.witness.module.AndroidSoundAlertModule
import org.wdsl.witness.module.AndroidVibrationModule
import org.wdsl.witness.module.EmergencyContactModule
import org.wdsl.witness.module.GeoRecordingModule
import org.wdsl.witness.module.RecordingServiceHandler
import org.wdsl.witness.module.RecordingServiceHandlerImpl
import org.wdsl.witness.module.SoundAlertModule
import org.wdsl.witness.module.VibrationModule
import org.wdsl.witness.module.audio.AndroidAudioPlayerModule
import org.wdsl.witness.module.audio.AndroidAudioRecorderModule
import org.wdsl.witness.module.audio.AudioPlayerModule
import org.wdsl.witness.module.audio.AudioRecorderModule
import org.wdsl.witness.usecase.EmergencyRecordingUseCase
import org.wdsl.witness.usecase.EmergencyRecordingUseCaseImpl
import org.wdsl.witness.util.AndroidCryptoManager
import org.wdsl.witness.util.CryptoManager

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

    override val audioRecorderModule: AudioRecorderModule by lazy {
        AndroidAudioRecorderModule(context)
    }

    override val audioPlayerModule: AudioPlayerModule by lazy {
        AndroidAudioPlayerModule(context)
    }

    override val geoRecordingModule: GeoRecordingModule by lazy {
        AndroidGeoRecordingModule(context)
    }

    override val emergencyContactModule: EmergencyContactModule by lazy {
        AndroidEmergencyContactModule(context)
    }

    override val recordingServiceHandler: RecordingServiceHandler by lazy {
        RecordingServiceHandlerImpl(context)
    }

    override val emergencyRecordingUseCase: EmergencyRecordingUseCase by lazy {
        EmergencyRecordingUseCaseImpl(
            recordingServiceHandler = recordingServiceHandler,
            emergencyContactModule = emergencyContactModule,
            geoRecordingModule = geoRecordingModule,
        )
    }
}
