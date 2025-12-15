package org.wdsl.witness.util

/**
 * A sealed class representing the result of an operation, which can be either a success or an error.
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val error: ResultError) : Result<Nothing>()

    /**
     * Returns the value of the [Success] result
     */
    suspend fun onSuccess(action: suspend (T) -> Unit): Result<T> {
        if (this is Success) {
            action(data)
        }
        return this
    }

    /**
     * Returns the value of the [Success] result or null if it's an [Error]
     */
    fun getSuccessOrNull(): T? {
        return if (this is Success) {
            data
        } else {
            null
        }
    }

    /**
     * Returns the value of the [Error] result
     */
    suspend fun onError(action: suspend (ResultError) -> Unit): Result<T> {
        if (this is Error) {
            action(error)
        }
        return this
    }
}
