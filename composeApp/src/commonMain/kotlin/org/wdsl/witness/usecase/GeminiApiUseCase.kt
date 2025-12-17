package org.wdsl.witness.usecase

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.headers
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.wdsl.witness.PlatformContext
import org.wdsl.witness.WitnessBuildConfig
import org.wdsl.witness.model.llm.LlmSummary
import org.wdsl.witness.storage.room.Recording
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.Result
import org.wdsl.witness.util.ResultError
import org.wdsl.witness.util.getRecordingFile
import kotlin.io.encoding.Base64

/**
 * Use case for interacting with the Gemini API to generate audio summaries.
 */
class GeminiApiUseCase() {

    /**
     * Generates an audio summary for the given recording using the Gemini API.
     *
     * @param platformContext The platform-specific context.
     * @param recording The recording for which to generate the summary.
     * @return A [Result] containing the [LlmSummary] on success or a [ResultError] on failure.
     */
    suspend fun getAudioSummary(
        platformContext: PlatformContext,
        recording: Recording
    ): Result<LlmSummary> = try {
        val client = HttpClient {}

        var summary: LlmSummary? = null
        getRecordingFile(
            platformContext = platformContext,
            fileName = recording.recordingFileName
        ).onError {
            throw Exception("Failed to get recording file: ${it.message}")
        }.onSuccess { recordingFile ->
            val base64Audio = Base64.encode(recordingFile)

            // Build JSON request body
            val requestJson = """
        {
          "contents": [
            {
              "parts": [
                {
                  "inline_data": {
                    "mime_type": "audio/m4a",
                    "data": "$base64Audio"
                  }
                },
                {
                  "text": ${Json.encodeToString(PROMPT)}
                }
              ]
            }
          ],
          "generation_config": {
            "response_mime_type": "application/json",
            "response_schema": {
              "type": "OBJECT",
              "properties": {
                "summary": {
                  "type": "STRING",
                  "description": ${Json.encodeToString(SUMMARY_DESC)}
                },
                "segments": {
                  "type": "ARRAY",
                  "description": ${Json.encodeToString(SEGMENTS_DESC)},
                  "items": {
                    "type": "OBJECT",
                    "properties": {
                      "speaker": { "type": "STRING" },
                      "timestamp": { "type": "STRING" },
                      "content": { "type": "STRING" },
                      "language": { "type": "STRING" },
                      "language_code": { "type": "STRING" },
                      "translation": { "type": "STRING" },
                      "emotion": {
                        "type": "STRING",
                        "enum": ["happy", "sad", "angry", "neutral"]
                      }
                    },
                    "required": ["speaker", "timestamp", "content", "language", "language_code", "emotion"]
                  }
                }
              },
              "required": ["summary", "segments"]
            }
          }
        }
        """.trimIndent()

            // Send request
            val response: HttpResponse = client.post("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${WitnessBuildConfig.GEMINI_API_KEY}") {
                headers {
                    append("x-goog-api-key", WitnessBuildConfig.GEMINI_API_KEY)
                    append(HttpHeaders.ContentType, "application/json")
                }
                setBody(requestJson)
            }

            val responseString = response.bodyAsText()
            Log.d(TAG, "getAudioSummary: Response String: $responseString")

            if (!response.status.isSuccess()) {
                throw Exception("Gemini API request failed: $summary")
            }

            val responseJson = Json.decodeFromString<JsonObject>(responseString)

            if (responseJson["candidates"] == null) {
                throw Exception("Gemini API response missing 'candidates': $responseString")
            }

            if (responseJson["candidates"]!!.jsonArray.isEmpty()) {
                throw Exception("Gemini API response has empty 'candidates': $responseString")
            }

            if (responseJson["candidates"]!!.jsonArray[0].jsonObject["content"] == null) {
                throw Exception("Gemini API response missing 'content' in first candidate: $responseString")
            }

            if (responseJson["candidates"]!!.jsonArray[0].jsonObject["content"]!!.jsonObject["parts"] == null) {
                throw Exception("Gemini API response missing 'parts' in content: $responseString")
            }

            val contentParts = responseJson["candidates"]!!
                .jsonArray[0].jsonObject["content"]!!
                .jsonObject["parts"]!!.jsonArray

            if (contentParts.isEmpty()) {
                throw Exception("Gemini API response has empty 'parts' in content: $responseString")
            }

            val contentText = contentParts[0].jsonObject["text"]?.toString()
                ?: throw Exception("Gemini API response missing 'text' in first part: $responseString")

            val json = Json { ignoreUnknownKeys = true }

            val innerJson = if (contentText.trimStart().startsWith("\"")) {
                json.decodeFromString<String>(contentText)
            } else {
                contentText
            }

            summary = json.decodeFromString(LlmSummary.serializer(), innerJson)

            Log.d(TAG, "getAudioSummary: Response: $summary")
        }

        if (summary == null) {
            throw Exception("Summary is null")
        }
        Result.Success(summary)
    } catch (e: Exception) {
        Log.e(TAG, "getAudioSummary: Failed to get audio summary", e)
        Result.Error(ResultError.UnknownError("Failed to get audio summary: ${e.message}"))
    }

    companion object {
        private const val TAG = "GeminiApiUseCase"

        /**
         * The prompt to be sent to the Gemini API for audio transcription.
         */
        private const val PROMPT = "Process the audio file and generate a detailed transcription.\n\nRequirements:\n1. Identify distinct speakers (e.g., Speaker 1, Speaker 2, or names if context allows).\n2. Provide accurate timestamps for each segment (Format: MM:SS).\n3. Detect the primary language of each segment.\n4. If the segment is in a language different than English, also provide the English translation.\n5. Identify the primary emotion of the speaker in this segment. You MUST choose exactly one of the following: Happy, Sad, Angry, Neutral.\n6. Provide a brief summary of the entire audio at the beginning."

        /**
         * Description for the summary field in the Gemini API response schema.
         */
        private const val SUMMARY_DESC = "A concise summary of the audio content."

        /**
         * Description for the segments field in the Gemini API response schema.
         */
        private const val SEGMENTS_DESC = "List of transcribed segments with speaker and timestamp."
    }
}
