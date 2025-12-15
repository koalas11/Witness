package org.wdsl.witness.storage.datastore

import androidx.datastore.core.okio.OkioSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import okio.BufferedSink
import okio.BufferedSource
import okio.IOException
import org.wdsl.witness.model.google.GoogleOAuth
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.cryptoManager

/**
 * Serializer for Settings using Protocol Buffers.
 *
 * Implementation based on [Datastore Magic in KMM](https://medium.com/@aribmomin111/unlocking-proto-datastore-magic-in-kmm-d397f40a0805)
 */
@OptIn(ExperimentalSerializationApi::class)
object GoogleOAuthSerializer : OkioSerializer<GoogleOAuth?> {
    override val defaultValue: GoogleOAuth? = null

    override suspend fun readFrom(source: BufferedSource): GoogleOAuth? {
        return try {
            val decrypted = cryptoManager.decrypt(source)
            if (decrypted.isEmpty()) return null
            ProtoBuf.decodeFromByteArray(GoogleOAuth.serializer(), decrypted)
        } catch (e: IOException) {
            Log.e(
                TAG,
                "Error occurred when decoding protobuf data: " + (e.message ?: "Unknown Error")
            )
            null
        }
    }

    override suspend fun writeTo(t: GoogleOAuth?, sink: BufferedSink) {
        val bytes = if (t != null) {
            ProtoBuf.encodeToByteArray(GoogleOAuth.serializer(), t)
        } else {
            ByteArray(0)
        }
        cryptoManager.encrypt(bytes, sink)
    }

    private const val TAG = "Google OAuth Serializer"
}

internal const val GOOGLE_OAUTH_DATASTORE_NAME = "google_oauth_datastore.preferences_pb"
