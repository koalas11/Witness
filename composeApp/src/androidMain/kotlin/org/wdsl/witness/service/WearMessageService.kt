package org.wdsl.witness.service

import android.content.Intent
import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import org.wdsl.witness.MainActivity
import org.wdsl.witness.WitnessApp
import org.wdsl.witness.module.SoundAlertModule
import org.wdsl.witness.state.EmergencyServiceState
import org.wdsl.witness.usecase.EmergencyRecordingUseCase

class WearMessageService : WearableListenerService() {

    private val emergencyRecordingUseCase: EmergencyRecordingUseCase by lazy {
        (application as WitnessApp).appContainer.emergencyRecordingUseCase
    }

    private val soundAlertModule: SoundAlertModule by lazy {
        (application as WitnessApp).appContainer.soundAlertModule
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        Log.d(
            "WearMessageService",
            "Message received with path: ${messageEvent.path}"
        )

        val senderNodeId = messageEvent.sourceNodeId
        val currentRecordingState = EmergencyServiceState.emergencyServiceState.value

        // Start the app
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)

        when (messageEvent.path) {

            "/WitnessHelpMessage" -> {
                try {
                    if (currentRecordingState !is EmergencyServiceState.State.Running) {
                        emergencyRecordingUseCase.startEmergencyRecording()
                    }

                    sendMessageToWearable(
                        senderNodeId,
                        "/WitnessHelpConfirmationMessage",
                        "start".toByteArray()
                    )
                } catch (e: Exception) {
                    Log.e("WearMessageService", "Error starting emergency recording", e)
                }
            }

            "/WitnessWhistleMessage" -> {
//                soundAlertModule.playAlertSound()

                sendMessageToWearable(
                    senderNodeId,
                    "/WitnessWhistleConfirmationMessage",
                    "start".toByteArray()
                )
            }
        }
    }
    private fun sendMessageToWearable(nodeId: String, path: String, message: ByteArray) {
        Wearable
            .getMessageClient(this)
            .sendMessage(nodeId, path, message)
                .addOnSuccessListener {
                    Log.d(
                        "WearMessageService",
                        "Confirmation message sent back to watch: $nodeId")
                }
                .addOnFailureListener { exception ->
                    Log.e(
                        "WearMessageService",
                        "Error sending confirmation message",
                        exception)
                }
    }
}