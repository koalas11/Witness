package org.wdsl.witness.util

/**
 * Sealed interface representing repository errors
 */
interface ResultError {
    val message: String

    data class UnknownError(
        override val message: String
    ) : ResultError
}

/**
 * Exception class for repository errors
 */
internal class ResultException(
    val error: ResultError
) : Exception()