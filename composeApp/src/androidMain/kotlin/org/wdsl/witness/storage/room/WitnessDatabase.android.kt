package org.wdsl.witness.storage.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.wdsl.witness.PlatformContext

/**
 * Gets the WitnessDatabase builder for Android platform.
 * @param platformContext The platform-specific context.
 * @return The RoomDatabase.Builder instance for WitnessDatabase.
 */
actual fun getDatabaseBuilder(platformContext: PlatformContext): RoomDatabase.Builder<WitnessDatabase> {
    val context = requireNotNull(platformContext.context as Context)
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(WITNESS_DB_NAME)
    return Room.databaseBuilder<WitnessDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
