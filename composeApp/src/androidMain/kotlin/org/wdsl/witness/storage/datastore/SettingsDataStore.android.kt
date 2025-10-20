package org.wdsl.witness.storage.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import okio.FileSystem
import okio.Path.Companion.toPath
import org.wdsl.witness.PlatformContext
import org.wdsl.witness.model.Settings

/**
 * Gets the Settings DataStore for Android platform.
 * @param platformContext The platform-specific context.
 * @return The DataStore instance for Settings.
 */
actual fun getSettingsDataStore(platformContext: PlatformContext): DataStore<Settings> {
    val context = requireNotNull(platformContext.context as Context)
    val producePath = { context.filesDir.resolve(SETTINGS_DATASTORE_NAME).absolutePath.toPath() }

    return createDataStore(fileSystem = FileSystem.SYSTEM, producePath = producePath)
}
