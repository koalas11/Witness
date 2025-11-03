package org.wdsl.witness.repository

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import org.wdsl.witness.model.Settings
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.Result
import org.wdsl.witness.util.ResultError

/**
 * Repository interface for managing user settings.
 */
interface SettingsRepository {
    /**
     * Retrieves a flow of user settings.
     * @return A Result containing a Flow of Settings or an error.
     */
    fun getSettingsFlow(): Result<Flow<Settings>>

    /**
     * Updates the user settings.
     * @param settings The Settings object to update.
     * @return A Result indicating success or failure.
     */
    suspend fun updateSettings(settings: Settings): Result<Unit>

    /**
     * Modifies the user settings using a provided modification function.
     * @param modify A function that takes the current Settings and returns the modified Settings.
     * @return A Result indicating success or failure.
     */
    suspend fun updateSettings(modify: (Settings) -> Settings): Result<Unit>
}

class SettingsRepositoryImpl(
    private val settingsDataStore: DataStore<Settings>
): SettingsRepository {

    override fun getSettingsFlow(): Result<Flow<Settings>> {
        return try {
            Result.Success(settingsDataStore.data)
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(ResultError.UnknownError("An unknown error occurred"))
        }
    }

    override suspend fun updateSettings(settings: Settings): Result<Unit> {
        return try {
            settingsDataStore.updateData { settings }
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(ResultError.UnknownError("An unknown error occurred"))
        }
    }

    override suspend fun updateSettings(modify: (Settings) -> Settings): Result<Unit> {
        return try {
            settingsDataStore.updateData { currentSettings ->
                modify(currentSettings)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(ResultError.UnknownError("An unknown error occurred"))
        }
    }

    companion object {
        private const val TAG = "SettingsRepository"
    }
}
