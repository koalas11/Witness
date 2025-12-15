package org.wdsl.witness.model

import kotlinx.serialization.Serializable

/**
 * Data class representing location data.
 *
 * @param latitude The latitude coordinate.
 * @param longitude The longitude coordinate.
 * @param altitude The altitude value.
 * @param timestamp The timestamp of the location data.
 */
@Serializable
data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val timestamp: Long
)
