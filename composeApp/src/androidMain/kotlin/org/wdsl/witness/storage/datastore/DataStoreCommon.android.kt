package org.wdsl.witness.storage.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.okio.OkioSerializer
import okio.FileSystem
import okio.Path.Companion.toPath
import org.wdsl.witness.PlatformContext

/**
 * Gets the Settings DataStore for Android platform.
 * @param platformContext The platform-specific context.
 * @return The DataStore instance for Settings.
 */
actual fun <T> getDataStore(
    platformContext: PlatformContext,
    relative: String,
    serializer:OkioSerializer<T>
): DataStore<T> {
    val context = requireNotNull(platformContext.context as Context)
    val producePath = { context.filesDir.resolve(relative).absolutePath.toPath() }

    return createDataStore(fileSystem = FileSystem.SYSTEM, producePath = producePath, serializer = serializer)
}
