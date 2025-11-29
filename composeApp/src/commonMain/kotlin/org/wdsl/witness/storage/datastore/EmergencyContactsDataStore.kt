package org.wdsl.witness.storage.datastore

import androidx.datastore.core.okio.OkioSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import okio.BufferedSink
import okio.BufferedSource
import okio.IOException
import org.wdsl.witness.model.EmergencyContacts
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.cryptoManager

/**
 * Serializer for Settings using Protocol Buffers.
 *
 * Implementation based on [Datastore Magic in KMM](https://medium.com/@aribmomin111/unlocking-proto-datastore-magic-in-kmm-d397f40a0805)
 */
@OptIn(ExperimentalSerializationApi::class)
object EmergencyContactsSerializer : OkioSerializer<EmergencyContacts> {
    override val defaultValue: EmergencyContacts
        get() = EmergencyContacts()

    override suspend fun readFrom(source: BufferedSource): EmergencyContacts {
        return try {
            ProtoBuf.decodeFromByteArray(EmergencyContacts.serializer(), cryptoManager.decrypt(source))
        } catch (e: IOException) {
            Log.e(
                TAG,
                "Error occurred when decoding protobuf data: " + (e.message ?: "Unknown Error")
            )
            defaultValue
        }
    }

    override suspend fun writeTo(t: EmergencyContacts, sink: BufferedSink) {
        val bytes = ProtoBuf.encodeToByteArray(EmergencyContacts.serializer(), t)
        cryptoManager.encrypt(bytes, sink)
    }

    private const val TAG = "Emergency Contacts Serializer"
}

internal const val EMERGENCY_CONTACTS_DATASTORE_NAME = "emergency_contacts_datastore.preferences_pb"
