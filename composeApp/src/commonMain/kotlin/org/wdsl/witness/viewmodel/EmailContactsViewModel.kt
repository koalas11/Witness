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

class EmailContactsViewModel(
    private val emergencyContactsRepository: EmergencyContactsRepository,
) : BaseOperationViewModel() {

    private var _emailContactsState: MutableStateFlow<EmailContactsUiState> = MutableStateFlow(EmailContactsUiState.Loading)
    val emailContactsState: StateFlow<EmailContactsUiState> = _emailContactsState.asStateFlow()

    private var isInitialized = false

    @MainThread
    fun initialize() {
        if (isInitialized) return
        isInitialized = true

        viewModelScope.launch {
            emergencyContactsRepository.getEmailEmergencyContactsFlow().onSuccess { flow ->
                flow.collect { contacts ->
                    _emailContactsState.value = EmailContactsUiState.Success(contacts)
                }
            }.onError { error ->
                _emailContactsState.value = EmailContactsUiState.Error(error.message)
            }
        }
    }

    fun addEmailContact(contact: String) {
        startOperation()
        viewModelScope.launch {
            emergencyContactsRepository.addEmailEmergencyContact(contact)
                .onSuccess {
                    operationUiMutableState.value = OperationUiState.Success("Contact added successfully")
                }
                .onError { error ->
                    operationUiMutableState.value = OperationUiState.Error(error.message)
                }
        }
    }

    fun removeEmailContact(contact: String) {
        startOperation()
        viewModelScope.launch {
            emergencyContactsRepository.removeEmailEmergencyContact(contact)
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
                EmailContactsViewModel(
                    emergencyContactsRepository = emergencyContactsRepository,
                )
            }
        }
    }
}

sealed interface EmailContactsUiState {
    object Loading : EmailContactsUiState
    data class Success(val contacts: List<String>) : EmailContactsUiState
    data class Error(val message: String?) : EmailContactsUiState
}
