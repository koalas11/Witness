package org.wdsl.witness.service

import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.wdsl.witness.broadcastreceiver.ACTION_WITNESS_EMERGENCY
import org.wdsl.witness.broadcastreceiver.START_EMERGENCY_ALERT_URI
import org.wdsl.witness.broadcastreceiver.START_RECORDING_URI
import org.wdsl.witness.shared.WearableMessageConstants
import org.wdsl.witness.state.EmergencyServiceState

class WearMessageService : WearableListenerService() {

    private var job: Job? = null

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        Log.d(
            "WearMessageService",
            "Message received with path: ${messageEvent.path}"
        )

        val senderNodeId = messageEvent.sourceNodeId

        when (messageEvent.path) {

            WearableMessageConstants.HELP_MESSAGE_PATH -> {
                try {
                    val intent = Intent(ACTION_WITNESS_EMERGENCY)
                        .setData(START_RECORDING_URI.toUri())
                        .setPackage(packageName)
                    sendBroadcast(intent)

                    job = CoroutineScope(SupervisorJob()).launch {

                        while (isActive) {
                            if (EmergencyServiceState.emergencyServiceState.value == EmergencyServiceState.State.Running) {
                                break
                            }
                            delay(500)
                        }

                        //                    sendMessageToWearable(
//                        senderNodeId,
//                        WearableMessageConstants.HELP_CONFIRMATION_PATH,
//                        "start".toByteArray()
//                    )

                        EmergencyServiceState.emergencyServiceState.collect { state ->
                            if (state == EmergencyServiceState.State.Idle) {

                                sendMessageToWearable(
                                    senderNodeId,
                                    WearableMessageConstants.HELP_CONFIRMATION_PATH,
                                    "start".toByteArray()
                                )

                                job?.cancel()
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("WearMessageService", "Error starting emergency recording", e)
                }
            }

            WearableMessageConstants.WHISTLE_MESSAGE_PATH -> {

                try {
                    val intent = Intent(ACTION_WITNESS_EMERGENCY)
                        .setData(START_EMERGENCY_ALERT_URI.toUri())
                        .setPackage(packageName)
                    sendBroadcast(intent)

                    sendMessageToWearable(
                        senderNodeId,
                        WearableMessageConstants.WHISTLE_CONFIRMATION_PATH,
                        "start".toByteArray()
                    )

                } catch (e: Exception) {
                    Log.e("WearMessageService", "Error starting emergency recording", e)
                }
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