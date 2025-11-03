package org.wdsl.witness.repository

import kotlinx.coroutines.flow.Flow
import org.wdsl.witness.storage.room.Recording
import org.wdsl.witness.storage.room.RecordingsDao
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.Result
import org.wdsl.witness.util.ResultError

/**
 * Repository interface for managing recordings.
 */
interface RecordingsRepository {
    /**
     * Retrieves a flow of recordings.
     * @return A Result containing a Flow of Recording objects or an error.
     */
    fun getRecordingsFlow(): Result<Flow<Recording>>

    /**
     * Inserts a new recording into the repository.
     * @param recording The Recording object to insert.
     * @return A Result indicating success or failure.
     */
    suspend fun insertRecording(recording: Recording): Result<Unit>
}

/**
 * Implementation of the RecordingsRepository using Room database.
 * @param recordingsDao The DAO for accessing recordings in the database.
 */
class RecordingsRepositoryImpl(
    private val recordingsDao: RecordingsDao,
): RecordingsRepository {

    override fun getRecordingsFlow(): Result<Flow<Recording>> {
        return try {
            Result.Success(recordingsDao.getRecordingsFlow())
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(ResultError.UnknownError("An unknown error occurred"))
        }
    }

    override suspend fun insertRecording(recording: Recording): Result<Unit> {
        return try {
            recordingsDao.insertRecording(recording)
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(ResultError.UnknownError("An unknown error occurred"))
        }
    }

    companion object {
        private const val TAG = "Recordings Repository"
    }
}
