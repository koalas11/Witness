package org.wdsl.witness.module

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import org.wdsl.witness.model.LocationData
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.Result
import org.wdsl.witness.util.ResultError

/**
 * Android implementation of the EmergencyContactModule.
 *
 * @param context The Android context.
 */
class AndroidEmergencyContactModule(
    private val context: Context,
) : EmergencyContactModule {

    override fun contactEmergencyContacts(
        locationData: LocationData?,
        numbers: List<String>,
    ): Result<Unit> {
        return try {
            Log.d(TAG, "contactEmergencyContacts: Sending SMS to emergency contacts")
            if (!hasSendSmsPermission()) {
                return Result.Error(ResultError.UnknownError("SEND_SMS permission denied"))
            }

            val message = buildMessage(locationData)
            val smsManager = context.getSystemService(SmsManager::class.java)

            for (number in numbers) {
                try {
                    smsManager.sendTextMessage(number, null, message, null, null)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to send SMS to $number", e)
                }
            }
            Log.d(TAG, "contactEmergencyContacts: SMS sent to emergency contacts")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "contactEmergencyContacts: Unknown error", e)
            Result.Error(ResultError.UnknownError("contactEmergencyContacts: ${e.message}"))
        }
    }

    private fun buildMessage(locationData: LocationData?): String {
        val base = StringBuilder()
        base.appendLine("Emergency! I need help.")
        locationData?.let {
            base.append("Position: ")
            base.appendLine("https://maps.google.com/?q=${it.latitude},${it.longitude}")
            base.appendLine("Latitude: ${it.latitude},")
            base.append(" Longitude: ${it.longitude},")
            base.append(" Altitude: ${it.altitude}m.")
        } ?: run {
            base.append("Position not available.")
        }
        return base.toString()
    }

    private fun hasSendSmsPermission(): Boolean {
        val perm = Manifest.permission.SEND_SMS
        return ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val TAG = "AndroidEmergencyContactModule"
    }
}
