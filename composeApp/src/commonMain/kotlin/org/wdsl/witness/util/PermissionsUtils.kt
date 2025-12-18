package org.wdsl.witness.util

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data class representing a permission with its details.
 *
 * @property id The unique identifier for the permission.
 * @property name The name of the permission.
 * @property description A brief description of the permission.
 * @property icon An icon representing the permission.
 */
data class Permission(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val rationate: String,
)

/**
 * Expected permission values for different platforms.
 */
expect val COARSE_LOCATION_PERMISSION: Permission
expect val FINE_LOCATION_PERMISSION: Permission
expect val AUDIO_RECORDING_PERMISSION: Permission
expect val SMS_PERMISSION: Permission
expect val READ_CONTACTS_PERMISSION: Permission
