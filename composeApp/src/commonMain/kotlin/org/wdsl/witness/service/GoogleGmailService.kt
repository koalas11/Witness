package org.wdsl.witness.service

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.utils.io.core.toByteArray
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.Result
import org.wdsl.witness.util.ResultError
import kotlin.io.encoding.Base64

interface GoogleGmailService {
    suspend fun sendEmergencyEmails(
        recipientEmails: List<String>,
        subject: String,
        gpsLat: Double,
        gpsLon: Double,
    ) : Result<Unit>
}

class GoogleGmailServiceImpl(
    private val httpClient: HttpClient,
): GoogleGmailService {
    override suspend fun sendEmergencyEmails(recipientEmails: List<String>, subject: String, gpsLat: Double, gpsLon: Double) : Result<Unit> {
        return try {
            val bodyHtml = """
                <html>
                  <body>
                    <h3>Emergency Alert!</h3>
                    <p><b>Location:</b><br>
                       Latitude: $gpsLat<br>
                       Longitude: $gpsLon</p>
                    <p><a href="https://maps.google.com/?q=$gpsLat,$gpsLon">View on Google Maps</a></p>
                  </body>
                </html>
                """.trimIndent()

            val emailContent = buildString {
                append("To: ${recipientEmails.joinToString(",")}\r\n")
                append("Subject: $subject\r\n")
                append("Content-Type: text/html; charset=UTF-8\r\n")
                append("\r\n") // blank line separates headers from body
                append(bodyHtml)
            }
            val encodedEmail = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT).encode(emailContent.toByteArray())

            val response = httpClient.post("https://gmail.googleapis.com/gmail/v1/users/me/messages/send") {
                setBody("""{"raw":"$encodedEmail"}""")
            }

            if (!response.status.isSuccess()) {
                Log.e(TAG, "Failed to send emergency emails, status: ${response.status.value}, body: ${response.bodyAsText()}")
                return Result.Error(ResultError.UnknownError("Failed to send emergency emails"))
            }

            Log.d(TAG, "Emergency emails sent successfully to: $recipientEmails")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send emergency emails", e)
            Result.Error(ResultError.UnknownError("Failed to send emergency emails"))
        }
    }

    companion object {
        private const val TAG = "GoogleGmailService"
    }
}
