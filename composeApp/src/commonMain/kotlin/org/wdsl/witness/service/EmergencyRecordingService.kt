package org.wdsl.witness.service

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.wdsl.witness.PlatformAppContainer
import org.wdsl.witness.WitnessApp
import org.wdsl.witness.model.LocationData
import org.wdsl.witness.state.EmergencyServiceState
import org.wdsl.witness.storage.room.Recording
import org.wdsl.witness.ui.util.getFormattedTimestamp

interface EmergencyRecordingService {
    var serviceJob: Job?
    val witnessApp: WitnessApp
    val appContainer: PlatformAppContainer

    var defaultDispatcher: CoroutineDispatcher

    fun startEmergencyRecording(
        stopService: () -> Unit,
    ) {
        if (!witnessApp.appContainer.emergencyRecordingUseCase.emergencyRecordingActive.value) {
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

    suspend fun onEmergencyRecordingStarted() {}

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
            appContainer.googleIntegrationUseCase.uploadRecordingToGoogleDrive(
                recording = recording,
            )
        }
    }
}