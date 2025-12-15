package org.wdsl.witness.model.google

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class representing Google OAuth response.
 *
 * @param accessToken The access token.
 * @param expiresIn The expiration time of the access token in seconds.
 * @param refreshToken The refresh token (optional).
 * @param scope The scope of the access token.
 * @param tokenType The type of the token.
 * @param idToken The ID token.
 */
@Serializable
data class GoogleOAuth(
    @property:SerialName("access_token") val accessToken: String,
    @property:SerialName("expires_in") val expiresIn: Int,
    @property:SerialName("refresh_token") val refreshToken: String? = null,
    @property:SerialName("refresh_token_expires_in") val refreshTokenExpiresIn: Int? = null,
    val scope: String,
    @property:SerialName("token_type") val tokenType: String,
    @property:SerialName("id_token") val idToken: String,
)
