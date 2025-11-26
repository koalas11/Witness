package org.wdsl.witness.model

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
@Suppress("PropertyName")
data class GoogleProfile @OptIn(ExperimentalTime::class) constructor(
    val lastUpdated: Long = Clock.System.now().epochSeconds,
    val id: String,
    val name: String,
    val given_name: String,
    val family_name: String,
    val picture: String,
    val email: String,
    val verified_email: Boolean,
)
