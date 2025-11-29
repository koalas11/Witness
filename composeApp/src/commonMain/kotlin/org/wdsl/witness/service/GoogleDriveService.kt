package org.wdsl.witness.service

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers
import org.wdsl.witness.PlatformContext
import org.wdsl.witness.storage.room.Recording
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.Result
import org.wdsl.witness.util.ResultError
import org.wdsl.witness.util.getRecordingFile

interface GoogleDriveService {
    suspend fun uploadRecordingToDrive(
        platformContext: PlatformContext,
        recording: Recording
    ): Result<Unit>
}

class GoogleDriveServiceImpl(
    private val httpClient: HttpClient,
): GoogleDriveService {
    override suspend fun uploadRecordingToDrive(
        platformContext: PlatformContext,
        recording: Recording
    ): Result<Unit> {
        return try {
            getRecordingFile(
                platformContext = platformContext,
                fileName = recording.recordingFileName
            ).onError {
                throw Exception("Failed to get recording file: ${it.message}")
            }.onSuccess { recordingFile ->
                val metadataJson = """
                    {
                      "name": "${recording.recordingFileName}",
                      "gpsPositions": ${recording.gpsPositions},
                    }
                """.trimIndent()

                val response: HttpResponse = httpClient.post("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart") {
                    headers {
                        append(HttpHeaders.Accept, "application/json")
                    }

                    setBody(MultiPartFormDataContent(formData {
                        // metadata part
                        append("metadata", metadataJson, Headers.build {
                            append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                        })


                        append("file", recordingFile, Headers.build {
                            append(HttpHeaders.ContentDisposition, "form-data; name=\"file\"; filename=\"${recording.recordingFileName}\"")
                            append(HttpHeaders.ContentType, ContentType.Application.OctetStream.toString())
                        })
                    }))
                }

                if (response.status != HttpStatusCode.OK) {
                    val errorBody = response.bodyAsText()
                    throw Exception("Google Drive upload failed with status ${response.status}: $errorBody")
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "uploadRecordingToDrive: Failed to upload recording to Google Drive", e)
            Result.Error(ResultError.UnknownError("Failed to upload recording to Google Drive: ${e.message}"))
        }
    }

    companion object {
        private const val TAG = "GoogleDriveService"
    }
}
