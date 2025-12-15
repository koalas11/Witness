package org.wdsl.witness.model.settings

import kotlinx.serialization.Serializable

/**
 * Data class representing emergency contact information.
 *
 * @smsContacts A list of SMS contact numbers.
 * @emailContacts A list of email contact addresses.
 */
@Serializable
data class EmergencyContacts(
    val smsContacts: List<String> = emptyList(),
    val emailContacts: List<String> = emptyList(),
)
