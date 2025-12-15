package org.wdsl.witness.service

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers
import io.ktor.utils.io.charsets.Charsets
import io.ktor.utils.io.core.toByteArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.wdsl.witness.PlatformContext
import org.wdsl.witness.storage.room.Recording
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.Result
import org.wdsl.witness.util.ResultError
import org.wdsl.witness.util.getFilenameTimestamp
import org.wdsl.witness.util.getFormattedTimestamp
import org.wdsl.witness.util.getRecordingFile
import kotlin.time.ExperimentalTime

/**
 * Service interface for uploading recordings to Google Drive.
 */
interface GoogleDriveService {

    /**
     * Uploads a recording to Google Drive.
     *
     * @param platformContext The platform-specific context.
     * @param recording The recording to upload.
     * @return Result indicating success or failure of the upload.
     */
    suspend fun uploadRecordingToDrive(
        platformContext: PlatformContext,
        recording: Recording
    ): Result<Unit>
}

/**
 * Implementation of the GoogleDriveService using Ktor HTTP client.
 *
 * @param httpClient The Ktor HTTP client for making requests.
 */
class GoogleDriveServiceImpl(
    private val httpClient: HttpClient,
): GoogleDriveService {
    @OptIn(ExperimentalTime::class)
    override suspend fun uploadRecordingToDrive(
        platformContext: PlatformContext,
        recording: Recording,
    ): Result<Unit> {
        return try {
            Log.d(TAG, "uploadRecordingToDrive: Starting upload of recording to Google Drive")
            val timestamp = recording.recordingFileName.subSequence(
                "recording_".length, recording.recordingFileName.lastIndexOf(".")
            ).toString().toLong()
            val subFolderName = "Recording ${getFilenameTimestamp(timestamp)}"

            val appFolderId = createOrGetFolder(APP_FOLDER_NAME, null)

            val recordingFolderId = createOrGetFolder(subFolderName, appFolderId)

            getRecordingFile(
                platformContext = platformContext,
                fileName = recording.recordingFileName
            ).onError {
                throw Exception("Failed to get recording file: ${it.message}")
            }.onSuccess { recordingFile ->
                val metaText = buildString {
                    appendLine("timestamp: ${getFormattedTimestamp(timestamp)}")

                    if (recording.gpsPositions.isNotEmpty()) {
                        val allPoints = recording.gpsPositions.joinToString("/") { pos ->
                            "${pos.latitude},${pos.longitude}"
                        }
                        appendLine("Google Maps route:")
                        appendLine("https://www.google.com/maps/dir/$allPoints")
                    }
                }

                uploadTextFileToFolder(
                    fileName = "${subFolderName}.txt",
                    content = metaText.toByteArray(Charsets.UTF_8),
                    parentFolderId = recordingFolderId,
                )

                val mediaMime = "audio/m4a"
                uploadBinaryFileToFolder(
                    fileName = recording.recordingFileName.subSequence(0, "recording".length).toString() + ".m4a",
                    content = recordingFile,
                    mediaMime = mediaMime,
                    parentFolderId = recordingFolderId,
                )
            }

            Log.d(TAG, "uploadRecordingToDrive: Successfully uploaded recording to Google Drive")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "uploadRecordingToDrive: Failed to upload recording to Google Drive", e)
            Result.Error(ResultError.UnknownError("Failed to upload recording to Google Drive: ${e.message}"))
        }
    }

    private suspend fun createOrGetFolder(folderName: String, parentId: String?): String {
        val q = buildString {
            append("name='${folderName.replace("'", "\\'")}' and mimeType='application/vnd.google-apps.folder' and trashed=false")
            if (parentId != null) append(" and '${parentId}' in parents")
        }

        val listResponse: HttpResponse = httpClient.get("https://www.googleapis.com/drive/v3/files") {
            url {
                parameters.append("q", q)
                parameters.append("fields", "files(id,name)")
            }
            headers {
                append(HttpHeaders.Accept, "application/json")
            }
        }

        if (listResponse.status != HttpStatusCode.OK) {
            val err = listResponse.bodyAsText()
            throw Exception("Drive files.list failed: $err")
        }

        val listBody = listResponse.bodyAsText()
        val parsed = Json.parseToJsonElement(listBody).jsonObject
        val files = parsed["files"]?.jsonArray
        if (files != null && files.isNotEmpty()) {
            val id = files[0].jsonObject["id"]!!.jsonPrimitive.content
            return id
        }

        val metadataJson = buildJsonObject {
            put("name", folderName)
            put("mimeType", "application/vnd.google-apps.folder")
            if (parentId != null) put("parents", buildJsonArray { add(parentId) })
        }.toString()

        val createResponse: HttpResponse = httpClient.post("https://www.googleapis.com/drive/v3/files") {
            headers {
                append(HttpHeaders.Accept, "application/json")
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }
            setBody(metadataJson)
        }

        if (createResponse.status != HttpStatusCode.OK && createResponse.status != HttpStatusCode.Created) {
            val err = createResponse.bodyAsText()
            throw Exception("Drive create folder failed: $err")
        }

        val createBody = createResponse.bodyAsText()
        val createdId = Json.parseToJsonElement(createBody).jsonObject["id"]!!.jsonPrimitive.content
        return createdId
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun uploadTextFileToFolder(
        fileName: String,
        content: ByteArray,
        parentFolderId: String,
    ) {
        val metadataJson = buildJsonObject {
            put("name", fileName)
            put("mimeType", "text/plain")
            put("parents", buildJsonArray { add(parentFolderId) })
        }.toString()

        val response: HttpResponse = httpClient.post("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        // Metadata part
                        append(
                            key = "metadata",
                            value = metadataJson,
                            headers = Headers.build {
                                append(
                                    HttpHeaders.ContentType,
                                    ContentType.Application.Json.toString()
                                )
                            }
                        )
                        // File part
                        append(
                            key = "file",
                            value = content,
                            headers = Headers.build {
                                append(HttpHeaders.ContentType, ContentType.Text.Plain.toString())
                            }
                        )
                    }
                )
            )
        }

        if (response.status != HttpStatusCode.OK && response.status != HttpStatusCode.Created) {
            throw Exception("Google Drive text upload failed: ${response.bodyAsText()}")
        }
    }


    @OptIn(ExperimentalTime::class)
    private suspend fun uploadBinaryFileToFolder(
        fileName: String,
        content: ByteArray,
        mediaMime: String,
        parentFolderId: String,
    ) {
        val metadataJson = buildJsonObject {
            put("name", fileName)
            put("mimeType", mediaMime)
            put("parents", buildJsonArray { add(parentFolderId) })
        }.toString()

        val response: HttpResponse = httpClient.post("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        // Metadata part
                        append(
                            key = "metadata",
                            value = metadataJson,
                            headers = Headers.build {
                                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                            }
                        )
                        // File part
                        append(
                            key = "file",
                            value = content,
                            headers = Headers.build {
                                append(HttpHeaders.ContentType, mediaMime)
                            }
                        )
                    }
                )
            )
        }

        if (response.status != HttpStatusCode.OK && response.status != HttpStatusCode.Created) {
            throw Exception("Google Drive binary upload failed: ${response.bodyAsText()}")
        }
    }


    companion object {
        private const val APP_FOLDER_NAME = "Witness App"
        private const val TAG = "GoogleDriveService"
    }
}
