package org.wdsl.witness.service

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import org.wdsl.witness.WitnessApp
import org.wdsl.witness.state.EmergencyServiceState
import org.wdsl.witness.usecase.EmergencyRecordingUseCase

class WearMessageService : WearableListenerService() {

    private val emergencyRecordingUseCase: EmergencyRecordingUseCase by lazy {
        (application as WitnessApp).appContainer.emergencyRecordingUseCase
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        Log.d(
            "WearMessageService",
            "Message received with path: ${messageEvent.path}"
        )

        val senderNodeId = messageEvent.sourceNodeId
        val currentRecordingState = EmergencyServiceState.emergencyServiceState.value

        if(messageEvent.path == "/WitnessHelpMessage") {

            if (currentRecordingState !is EmergencyServiceState.State.Running) {
                emergencyRecordingUseCase.startEmergencyRecording()
            }

            sendMessageToWearable(
                senderNodeId,
                "/WitnessConfirmationMessage",
                "start".toByteArray()
            )
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