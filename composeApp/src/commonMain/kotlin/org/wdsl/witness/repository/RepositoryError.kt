package org.wdsl.witness.repository

/**
 * Sealed interface representing repository errors
 */
sealed interface RepositoryError {
    val message: String

    data class UnknownError(
        override val message: String
    ) : RepositoryError
}

/**
 * Exception class for repository errors
 */
internal class RepositoryException(
    val error: RepositoryError
) : Exception()
