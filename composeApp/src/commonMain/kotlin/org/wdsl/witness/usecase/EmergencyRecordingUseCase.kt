package org.wdsl.witness.usecase

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.wdsl.witness.module.EmergencyContactModule
import org.wdsl.witness.module.GeoRecordingModule
import org.wdsl.witness.module.RecordingServiceHandler
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.Result

interface EmergencyRecordingUseCase {
    val emergencyRecordingActive: StateFlow<Boolean>
    fun contactEmergencyContacts(): Result<Unit>
    fun startEmergencyRecording(): Result<Unit>
    fun stopEmergencyRecording(): Result<Unit>
}

class EmergencyRecordingUseCaseImpl(
    private val recordingServiceHandler: RecordingServiceHandler,
    private val emergencyContactModule: EmergencyContactModule,
    private val geoRecordingModule: GeoRecordingModule,
) : EmergencyRecordingUseCase {

    private var _emergencyRecordingActive: MutableStateFlow<Boolean> =
        MutableStateFlow(false)
    override val emergencyRecordingActive: StateFlow<Boolean>
        = _emergencyRecordingActive.asStateFlow()

    override fun contactEmergencyContacts(): Result<Unit> {
        return try {
            geoRecordingModule.getCurrentLocation(
                onSuccess = {
                    emergencyContactModule.contactEmergencyContacts(it)
                },
                onError = {
                    Log.d(TAG, "No current position available")
                }
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to contact emergency contacts", e)
            Result.Error(org.wdsl.witness.util.ResultError.UnknownError("Failed to contact emergency contacts: ${e.message}"))
        }
    }

    override fun startEmergencyRecording(): Result<Unit> {
        return try {
            recordingServiceHandler.startEmergencyRecordingService()
            _emergencyRecordingActive.value = true
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start emergency recording", e)
            Result.Error(org.wdsl.witness.util.ResultError.UnknownError("Failed to start emergency recording: ${e.message}"))
        }
    }

    override fun stopEmergencyRecording(): Result<Unit> {
        return try {
            //recordingServiceHandler.stopEmergencyRecordingService()
            _emergencyRecordingActive.value = false
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop emergency recording", e)
            Result.Error(org.wdsl.witness.util.ResultError.UnknownError("Failed to stop emergency recording: ${e.message}"))
        }
    }

    companion object {
        private const val TAG = "EmergencyRecordingUseCase"
    }
}
