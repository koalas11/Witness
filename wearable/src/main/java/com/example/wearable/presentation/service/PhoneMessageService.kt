package com.example.wearable.presentation.service

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class PhoneMessageService: WearableListenerService() {
    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        Log.d(
            "WearMessageService",
            "Message received from phone with path: ${messageEvent.path}"
        )
    }
}