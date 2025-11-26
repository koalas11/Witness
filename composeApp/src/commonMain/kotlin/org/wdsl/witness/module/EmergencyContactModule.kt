package org.wdsl.witness.module

import org.wdsl.witness.model.LocationData

interface EmergencyContactModule {
    fun contactEmergencyContacts(locationData: LocationData?)
}
