package org.wdsl.witness.repository

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.wdsl.witness.model.GoogleOAuth
import org.wdsl.witness.model.GoogleProfile
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.Result
import org.wdsl.witness.util.ResultError

interface GoogleAccountRepository {
    suspend fun getProfileFlow(): Result<Flow<GoogleProfile?>>
    suspend fun updateProfile(profile: GoogleProfile): Result<Unit>
    suspend fun getGoogleOAuth(): Result<GoogleOAuth?>
    suspend fun updateGoogleOAuth(googleOAuth: GoogleOAuth): Result<Unit>
}

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

    companion object {
        private const val TAG = "Google Account Repository"
    }
}



