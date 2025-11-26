package org.wdsl.witness.module

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import org.wdsl.witness.model.LocationData

class AndroidEmergencyContactModule(
    private val context: Context,
) : EmergencyContactModule {

    override fun contactEmergencyContacts(locationData: LocationData?) {
        if (!hasSendSmsPermission()) {
            return
        }

        val message = buildMessage(locationData)
        val smsManager = context.getSystemService(SmsManager::class.java)

        for (number in emptyList<String>()) {
            try {
                smsManager.sendTextMessage(number, null, message, null, null)
            } catch (e: Exception) {
                // Gestire/loggare l'errore secondo necessità
                e.printStackTrace()
            }
        }
    }

    private fun buildMessage(locationData: LocationData?): String {
        val base = StringBuilder()
        base.append("Emergency! I need help.")
        locationData?.let {
            base.append(" Position: ")
            base.append("https://maps.google.com/?q=${it.latitude},${it.longitude}")
        } ?: run {
            base.append(" Position not available.")
        }
        return base.toString()
    }

    private fun hasSendSmsPermission(): Boolean {
        val perm = Manifest.permission.SEND_SMS
        return ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
    }
}
