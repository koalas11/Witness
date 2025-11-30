package org.wdsl.witness.service

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService

class WearMessageService : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        Log.d(
            "WearMessageService",
            "Message received with path: ${messageEvent.path}"
        )

        if(messageEvent.path == "/WitnessHelpMessage") {
            val senderNodeId = messageEvent.sourceNodeId
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