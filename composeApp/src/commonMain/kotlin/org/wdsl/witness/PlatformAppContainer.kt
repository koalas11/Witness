package org.wdsl.witness

import org.wdsl.witness.module.EmergencyContactModule
import org.wdsl.witness.module.GeoRecordingModule
import org.wdsl.witness.module.RecordingServiceHandler
import org.wdsl.witness.module.SoundAlertModule
import org.wdsl.witness.module.VibrationModule
import org.wdsl.witness.module.audio.AudioPlayerModule
import org.wdsl.witness.module.audio.AudioRecorderModule
import org.wdsl.witness.usecase.EmergencyRecordingUseCase

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

    /**
     * Add other platform-specific modules or services here.
     */
    val audioRecorderModule: AudioRecorderModule

    /**
     * Platform-specific AudioPlayerModule instance.
     */
    val audioPlayerModule: AudioPlayerModule

    /**
     * Platform-specific GeoRecordingModule instance.
     */
    val geoRecordingModule: GeoRecordingModule

    /**
     * Platform-specific EmergencyContactModule instance.
     */
    val emergencyContactModule: EmergencyContactModule

    /**
     * Platform-specific RecordingServiceHandler instance.
     */
    val recordingServiceHandler: RecordingServiceHandler

    /**
     * Platform-specific EmergencyRecordingUseCase instance.
     */
    val emergencyRecordingUseCase: EmergencyRecordingUseCase
}
