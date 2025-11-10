package org.wdsl.witness.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleOAuth(
    @property:SerialName("access_token") val accessToken: String,
    @property:SerialName("expires_in") val expiresIn: Int,
    @property:SerialName("refresh_token") val refreshToken: String? = null,
    val scope: String,
    @property:SerialName("token_type") val tokenType: String,
    @property:SerialName("id_token") val idToken: String,
)
