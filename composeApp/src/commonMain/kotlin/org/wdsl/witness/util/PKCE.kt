package org.wdsl.witness.util

/**
 * Generates a code challenge from the given verifier string.
 *
 * @param verifier The code verifier string.
 * @return The generated code challenge.
 */
expect fun generateCodeChallenge(verifier: String): String
