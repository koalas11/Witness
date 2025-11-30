package org.wdsl.witness.repository

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.wdsl.witness.model.EmergencyContacts
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.Result
import org.wdsl.witness.util.ResultError

interface EmergencyContactsRepository {
    fun getSMSEmergencyContactsFlow(): Result<Flow<List<String>>>
    suspend fun getSMSEmergencyContacts(): Result<List<String>>
    suspend fun addSMSEmergencyContact(contact: String): Result<Unit>
    suspend fun removeSMSEmergencyContact(contact: String): Result<Unit>
    fun getEmailEmergencyContactsFlow(): Result<Flow<List<String>>>
    suspend fun getEmailEmergencyContacts(): Result<List<String>>
    suspend fun addEmailEmergencyContact(contact: String): Result<Unit>
    suspend fun removeEmailEmergencyContact(contact: String): Result<Unit>
}

class EmergencyContactsRepositoryImpl(
    private val dataSource: DataStore<EmergencyContacts>,
) : EmergencyContactsRepository {
    override fun getSMSEmergencyContactsFlow(): Result<Flow<List<String>>> {
        return try {
            val flow = dataSource.data.map { it.smsContacts }
            Result.Success(flow)
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(ResultError.UnknownError("An unknown error occurred"))
        }
    }

    override suspend fun getSMSEmergencyContacts(): Result<List<String>> {
        return try {
            val contacts = dataSource.data.first().smsContacts
            Result.Success(contacts)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve SMS emergency contacts", e)
            Result.Error(ResultError.UnknownError("Failed to retrieve SMS emergency contacts: ${e.message}"))
        }
    }

    override suspend fun addSMSEmergencyContact(contact: String): Result<Unit> {
        return try {
            dataSource.updateData { currentData ->
                val updatedContacts = currentData.smsContacts.toMutableList().apply {
                    if (!contains(contact)) {
                        add(contact)
                    }
                }
                currentData.copy(smsContacts = updatedContacts)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add SMS emergency contact", e)
            Result.Error(ResultError.UnknownError("Failed to add SMS emergency contact: ${e.message}"))
        }
    }

    override suspend fun removeSMSEmergencyContact(contact: String): Result<Unit> {
        return try {
            dataSource.updateData { currentData ->
                val updatedContacts = currentData.smsContacts.toMutableList().apply {
                    remove(contact)
                }
                currentData.copy(smsContacts = updatedContacts)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove SMS emergency contact", e)
            Result.Error(ResultError.UnknownError("Failed to remove SMS emergency contact: ${e.message}"))
        }
    }

    override fun getEmailEmergencyContactsFlow(): Result<Flow<List<String>>> {
        return try {
            val flow = dataSource.data.map { it.emailContacts }
            Result.Success(flow)
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(ResultError.UnknownError("An unknown error occurred"))
        }
    }

    override suspend fun getEmailEmergencyContacts(): Result<List<String>> {
        return try {
            val contacts = dataSource.data.first().emailContacts
            Result.Success(contacts)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve email emergency contacts", e)
            Result.Error(ResultError.UnknownError("Failed to retrieve email emergency contacts: ${e.message}"))
        }
    }

    override suspend fun addEmailEmergencyContact(contact: String): Result<Unit> {
        return try {
            dataSource.updateData { currentData ->
                val updatedContacts = currentData.emailContacts.toMutableList().apply {
                    if (!contains(contact)) {
                        add(contact)
                    }
                }
                currentData.copy(emailContacts = updatedContacts)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add email emergency contact", e)
            Result.Error(ResultError.UnknownError("Failed to add email emergency contact: ${e.message}"))
        }
    }

    override suspend fun removeEmailEmergencyContact(contact: String): Result<Unit> {
        return try {
            dataSource.updateData { currentData ->
                val updatedContacts = currentData.emailContacts.toMutableList().apply {
                    remove(contact)
                }
                currentData.copy(emailContacts = updatedContacts)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove email emergency contact", e)
            Result.Error(ResultError.UnknownError("Failed to remove email emergency contact: ${e.message}"))
        }
    }

    companion object {
        private const val TAG = "EmergencyContactsRepository"
    }
}
