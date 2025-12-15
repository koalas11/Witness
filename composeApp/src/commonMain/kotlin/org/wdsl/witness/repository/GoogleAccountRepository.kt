package org.wdsl.witness.repository

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.wdsl.witness.model.google.GoogleOAuth
import org.wdsl.witness.model.google.GoogleProfile
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.Result
import org.wdsl.witness.util.ResultError

/**
 * Repository interface for managing Google account data.
 */
interface GoogleAccountRepository {
    /**
     * Retrieves a flow of GoogleProfile data.
     *
     * @return A Result containing a Flow of GoogleProfile or an error.
     */
    suspend fun getProfileFlow(): Result<Flow<GoogleProfile?>>

    /**
     * Updates the GoogleProfile data.
     *
     * @param profile The GoogleProfile to be updated.
     * @return A Result indicating success or failure of the update operation.
     */
    suspend fun updateProfile(profile: GoogleProfile): Result<Unit>

    /**
     * Retrieves the GoogleOAuth data.
     *
     * @return A Result containing GoogleOAuth or an error.
     */
    suspend fun getGoogleOAuth(): Result<GoogleOAuth?>

    /**
     * Updates the GoogleOAuth data.
     *
     * @param googleOAuth The GoogleOAuth to be updated.
     * @return A Result indicating success or failure of the update operation.
     */
    suspend fun updateGoogleOAuth(googleOAuth: GoogleOAuth): Result<Unit>

    /**
     * Clears all Google account related data.
     *
     * @return A Result indicating success or failure of the clear operation.
     */
    suspend fun clearAllData(): Result<Unit>
}

/**
 * Implementation of GoogleAccountRepository using DataStore for persistence.
 *
 * @param googleOAuthDataStore DataStore for GoogleOAuth data.
 * @param googleProfileDataStore DataStore for GoogleProfile data.
 */
class GoogleAccountRepositoryImpl(
    private val googleOAuthDataStore: DataStore<GoogleOAuth?>,
    private val googleProfileDataStore: DataStore<GoogleProfile?>,
) : GoogleAccountRepository {
    override suspend fun getProfileFlow(): Result<Flow<GoogleProfile?>> {
        return try {
            Result.Success(googleProfileDataStore.data)
        } catch (e: Exception) {
            Log.d(TAG, "Error accessing Google Profile data store", e)
            Result.Error(ResultError.UnknownError("An unknown error occurred while accessing Google Profile data store"))
        }
    }

    override suspend fun updateProfile(profile: GoogleProfile): Result<Unit> {
        return try {
            googleProfileDataStore.updateData {
                profile
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.d(TAG, "Error updating Google Profile data", e)
            Result.Error(ResultError.UnknownError("An unknown error occurred while clearing Google Profile data"))
        }
    }

    override suspend fun getGoogleOAuth(): Result<GoogleOAuth?> {
        return try {
            Result.Success(googleOAuthDataStore.data.first())
        } catch (e: Exception) {
            Log.d(TAG, "Error retrieving Google OAuth data", e)
            Result.Error(ResultError.UnknownError("An unknown error occurred while retrieving Google OAuth data"))
        }
    }

    override suspend fun updateGoogleOAuth(googleOAuth: GoogleOAuth): Result<Unit> {
        return try {
            googleOAuthDataStore.updateData {
                googleOAuth
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.d(TAG, "Error updating Google OAuth data", e)
            Result.Error(ResultError.UnknownError("An unknown error occurred while saving Google OAuth data"))
        }
    }

    override suspend fun clearAllData(): Result<Unit> {
        return try {
            googleProfileDataStore.updateData {
                null
            }
            googleOAuthDataStore.updateData {
                null
            }
            Log.d(TAG, "Google account data cleared successfully.")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred while clearing Google account data", e)
            Result.Error(ResultError.UnknownError("An unknown error occurred while clearing Google account data"))
        }
    }

    companion object {
        private const val TAG = "Google Account Repository"
    }
}
