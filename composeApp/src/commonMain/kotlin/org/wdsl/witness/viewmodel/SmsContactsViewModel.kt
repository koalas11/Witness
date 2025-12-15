package org.wdsl.witness.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.wdsl.witness.repository.EmergencyContactsRepository

/**
 * ViewModel for managing SMS emergency contacts.
 *
 * @param emergencyContactsRepository Repository for accessing and modifying SMS emergency contacts.
 */
class SmsContactsViewModel(
    private val emergencyContactsRepository: EmergencyContactsRepository,
): BaseOperationViewModel() {
    private var _smsContactsState: MutableStateFlow<SmsContactsUiState> = MutableStateFlow(SmsContactsUiState.Loading)
    val smsContactsState: StateFlow<SmsContactsUiState> = _smsContactsState.asStateFlow()

    private var isInitialized = false

    @MainThread
    fun initialize() {
        if (isInitialized) return
        isInitialized = true

        viewModelScope.launch {
            emergencyContactsRepository.getSMSEmergencyContactsFlow().onSuccess { flow ->
                flow.collect { contacts ->
                    _smsContactsState.value = SmsContactsUiState.Success(contacts)
                }
            }.onError { error ->
                _smsContactsState.value = SmsContactsUiState.Error(error.message)
            }
        }
    }

    fun addSmsContact(contact: String) {
        startOperation()
        viewModelScope.launch {
            emergencyContactsRepository.addSMSEmergencyContact(contact)
                .onSuccess {
                    operationUiMutableState.value = OperationUiState.Success("Contact added successfully")
                }
                .onError { error ->
                    operationUiMutableState.value = OperationUiState.Error(error.message)
                }
        }
    }

    fun removeSmsContact(contact: String) {
        startOperation()
        viewModelScope.launch {
            emergencyContactsRepository.removeSMSEmergencyContact(contact)
                .onSuccess {
                    operationUiMutableState.value = OperationUiState.Success("Contact removed successfully")
                }
                .onError { error ->
                    operationUiMutableState.value = OperationUiState.Error(error.message)
                }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val emergencyContactsRepository = witnessAppContainer().emergencyContactsRepository
                SmsContactsViewModel(
                    emergencyContactsRepository = emergencyContactsRepository,
                )
            }
        }
    }
}

sealed interface SmsContactsUiState {
    object Loading: SmsContactsUiState
    data class Success(val contacts: List<String>): SmsContactsUiState
    data class Error(val message: String?): SmsContactsUiState
}
