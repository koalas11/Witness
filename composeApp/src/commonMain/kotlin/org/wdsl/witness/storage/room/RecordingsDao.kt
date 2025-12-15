package org.wdsl.witness.storage.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.wdsl.witness.llm.LlmSummary

/**
 * Data Access Object (DAO) for managing recordings in the Room database.
 */
@Dao
interface RecordingsDao {

    @Query("SELECT * from recording WHERE id = :id")
    fun getRecordingFlowById(id: Long): Flow<Recording?>

    @Query("SELECT * from recording")
    fun getRecordingsFlow(): Flow<List<Recording>>

    @Query("SELECT recordingFileName from recording")
    suspend fun getAllRecordingFileNames(): List<String>

    @Insert
    suspend fun insertRecording(recording: Recording)

    @Delete
    suspend fun deleteRecording(recording: Recording)

    @Query("DELETE FROM recording")
    suspend fun clearAllRecordings()

    @Query("UPDATE recording SET llmSummary = :llmSummary WHERE id = :id")
    suspend fun createRecordingSummary(id: Long, llmSummary: LlmSummary)

    @Query("UPDATE recording SET llmSummary = null WHERE id = :id")
    suspend fun deleteRecordingSummary(id: Long)
}
