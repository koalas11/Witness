package org.wdsl.witness.storage.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import okio.FileSystem
import okio.Path
import org.wdsl.witness.PlatformContext

/**
 * Gets the Settings DataStore for the current platform.
 * @param platformContext The platform-specific context.
 * @return The DataStore instance for Settings.
 */
expect fun <T> getDataStore(
    platformContext: PlatformContext,
    relative: String,
    serializer:OkioSerializer<T>
): DataStore<T>

/**
 * Creates a DataStore for a specific serializer.
 * @param fileSystem The file system to use.
 * @param producePath A lambda that produces the file path for the DataStore.
 * @param serializer The serializer to use for the DataStore.
 * @return The DataStore instance.
 */
fun <T> createDataStore(
    fileSystem: FileSystem,
    producePath: () -> Path,
    serializer: OkioSerializer<T>,
): DataStore<T> =
    DataStoreFactory.create(
        storage = OkioStorage(
            fileSystem = fileSystem,
            producePath = producePath,
            serializer = serializer,
        ),
    )
