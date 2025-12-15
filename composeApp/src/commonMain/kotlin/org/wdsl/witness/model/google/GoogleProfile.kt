package org.wdsl.witness.model.google

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Data class representing a Google user profile.
 *
 * @param lastUpdated The timestamp of the last update to the profile.
 * @param id The unique identifier for the Google user.
 * @param name The full name of the Google user.
 * @param given_name The given name (first name) of the Google user.
 * @param family_name The family name (last name) of the Google user.
 * @param picture The URL of the user's profile picture.
 * @param email The email address of the Google user.
 * @param verified_email A boolean indicating if the email is verified.
 */
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
