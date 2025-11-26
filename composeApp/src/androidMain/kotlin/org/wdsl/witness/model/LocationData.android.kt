package org.wdsl.witness.model

import android.location.Location

fun Location.toLocationData() : LocationData{
    return LocationData(
        latitude = this.latitude,
        longitude = this.longitude,
        altitude = this.altitude,
        timestamp = this.time
    )
}
