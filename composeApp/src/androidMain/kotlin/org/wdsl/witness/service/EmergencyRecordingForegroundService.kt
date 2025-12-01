package org.wdsl.witness.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.wdsl.witness.PlatformAppContainer
import org.wdsl.witness.R.drawable.robe
import org.wdsl.witness.WitnessApp
import org.wdsl.witness.util.EMERGENCY_NOTIFICATION_CHANNEL_ID

class EmergencyRecordingForegroundService : Service(), EmergencyRecordingService {

    override var serviceJob: Job? = null
    override var defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    override val appContainer: PlatformAppContainer
        get() = witnessApp.appContainer

    override val witnessApp: WitnessApp
        get() = application as WitnessApp

    override fun onCreate() {
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground()
        startEmergencyRecording {
            stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
        serviceJob?.cancel()
        serviceJob = null
    }

    override fun onBind(intent: Intent): IBinder? = null

    @SuppressLint("InlinedApi")
    private fun startForeground() {
        val notification = NotificationCompat.Builder(this, EMERGENCY_NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Witness Service Active")
            .setContentText("Recording Audio and Position.")
            .setSmallIcon(robe)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(true)
            .build()
        ServiceCompat.startForeground(
            this,
            100,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
        )
    }
}
