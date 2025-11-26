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

    @Query("SELECT * from recording WHERE id = :id")
    fun getRecordingFlowById(id: Long): Flow<Recording?>

    @Query("SELECT * from recording")
    fun getRecordingsFlow(): Flow<List<Recording>>

    @Insert
    suspend fun insertRecording(trip: Recording)
}
