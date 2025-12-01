package org.wdsl.witness.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic

actual val FINE_LOCATION_PERMISSION: Permission = Permission(
    id = "android.permission.ACCESS_FINE_LOCATION",
    name = "Fine Location",
    description = "Allows the app to access precise location data.",
    icon = Icons.Default.LocationOn,
)

actual val AUDIO_RECORDING_PERMISSION: Permission = Permission(
    id = "android.permission.RECORD_AUDIO",
    name = "Audio Recording",
    description = "Allows the app to record audio from the device's microphone.",
    icon = Icons.Default.Mic,
)
