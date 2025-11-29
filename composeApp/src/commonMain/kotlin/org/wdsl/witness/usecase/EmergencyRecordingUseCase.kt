package org.wdsl.witness.usecase

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.wdsl.witness.PlatformContext
import org.wdsl.witness.module.EmergencyContactModule
import org.wdsl.witness.module.GeoRecordingModule
import org.wdsl.witness.module.RecordingServiceHandler
import org.wdsl.witness.repository.EmergencyContactsRepository
import org.wdsl.witness.util.ERROR_NOTIFICATION_CHANNEL_ID
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.Result
import org.wdsl.witness.util.ResultError

interface EmergencyRecordingUseCase {
    val emergencyRecordingActive: StateFlow<Boolean>
    fun contactEmergencyContacts(): Result<Unit>
    fun startEmergencyRecording(): Result<Unit>
    fun stopEmergencyRecording(): Result<Unit>
}

class EmergencyRecordingUseCaseImpl(
    private val platformContext: PlatformContext,
    private val emergencyContactsRepository: EmergencyContactsRepository,
    private val recordingServiceHandler: RecordingServiceHandler,
    private val geoRecordingModule: GeoRecordingModule,
    private val emergencyContactModule: EmergencyContactModule,
    private val googleIntegrationUseCase: GoogleIntegrationUseCase,
) : EmergencyRecordingUseCase {

    private var _emergencyRecordingActive: MutableStateFlow<Boolean> =
        MutableStateFlow(false)
    override val emergencyRecordingActive: StateFlow<Boolean>
        = _emergencyRecordingActive.asStateFlow()

    override fun contactEmergencyContacts(): Result<Unit> {
        return try {
            geoRecordingModule.getCurrentLocation(
                onSuccess = { locationData ->
                    platformContext.witnessApp.appScope.launch {
                        emergencyContactsRepository.getSMSEmergencyContacts().onSuccess {
                            emergencyContactModule.contactEmergencyContacts(locationData, emptyList())
                                .onErrorSync { error ->
                                    platformContext.sendNotification(
                                        ERROR_NOTIFICATION_CHANNEL_ID,
                                        "Error",
                                        "Failed to contact emergency contacts: ${error.message}",
                                        2,
                                    )
                                }
                        }
                    }
                    platformContext.witnessApp.appScope.launch {
                        googleIntegrationUseCase.sendEmergencyEmail(
                            locationData = locationData,
                            subject = "Emergency! I need help.",
                        )
                    }
                },
                onError = {
                    platformContext.sendNotification(
                        ERROR_NOTIFICATION_CHANNEL_ID,
                        "Error",
                        "Failed to retrieve current location.",
                        2,
                    )
                    platformContext.witnessApp.appScope.launch {
                        emergencyContactsRepository.getSMSEmergencyContacts().onSuccess {
                            emergencyContactModule.contactEmergencyContacts(null, emptyList())
                                .onErrorSync { error ->
                                    platformContext.sendNotification(
                                        ERROR_NOTIFICATION_CHANNEL_ID,
                                        "Error",
                                        "Failed to contact emergency contacts: ${error.message}",
                                        2,
                                    )
                                }
                        }
                    }
                    platformContext.witnessApp.appScope.launch {
                        googleIntegrationUseCase.sendEmergencyEmail(
                            locationData = null,
                            subject = "Emergency! I need help.",
                        )
                    }
                    Log.d(TAG, "No current position available")
                }
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to contact emergency contacts", e)
            Result.Error(ResultError.UnknownError("Failed to contact emergency contacts: ${e.message}"))
        }
    }

    override fun startEmergencyRecording(): Result<Unit> {
        return try {
            recordingServiceHandler.startEmergencyRecordingService()
            _emergencyRecordingActive.value = true
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start emergency recording", e)
            Result.Error(ResultError.UnknownError("Failed to start emergency recording: ${e.message}"))
        }
    }

    override fun stopEmergencyRecording(): Result<Unit> {
        return try {
            //recordingServiceHandler.stopEmergencyRecordingService()
            _emergencyRecordingActive.value = false
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop emergency recording", e)
            Result.Error(ResultError.UnknownError("Failed to stop emergency recording: ${e.message}"))
        }
    }

    companion object {
        private const val TAG = "EmergencyRecordingUseCase"
    }
}
