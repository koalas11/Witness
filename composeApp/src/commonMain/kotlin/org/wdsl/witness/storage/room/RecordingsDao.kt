package org.wdsl.witness.storage.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for managing recordings in the Room database.
 */
@Dao
interface RecordingsDao {
    @Query("SELECT * from recording")
    fun getRecordingsFlow(): Flow<Recording>

    @Insert
    suspend fun insertRecording(trip: Recording)
}
