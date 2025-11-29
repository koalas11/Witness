package org.wdsl.witness.repository

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.first
import org.wdsl.witness.model.EmergencyContacts
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.Result
import org.wdsl.witness.util.ResultError

interface EmergencyContactsRepository {
    suspend fun getSMSEmergencyContacts(): Result<List<String>>

    suspend fun getEmailEmergencyContacts(): Result<List<String>>
}

class EmergencyContactsRepositoryImpl(
    private val dataSource: DataStore<EmergencyContacts>,
) : EmergencyContactsRepository {
    override suspend fun getSMSEmergencyContacts(): Result<List<String>> {
        return try {
            val contacts = dataSource.data.first().smsContacts
            Result.Success(contacts)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve SMS emergency contacts", e)
            Result.Error(ResultError.UnknownError("Failed to retrieve SMS emergency contacts: ${e.message}"))
        }
    }

    override suspend fun getEmailEmergencyContacts(): Result<List<String>> {
        return try {
            val contacts = dataSource.data.first().smsContacts
            Result.Success(contacts)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve email emergency contacts", e)
            Result.Error(ResultError.UnknownError("Failed to retrieve email emergency contacts: ${e.message}"))
        }
    }

    companion object {
        private const val TAG = "EmergencyContactsRepository"
    }
}
