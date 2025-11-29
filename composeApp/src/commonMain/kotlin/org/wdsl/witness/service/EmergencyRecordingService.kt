package org.wdsl.witness.service

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.wdsl.witness.PlatformAppContainer
import org.wdsl.witness.WitnessApp
import org.wdsl.witness.model.LocationData
import org.wdsl.witness.state.EmergencyServiceState
import org.wdsl.witness.storage.room.Recording
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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
        EmergencyServiceState.setEmergencyServiceState(EmergencyServiceState.State.Running)

        val audioRecorderModule = witnessApp.appContainer.audioRecorderModule
        val recordingFileName = audioRecorderModule.startRecording().getSuccessOrNull()

        if (recordingFileName == null) {
            stopService()
            return
        }

        val geoRecordingModule = witnessApp.appContainer.geoRecordingModule
        geoRecordingModule.startGeoRecording()

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
    }

    suspend fun onEmergencyRecordingStarted() {}

    @OptIn(ExperimentalTime::class)
    fun saveEmergencyRecording(
        recordingFileName: String,
        gpsPositions: List<LocationData>,
    ) {
        val timestamp = recordingFileName.subSequence("recording_".length, recordingFileName.lastIndexOf(".")).toString().toLong()

        val instant = Instant.fromEpochMilliseconds(timestamp)
        val ldt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val timestampPrettyString = "${ldt.year}-${ldt.month.number.toString().padStart(2, '0')}-${
            ldt.day.toString().padStart(2, '0')
        } " +
                "${ldt.hour.toString().padStart(2, '0')}:${
                    ldt.minute.toString().padStart(2, '0')
                }:${ldt.second.toString().padStart(2, '0')}"

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
    }
}