package org.wdsl.witness.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import org.wdsl.witness.broadcastreceiver.EmergencyAlertReceiver.Companion.TAG
import org.wdsl.witness.state.AppSettingsState

class AppSettingsChangedBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d(TAG, "Received intent with action: $action")
        if (action !in listOf(Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED)) {
            return
        }
        AppSettingsState.notifySettingsChanged()
    }
}
