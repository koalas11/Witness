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
     * Retrieves a flow of a specific recording by its ID.
     * @param recordingId The ID of the recording to retrieve.
     * @return A Result containing a Flow of the Recording object or an error.
     */
    fun getRecordingFlowById(recordingId: Long): Result<Flow<Recording?>>
    /**
     * Retrieves a flow of recordings.
     * @return A Result containing a Flow of Recording objects or an error.
     */
    fun getRecordingsFlow(): Result<Flow<List<Recording>>>

    suspend fun getAllRecordingFileNames(): Result<List<String>>

    /**
     * Inserts a new recording into the repository.
     * @param recording The Recording object to insert.
     * @return A Result indicating success or failure.
     */
    suspend fun insertRecording(recording: Recording): Result<Unit>

    suspend fun deleteRecording(recording: Recording): Result<Unit>

    suspend fun clearAllRecordings(): Result<Unit>
}

/**
 * Implementation of the RecordingsRepository using Room database.
 * @param recordingsDao The DAO for accessing recordings in the database.
 */
class RecordingsRepositoryImpl(
    private val recordingsDao: RecordingsDao,
): RecordingsRepository {

    override fun getRecordingFlowById(recordingId: Long): Result<Flow<Recording?>> {
        return try {
            Result.Success(recordingsDao.getRecordingFlowById(recordingId))
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(ResultError.UnknownError("An unknown error occurred"))
        }
    }

    override fun getRecordingsFlow(): Result<Flow<List<Recording>>> {
        return try {
            Result.Success(recordingsDao.getRecordingsFlow())
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(ResultError.UnknownError("An unknown error occurred"))
        }
    }

    override suspend fun getAllRecordingFileNames(): Result<List<String>> {
        return try {
            val recordings = recordingsDao.getAllRecordingFileNames()
            Result.Success(recordings)
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

    override suspend fun deleteRecording(recording: Recording): Result<Unit> {
        return try {
            recordingsDao.deleteRecording(recording)
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(ResultError.UnknownError("An unknown error occurred"))
        }
    }

    override suspend fun clearAllRecordings(): Result<Unit> {
        return try {
            recordingsDao.clearAllRecordings()
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("Recordings Repository", "An unknown error occurred", e)
            Result.Error(ResultError.UnknownError("An unknown error occurred"))
        }
    }

    companion object {
        private const val TAG = "Recordings Repository"
    }
}
