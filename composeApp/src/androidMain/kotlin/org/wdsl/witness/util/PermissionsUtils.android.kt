package org.wdsl.witness.util

import android.Manifest
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Sms

/**
 * Coarse Location permission definition for Android platform.
 */
actual val COARSE_LOCATION_PERMISSION: Permission = Permission(
    id = Manifest.permission.ACCESS_COARSE_LOCATION,
    name = "Location",
    description = "Allows the app to access precise/coarse location data.",
    icon = Icons.Default.LocationOn,
    rationate = "Location access is required to record your geographical position during audio recordings.",
)

/**
 * Fine Location permission definition for Android platform.
 */
actual val FINE_LOCATION_PERMISSION: Permission = Permission(
    id = Manifest.permission.ACCESS_FINE_LOCATION,
    name = "Location",
    description = "Allows the app to access precise location data.",
    icon = Icons.Default.LocationOn,
    rationate = "Location access is required to record your geographical position during audio recordings.",
)

/**
 * Audio Recording permission definition for Android platform.
 */
actual val AUDIO_RECORDING_PERMISSION: Permission = Permission(
    id = Manifest.permission.RECORD_AUDIO,
    name = "Audio Recording",
    description = "Allows the app to record audio from the device's microphone.",
    icon = Icons.Default.Mic,
    rationate = "Audio recording permission is required to capture audio during your recordings.",
)

/**
 * SMS permission definition for Android platform.
 */
actual val SMS_PERMISSION: Permission = Permission(
    id = Manifest.permission.SEND_SMS,
    name = "SMS",
    description = "Allows the app to send SMS messages.",
    icon = Icons.Default.Sms,
    rationate = "SMS permission is required to send emergency messages with your location.",
)

/**
 * Read Contacts permission definition for Android platform.
 */
actual val READ_CONTACTS_PERMISSION: Permission = Permission(
    id = Manifest.permission.READ_CONTACTS,
    name = "Read Contacts",
    description = "Allows the app to read contacts from the device.",
    icon = Icons.Default.Sms,
    rationate = "Read Contacts permission is required to select emergency contacts for SMS notifications.",
)
