package org.wdsl.witness.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class EmergencyAlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d(TAG, "Received intent with action: $action")
        if (Intent.ACTION_BOOT_COMPLETED != action) {
            return
        }
    }

    companion object {
        const val TAG = "DeviceBootReceiver"
    }
}
