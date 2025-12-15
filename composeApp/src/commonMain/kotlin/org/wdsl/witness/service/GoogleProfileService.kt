package org.wdsl.witness.service

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import org.wdsl.witness.model.google.GoogleOAuth
import org.wdsl.witness.model.google.GoogleProfile
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.Result
import org.wdsl.witness.util.ResultError
import kotlin.time.ExperimentalTime

/**
 * Service interface for fetching Google profile information.
 */
interface GoogleProfileService {
    /**
     * Fetches the Google profile information using the provided OAuth credentials.
     *
     * @param googleOAuth The Google OAuth credentials.
     * @return A [Result] containing the [GoogleProfile] on success or a [ResultError] on failure.
     */
    suspend fun getProfileInfo(googleOAuth: GoogleOAuth) : Result<GoogleProfile>
}

/**
 * Implementation of [GoogleProfileService] using Ktor HTTP client.
 *
 * @property httpClient The Ktor HTTP client used for making requests.
 */
class GoogleProfileServiceImpl(
    private val httpClient: HttpClient,
) : GoogleProfileService {
    @OptIn(ExperimentalTime::class)
    override suspend fun getProfileInfo(googleOAuth: GoogleOAuth): Result<GoogleProfile> {
        return try {
            val response: HttpResponse = httpClient.get("https://www.googleapis.com/oauth2/v1/userinfo") {
                url {
                    parameters.append("alt", "json")
                }
            }

            Log.d(TAG, "Google profile info response status: ${response.status.value}")

            if (response.status.value != 200) {
                Log.d(TAG, "Failed to fetch Google profile info, status: ${response.status.value}, body: ${response.bodyAsText()}")
                return Result.Error(ResultError.UnknownError("Failed to fetch Google profile info"))
            }

            Log.d(TAG, "Got Google profile info")
            val profile = Json.decodeFromString<GoogleProfile>(response.bodyAsText())

            Result.Success(profile)
        } catch (e: Exception) {
            Log.d(TAG, "Failed to fetch Google profile info", e)
            Result.Error(ResultError.UnknownError("Failed to fetch Google profile info"))
        }
    }

    companion object {
        private const val TAG = "GoogleProfileService"
    }
}
