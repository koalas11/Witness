package org.wdsl.witness.util

import androidx.compose.ui.graphics.vector.ImageVector

data class Permission(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
)

expect val FINE_LOCATION_PERMISSION: Permission
expect val AUDIO_RECORDING_PERMISSION: Permission
