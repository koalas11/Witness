package org.wdsl.witness.storage.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import okio.BufferedSink
import okio.BufferedSource
import okio.FileSystem
import okio.IOException
import okio.Path
import org.wdsl.witness.PlatformContext
import org.wdsl.witness.model.Settings
import org.wdsl.witness.util.Log

/**
 * Serializer for Settings using Protocol Buffers.
 *
 * Implementation based on [Datastore Magic in KMM](https://medium.com/@aribmomin111/unlocking-proto-datastore-magic-in-kmm-d397f40a0805)
 */
@OptIn(ExperimentalSerializationApi::class)
object SettingsSerializer : OkioSerializer<Settings> {
    override val defaultValue: Settings
        get() = Settings()

    override suspend fun readFrom(source: BufferedSource): Settings {
        return try {
            ProtoBuf.decodeFromByteArray(Settings.serializer(), source.readByteArray())
        } catch (e: IOException) {
            Log.e(
                TAG,
                "Error occurred when decoding protobuf data: " + (e.message ?: "Unknown Error")
            )
            defaultValue
        }
    }

    override suspend fun writeTo(t: Settings, sink: BufferedSink) {
        val bytes = ProtoBuf.encodeToByteArray(Settings.serializer(), t)
        sink.write(bytes)
    }

    private const val TAG = "Settings Serializer"
}

internal const val SETTINGS_DATASTORE_NAME = "settings_datastore.preferences_pb"

/**
 * Gets the Settings DataStore for the current platform.
 * @param platformContext The platform-specific context.
 * @return The DataStore instance for Settings.
 */
expect fun getSettingsDataStore(platformContext: PlatformContext): DataStore<Settings>

/**
 * Creates a DataStore for Settings.
 * @param fileSystem The file system to use.
 * @param producePath A lambda that produces the file path for the DataStore.
 * @return The DataStore instance for Settings.
 */
fun createDataStore(
    fileSystem: FileSystem,
    producePath: () -> Path
): DataStore<Settings> =
    DataStoreFactory.create(
        storage = OkioStorage(
            fileSystem = fileSystem,
            producePath = producePath,
            serializer = SettingsSerializer,
        ),
    )
