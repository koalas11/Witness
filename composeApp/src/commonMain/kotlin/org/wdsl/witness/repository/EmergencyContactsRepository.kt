package org.wdsl.witness.repository

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.wdsl.witness.model.settings.EmergencyContacts
import org.wdsl.witness.util.Log
import org.wdsl.witness.util.Result
import org.wdsl.witness.util.ResultError

/**
 * Repository interface for managing emergency contacts.
 */
interface EmergencyContactsRepository {

    /**
     * Gets a flow of SMS emergency contacts.
     *
     * @return A Result containing a Flow of a list of SMS contact strings.
     */
    fun getSMSEmergencyContactsFlow(): Result<Flow<List<String>>>

    /**
     * Gets the list of SMS emergency contacts.
     *
     * @return A Result containing a list of SMS contact strings.
     */
    suspend fun getSMSEmergencyContacts(): Result<List<String>>

    /**
     * Adds an SMS emergency contact.
     *
     * @param contact The SMS contact string to add.
     * @return A Result indicating success or failure.
     */
    suspend fun addSMSEmergencyContact(contact: String): Result<Unit>

    /**
     * Removes an SMS emergency contact.
     *
     * @param contact The SMS contact string to remove.
     * @return A Result indicating success or failure.
     */
    suspend fun removeSMSEmergencyContact(contact: String): Result<Unit>

    /**
     * Gets a flow of email emergency contacts.
     *
     * @return A Result containing a Flow of a list of email contact strings.
     */
    fun getEmailEmergencyContactsFlow(): Result<Flow<List<String>>>

    /**
     * Gets the list of email emergency contacts.
     *
     * @return A Result containing a list of email contact strings.
     */
    suspend fun getEmailEmergencyContacts(): Result<List<String>>

    /**
     * Adds an email emergency contact.
     *
     * @param contact The email contact string to add.
     * @return A Result indicating success or failure.
     */
    suspend fun addEmailEmergencyContact(contact: String): Result<Unit>

    /**
     * Removes an email emergency contact.
     *
     * @param contact The email contact string to remove.
     * @return A Result indicating success or failure.
     */
    suspend fun removeEmailEmergencyContact(contact: String): Result<Unit>

    /**
     * Removes all email emergency contacts.
     *
     * @return A Result indicating success or failure.
     */
    suspend fun removeAllEmailEmergencyContacts(): Result<Unit>
}

/**
 * Implementation of EmergencyContactsRepository using DataStore.
 *
 * @param dataSource The DataStore instance for EmergencyContacts.
 */
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

    override suspend fun removeAllEmailEmergencyContacts(): Result<Unit> {
        return try {
            dataSource.updateData { currentData ->
                currentData.copy(emailContacts = emptyList())
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove all email emergency contacts", e)
            Result.Error(ResultError.UnknownError("Failed to remove all email emergency contacts: ${e.message}"))
        }
    }

    companion object {
        private const val TAG = "EmergencyContactsRepository"
    }
}
