package org.wdsl.witness.service

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.wdsl.witness.PlatformAppContainer
import org.wdsl.witness.PlatformContext
import org.wdsl.witness.WitnessApp
import org.wdsl.witness.model.LocationData
import org.wdsl.witness.state.EmergencyServiceState
import org.wdsl.witness.storage.room.Recording
import org.wdsl.witness.util.EMERGENCY_NOTIFICATION_CHANNEL_ID
import org.wdsl.witness.util.getFormattedTimestamp

/**
 * Interface defining the emergency recording service functionality.
 */
interface EmergencyRecordingService {
    /**
     * Job representing the ongoing emergency recording process.
     */
    var serviceJob: Job?

    /**
     * Reference to the platform-specific context.
     */
    val platformContext: PlatformContext

    /**
     * Reference to the WitnessApp instance.
     */
    val witnessApp: WitnessApp

    /**
     * Reference to the platform-specific application container.
     */
    val appContainer: PlatformAppContainer

    /**
     * The default coroutine dispatcher for executing tasks.
     */
    var defaultDispatcher: CoroutineDispatcher

    /**
     * Starts the emergency recording process.
     *
     * @param stopService A lambda function to stop the service.
     */
    fun startEmergencyRecording(
        stopService: () -> Unit,
    ) {
        if (!witnessApp.appContainer.emergencyRecordingUseCase.emergencyRecordingActive.value) {
            stopService()
            return
        }

        if (!platformContext.checkRequiredPermissionsForEmergencyRecording()) {
            platformContext.sendNotification(
                channelId = EMERGENCY_NOTIFICATION_CHANNEL_ID,
                title = "Emergency Recording",
                message = "Emergency recording failed to start due to missing permissions, emergency contacts will still be notified.",
                priority = 2,
            )
            stopService()
            return
        }

        runCatching {
            val audioRecorderModule = witnessApp.appContainer.audioRecorderModule
            val recordingFileName = audioRecorderModule.startRecording().getSuccessOrNull()

            if (recordingFileName == null) {
                stopService()
                return
            }

            val geoRecordingModule = witnessApp.appContainer.geoRecordingModule
            geoRecordingModule.startGeoRecording()

            EmergencyServiceState.setEmergencyServiceState(EmergencyServiceState.State.Running)
            serviceJob = witnessApp.appScope.launch(defaultDispatcher) {
                witnessApp.appContainer.emergencyRecordingUseCase.emergencyRecordingActive.collect {
                    if (!it) {
                        audioRecorderModule.stopRecording()
                        geoRecordingModule.stopGeoRecording()
                        saveEmergencyRecording(
                            recordingFileName,
                            geoRecordingModule.getGeoRecordings(),
                        )
                        stopService()
                    }
                }
            }
        }.onFailure {
            EmergencyServiceState.setEmergencyServiceState(EmergencyServiceState.State.Error("Failed to start emergency recording"))
            stopService()
        }
    }

    /**
     * Saves the emergency recording along with GPS positions.
     *
     * @param recordingFileName The name of the recording file.
     * @param gpsPositions The list of GPS positions recorded during the emergency.
     */
    fun saveEmergencyRecording(
        recordingFileName: String,
        gpsPositions: List<LocationData>,
    ) {
        val timestamp = recordingFileName.subSequence(
            "recording_".length, recordingFileName.lastIndexOf(".")
        ).toString().toLong()
        val timestampPrettyString = getFormattedTimestamp(timestamp)

        val recording = Recording(
            title = "Emergency Recording: $timestampPrettyString",
            recordingFileName = recordingFileName,
            gpsPositions = gpsPositions,
        )
        witnessApp.appScope.launch(defaultDispatcher) {
            appContainer.recordingsRepository.insertRecording(
                recording
            ).getSuccessOrNull() ?: run {
                EmergencyServiceState.setEmergencyServiceState(EmergencyServiceState.State.Error("Failed to save emergency recording"))
            }
            EmergencyServiceState.setEmergencyServiceState(EmergencyServiceState.State.Idle)
        }
        witnessApp.appScope.launch(defaultDispatcher) {
            if (witnessApp.appContainer.settingsRepository.getUploadToDriveEnabled().getSuccessOrNull() == true) {
                appContainer.googleIntegrationUseCase.uploadRecordingToGoogleDrive(
                    recording = recording,
                )
            }
        }
    }
}
