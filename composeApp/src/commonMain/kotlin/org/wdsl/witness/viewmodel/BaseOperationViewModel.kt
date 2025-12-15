package org.wdsl.witness.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.wdsl.witness.util.Result
import org.wdsl.witness.util.ResultError

/**
 * A base ViewModel class that provides common functionality for handling operations and their
 * states.
 */
abstract class BaseOperationViewModel(): ViewModel() {
    protected var operationUiMutableState: MutableStateFlow<OperationUiState> =
        MutableStateFlow(OperationUiState.Idle)
    val operationUiState: StateFlow<OperationUiState> = operationUiMutableState.asStateFlow()

    /**
     * Handles the result of an operation by updating the [operationUiState] with the appropriate
     * state based on the result.
     *
     * @param result The result of the operation
     * @param successMsg The message to set the operation state if the operation was successful
     */
    protected suspend fun handleResult(result: Result<Unit>, successMsg: SuccessNotifications) {
        result.onSuccess {
            operationUiMutableState.value = OperationUiState.Success(successMsg.message)
        }.onError {
            operationUiMutableState.value = OperationUiState.Error(it.message)
        }
    }

    /**
     * Handles the result of an operation by calling the [onSuccess] lambda if the result is a
     * [Result.Success] and the [onError] lambda if the result is a [Result.Error].
     *
     * @param result The result of the operation
     * @param onSuccess The lambda to call if the result is a [Result.Success]
     * @param onError The lambda to call if the result is a [Result.Error]
     */
    protected suspend fun <T> handleResult(
        result: Result<T>,
        onSuccess: suspend (T) -> Unit,
        onError: suspend (ResultError) -> Unit = {
            operationUiMutableState.value = OperationUiState.Error(it.message)
        },
    ) {
        result.onSuccess {
            onSuccess(it)
        }.onError {
            onError(it)
        }
    }

    /**
     * Starts an operation by setting the [operationUiState] to [OperationUiState.Loading].
     *
     * @throws IllegalStateException if another operation is in progress as this method should only
     * be called when no operation is in progress.
     */
    protected fun startOperation() {
        require(operationUiMutableState.value != OperationUiState.Loading) {
            "Another operation is in progress"
        }
        operationUiMutableState.value = OperationUiState.Loading
    }

    /**
     * Resets the state of the operation by setting the [operationUiState] to [OperationUiState.Idle].
     */
    fun resetOperationState() {
        operationUiMutableState.value = OperationUiState.Idle
    }
}

/**
 * A sealed interface that represents the state of an operation.
 */
sealed interface OperationUiState {
    object Idle : OperationUiState
    object Loading : OperationUiState
    data class Success(val message: String?) : OperationUiState
    data class Error(val message: String) : OperationUiState
}

/**
 * A sealed interface that represents the success notifications for operations.
 */
sealed interface SuccessNotifications {
    val message: String
}

/**
 * A sealed interface that represents the error notifications for operations.
 */
sealed interface ErrorNotifications {
    val message: String
}
