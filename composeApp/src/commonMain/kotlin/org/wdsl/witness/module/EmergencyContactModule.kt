package org.wdsl.witness.module

import org.wdsl.witness.model.LocationData
import org.wdsl.witness.util.Result

/**
 * Common interface for contacting emergency contacts.
 */
interface EmergencyContactModule {
    /**
     * Contacts the emergency contacts with the provided location data and phone numbers.
     *
     * @param locationData The location data to include in the message, or null if not available.
     * @param numbers The list of phone numbers to contact.
     * @return A [Result] indicating success or failure of the operation.
     */
    fun contactEmergencyContacts(
        locationData: LocationData?,
        numbers: List<String>,
    ): Result<Unit>
}
