package org.wdsl.witness.module

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import org.wdsl.witness.service.EmergencyRecordingForegroundService

class RecordingServiceHandlerImpl(
    private val context: Context,
) : RecordingServiceHandler {
    override fun startEmergencyRecordingService() {
        val intent = Intent(context, EmergencyRecordingForegroundService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    override fun stopEmergencyRecordingService() {
        val intent = Intent(context, EmergencyRecordingForegroundService::class.java)
        context.stopService(intent)
    }
}
