package org.wdsl.witness.service

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.buildUrl
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import kotlinx.serialization.json.Json
import org.wdsl.witness.PlatformContext
import org.wdsl.witness.model.GoogleOAuth
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.Result
import org.wdsl.witness.util.ResultError
import org.wdsl.witness.util.generateCodeChallenge

interface GoogleOAuthService {
    fun startGoogleOAuthFlow(platformContext: PlatformContext, codeVerifier: String, state: String) : Result<Unit>

    suspend fun handleGoogleOAuthResponse(codeVerifier: String, code: String) : Result<GoogleOAuth>

    suspend fun refreshGoogleOAuth(googleOAuth: GoogleOAuth): Result<GoogleOAuth>
}

class GoogleOAuthServiceImpl(
    private val httpClient: HttpClient,
) : GoogleOAuthService {
    override fun startGoogleOAuthFlow(platformContext: PlatformContext, codeVerifier: String, state: String): Result<Unit> {
        return try {
            val codeChallenge = generateCodeChallenge(codeVerifier)

            val url = buildUrl {
                protocol = URLProtocol.HTTPS
                host = "accounts.google.com"
                encodedPath = "/o/oauth2/v2/auth"
                parameters.append("client_id", client_id)
                parameters.append("redirect_uri", REDIRECT_URI)
                parameters.append("response_type", "code")
                parameters.append("scope", "email profile openid https://www.googleapis.com/auth/gmail.send https://www.googleapis.com/auth/drive.file")
                parameters.append("state", state)
                parameters.append("code_challenge", codeChallenge)
                parameters.append("code_challenge_method", "S256")
            }

            openOAuthCustomTab(platformContext, url)
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting OAuth flow", e)
            Result.Error(ResultError.UnknownError("Failed to start Google OAuth flow"))
        }
    }

    override suspend fun handleGoogleOAuthResponse(codeVerifier: String, code: String): Result<GoogleOAuth> {
        return try {
            val formParameters = Parameters.build {
                append("code", code)
                append("client_id", client_id)
                append("redirect_uri", REDIRECT_URI)
                append("grant_type", "authorization_code")
                append("code_verifier", codeVerifier)
            }

            val response: HttpResponse = httpClient.post("https://oauth2.googleapis.com/token") {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(FormDataContent(formParameters))
            }

            Log.d(TAG, "Received OAuth token response")
            val googleOAuth = Json.decodeFromString<GoogleOAuth>(response.bodyAsText())

            Result.Success(googleOAuth)
        } catch (e: Exception) {
            Log.e(TAG, "Error handling OAuth response", e)
            Result.Error(ResultError.UnknownError("Failed to handle Google OAuth response"))
        }
    }

    override suspend fun refreshGoogleOAuth(googleOAuth: GoogleOAuth): Result<GoogleOAuth> {
        return try {
            val formParameters = Parameters.build {
                append("client_id", client_id)
                append("grant_type", "refresh_token")
                append("refresh_token", googleOAuth.refreshToken!!)
            }

            val response: HttpResponse = httpClient.post("https://oauth2.googleapis.com/token") {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(FormDataContent(formParameters))
            }

            Log.d(TAG, "Received OAuth token response")
            val googleOAuthNew = Json.decodeFromString<GoogleOAuth>(response.bodyAsText())
            if (googleOAuthNew.refreshToken != null) {
                Result.Success(googleOAuthNew)
            } else {
                val updatedOAuth = googleOAuthNew.copy(refreshToken = googleOAuth.refreshToken)
                Result.Success(updatedOAuth)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling OAuth response", e)
            Result.Error(ResultError.UnknownError("Failed to handle Google OAuth response"))
        }
    }

    companion object {
        private const val TAG = "GoogleOAuthModule"
    }
}

const val REDIRECT_URI = "org.wdsl.witness:/oauth2redirect"

expect val client_id: String

expect fun openOAuthCustomTab(platformContext: PlatformContext, url: Url)
