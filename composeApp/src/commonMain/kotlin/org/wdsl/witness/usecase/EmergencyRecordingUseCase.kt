package org.wdsl.witness.usecase

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.wdsl.witness.PlatformContext
import org.wdsl.witness.module.EmergencyContactModule
import org.wdsl.witness.module.GeoRecordingModule
import org.wdsl.witness.module.RecordingServiceHandler
import org.wdsl.witness.module.VibrationModule
import org.wdsl.witness.repository.EmergencyContactsRepository
import org.wdsl.witness.repository.SettingsRepository
import org.wdsl.witness.util.ERROR_NOTIFICATION_CHANNEL_ID
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.Result
import org.wdsl.witness.util.ResultError
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Use case interface for managing emergency recording functionality.
 */
interface EmergencyRecordingUseCase {
    /**
     * StateFlow indicating whether emergency recording is active.
     */
    val emergencyRecordingActive: StateFlow<Boolean>
    /**
     * Contacts emergency contacts via SMS and/or email.
     *
     * @param smsOn Whether to contact via SMS.
     * @param emailOn Whether to contact via email.
     * @return A Result indicating success or failure.
     */
    fun contactEmergencyContacts(smsOn: Boolean, emailOn: Boolean): Result<Unit>
    /**
     * Starts emergency recording.
     *
     * @return A Result indicating success or failure.
     */
    fun startEmergencyRecording(): Result<Unit>
    /**
     * Stops emergency recording.
     *
     * @return A Result indicating success or failure.
     */
    fun stopEmergencyRecording(): Result<Unit>
}

/**
 * Implementation of EmergencyRecordingUseCase.
 *
 * @param platformContext The platform context.
 * @param settingsRepository Repository for accessing settings.
 * @param emergencyContactsRepository Repository for accessing emergency contacts.
 * @param vibrationModule Module for handling device vibration.
 * @param recordingServiceHandler Handler for managing recording services.
 * @param geoRecordingModule Module for geolocation during recording.
 * @param emergencyContactModule Module for contacting emergency contacts.
 * @param googleIntegrationUseCase Use case for Google integration functionalities.
 */
class EmergencyRecordingUseCaseImpl(
    private val platformContext: PlatformContext,
    private val settingsRepository: SettingsRepository,
    private val emergencyContactsRepository: EmergencyContactsRepository,
    private val vibrationModule: VibrationModule,
    private val recordingServiceHandler: RecordingServiceHandler,
    private val geoRecordingModule: GeoRecordingModule,
    private val emergencyContactModule: EmergencyContactModule,
    private val googleIntegrationUseCase: GoogleIntegrationUseCase,
) : EmergencyRecordingUseCase {

    private var _emergencyRecordingActive: MutableStateFlow<Boolean> =
        MutableStateFlow(false)
    override val emergencyRecordingActive: StateFlow<Boolean>
        = _emergencyRecordingActive.asStateFlow()

    private var routineContactContactsJob: Job? = null

    override fun contactEmergencyContacts(smsOn: Boolean, emailOn: Boolean): Result<Unit> {
        return try {
            Log.d(TAG, "Retrieving current location to contact emergency contacts")
            geoRecordingModule.getCurrentLocation(
                onSuccess = { locationData ->
                    Log.d(TAG, "Current location retrieved: $locationData")
                    if (smsOn) {
                        Log.d(TAG, "Contacting emergency contacts via SMS")
                        platformContext.witnessApp.appScope.launch {
                            emergencyContactsRepository.getSMSEmergencyContacts().onSuccess {
                                emergencyContactModule.contactEmergencyContacts(locationData, emptyList())
                                    .onError { error ->
                                        platformContext.sendNotification(
                                            ERROR_NOTIFICATION_CHANNEL_ID,
                                            "Error",
                                            "Failed to contact emergency contacts: ${error.message}",
                                            2,
                                        )
                                    }
                            }
                        }
                    }
                    if (emailOn) {
                        Log.d(TAG, "Contacting emergency contacts via Email")
                        platformContext.witnessApp.appScope.launch {
                            googleIntegrationUseCase.sendEmergencyEmail(
                                locationData = locationData,
                                subject = "Emergency! I need help.",
                            )
                        }
                    }
                },
                onError = {
                    platformContext.sendNotification(
                        ERROR_NOTIFICATION_CHANNEL_ID,
                        "Emergency Contact",
                        "Failed to retrieve current location, emergency contacts will still be notified.",
                        2,
                    )
                    if (smsOn) {
                        platformContext.witnessApp.appScope.launch {
                            emergencyContactsRepository.getSMSEmergencyContacts().onSuccess {
                                emergencyContactModule.contactEmergencyContacts(null, emptyList())
                                    .onError { error ->
                                        platformContext.sendNotification(
                                            ERROR_NOTIFICATION_CHANNEL_ID,
                                            "Emergency Contact",
                                            "Failed to contact emergency contacts: ${error.message}",
                                            2,
                                        )
                                    }
                            }
                        }
                    }
                    if (emailOn) {
                        platformContext.witnessApp.appScope.launch {
                            googleIntegrationUseCase.sendEmergencyEmail(
                                locationData = null,
                                subject = "Emergency! I need help.",
                            )
                        }
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
            platformContext.witnessApp.appScope.launch {
                settingsRepository.getEmergencyRecordingSettings()
                    .onError { error ->
                        Log.e(TAG, "Failed to get emergency recording setting: ${error.message}")
                        platformContext.sendNotification(
                            ERROR_NOTIFICATION_CHANNEL_ID,
                            "Error",
                            "Error when retrieving settings: ${error.message}",
                            2,
                        )
                    }
                    .onSuccess { (vibrationOn, smsOn, emailOn, routineContactContacts) ->
                        if (vibrationOn) {
                            vibrationModule.vibrate(0.5.seconds.inWholeMilliseconds)
                        }
                        if (smsOn || emailOn) {
                            if (routineContactContacts) {
                                routineContactContactsJob = platformContext.witnessApp.appScope.launch {
                                    while (isActive) {
                                        contactEmergencyContacts(smsOn, emailOn)
                                        delay(5.minutes)
                                    }
                                }
                            } else {
                                contactEmergencyContacts(smsOn, emailOn)
                            }
                        }
                        Log.d(TAG, "Emergency recording started with settings - Vibration: $vibrationOn SMS: $smsOn Email: $emailOn")
                    }
            }
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
            _emergencyRecordingActive.value = false
            routineContactContactsJob?.cancel()
            routineContactContactsJob = null
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
