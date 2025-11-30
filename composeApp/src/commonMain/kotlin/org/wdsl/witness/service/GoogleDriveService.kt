package org.wdsl.witness.service

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
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
import org.wdsl.witness.util.getRecordingFile
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

interface GoogleDriveService {
    suspend fun uploadRecordingToDrive(
        platformContext: PlatformContext,
        recording: Recording
    ): Result<Unit>
}

class GoogleDriveServiceImpl(
    private val httpClient: HttpClient,
): GoogleDriveService {
    @OptIn(ExperimentalTime::class)
    override suspend fun uploadRecordingToDrive(
        platformContext: PlatformContext,
        recording: Recording,
    ): Result<Unit> {
        return try {
            val subFolderName = "${recording.recordingFileName}_${Clock.System.now().epochSeconds}"

            val appFolderId = createOrGetFolder(APP_FOLDER_NAME, null)

            val recordingFolderId = createOrGetFolder(subFolderName, appFolderId)

            getRecordingFile(
                platformContext = platformContext,
                fileName = recording.recordingFileName
            ).onError {
                throw Exception("Failed to get recording file: ${it.message}")
            }.onSuccess { recordingFile ->
                val metaText = buildString {
                    appendLine("name: ${recording.recordingFileName}")
                    appendLine("gpsPositions: ${recording.gpsPositions}")
                    appendLine("timestamp: ${Clock.System.now().epochSeconds}")
                }

                uploadTextFileToFolder(
                    fileName = "${recording.recordingFileName}.txt",
                    content = metaText.toByteArray(Charsets.UTF_8),
                    parentFolderId = recordingFolderId,
                )

                val mediaMime = "audio/m4a"
                uploadBinaryFileToFolder(
                    fileName = recording.recordingFileName,
                    content = recordingFile,
                    mediaMime = mediaMime,
                    parentFolderId = recordingFolderId,
                )
            }

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

        val boundary = "----wdsl-boundary-${Clock.System.now().epochSeconds}"
        val crlf = "\r\n"
        val dashBoundary = "--$boundary"

        val metaPart = StringBuilder()
            .append(dashBoundary).append(crlf)
            .append("Content-Type: application/json; charset=UTF-8").append(crlf)
            .append(crlf)
            .append(metadataJson).append(crlf)
            .toString()
            .toByteArray(Charsets.UTF_8)

        val fileHeader = StringBuilder()
            .append(dashBoundary).append(crlf)
            .append("Content-Type: text/plain; charset=UTF-8").append(crlf)
            .append("Content-Disposition: attachment; filename=\"").append(fileName).append("\"").append(crlf)
            .append(crlf)
            .toString()
            .toByteArray(Charsets.UTF_8)

        val closing = ("$crlf--$boundary--$crlf").toByteArray(Charsets.UTF_8)

        val bodyBytes: ByteArray = metaPart + fileHeader + content + closing

        val response: HttpResponse = httpClient.post("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart") {
            headers {
                append(HttpHeaders.Accept, "application/json")
                append(HttpHeaders.ContentType, "multipart/related; boundary=$boundary")
            }
            setBody(bodyBytes)
        }

        if (response.status != HttpStatusCode.OK) {
            val errorBody = response.bodyAsText()
            throw Exception("Google Drive text upload failed with status ${response.status}: $errorBody")
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

        val boundary = "----wdsl-boundary-${Clock.System.now().epochSeconds}"
        val crlf = "\r\n"
        val dashBoundary = "--$boundary"

        val metaPart = StringBuilder()
            .append(dashBoundary).append(crlf)
            .append("Content-Type: application/json; charset=UTF-8").append(crlf)
            .append(crlf)
            .append(metadataJson).append(crlf)
            .toString()
            .toByteArray(Charsets.UTF_8)

        val fileHeader = StringBuilder()
            .append(dashBoundary).append(crlf)
            .append("Content-Type: ").append(mediaMime).append(crlf)
            .append("Content-Disposition: attachment; filename=\"").append(fileName).append("\"").append(crlf)
            .append(crlf)
            .toString()
            .toByteArray(Charsets.UTF_8)

        val closing = ("$crlf--$boundary--$crlf").toByteArray(Charsets.UTF_8)

        val bodyBytes: ByteArray = metaPart + fileHeader + content + closing

        val response: HttpResponse = httpClient.post("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart") {
            headers {
                append(HttpHeaders.Accept, "application/json")
                append(HttpHeaders.ContentType, "multipart/related; boundary=$boundary")
            }
            setBody(bodyBytes)
        }

        if (response.status != HttpStatusCode.OK) {
            val errorBody = response.bodyAsText()
            throw Exception("Google Drive upload failed with status ${response.status}: $errorBody")
        }
    }

    companion object {
        private const val APP_FOLDER_NAME = "Witness App"
        private const val TAG = "GoogleDriveService"
    }
}
