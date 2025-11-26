package org.wdsl.witness.storage.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.runBlocking
import org.wdsl.witness.PlatformContext
import kotlin.concurrent.Volatile

/**
 * Room database for the Witness application.
 */
@Database(entities = [Recording::class], version = 1)
@TypeConverters(Converters::class)
abstract class WitnessDatabase : RoomDatabase() {
    abstract fun recordingDao(): RecordingsDao

    companion object {
        @Volatile
        private var Instance: WitnessDatabase? = null

        fun getDatabase(platformContext: PlatformContext): WitnessDatabase {
            return Instance ?: runBlocking {
                getDatabaseBuilder(platformContext)
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

internal const val WITNESS_DB_NAME = "Witness_DB"

/**
 * Gets the WitnessDatabase builder for the specific platform.
 * @param platformContext The platform-specific context.
 * @return The RoomDatabase.Builder instance for WitnessDatabase.
 */
expect fun getDatabaseBuilder(platformContext: PlatformContext): RoomDatabase.Builder<WitnessDatabase>
