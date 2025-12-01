package org.wdsl.witness.module

import org.wdsl.witness.model.LocationData
import org.wdsl.witness.util.Result

interface EmergencyContactModule {
    fun contactEmergencyContacts(
        locationData: LocationData?,
        numbers: List<String>,
    ): Result<Unit>
}
