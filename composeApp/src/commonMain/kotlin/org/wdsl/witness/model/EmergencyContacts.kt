package org.wdsl.witness.model

import kotlinx.serialization.Serializable

@Serializable
data class EmergencyContacts(
    val smsContacts: List<String> = emptyList(),
    val emailContacts: List<String> = emptyList(),
)
