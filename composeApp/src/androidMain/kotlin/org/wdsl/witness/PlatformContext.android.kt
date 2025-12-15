package org.wdsl.witness

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import org.wdsl.witness.R.drawable.robe

/**
 * Android-specific implementation of the PlatformContext.
 * @param context The Android context.
 */
data class AndroidContext(override val context: Context) : PlatformContext {
    override val witnessApp: WitnessApp
        get() = context.applicationContext as WitnessApp

    override fun sendNotification(channelId: String, title: String, message: String, priority: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(robe)
            .setPriority(priority)
            .setOngoing(true)
            .build()
        notificationManager.notify(1, notification)
    }

    override fun checkRequiredPermissionsForEmergencyRecording(): Boolean {
        val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION
        val recordAudioPermission = Manifest.permission.RECORD_AUDIO

        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            fineLocationPermission
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context,
            coarseLocationPermission
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        val hasRecordAudioPermission = ContextCompat.checkSelfPermission(
            context,
            recordAudioPermission
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        return (hasFineLocationPermission || hasCoarseLocationPermission) && hasRecordAudioPermission
    }
}
