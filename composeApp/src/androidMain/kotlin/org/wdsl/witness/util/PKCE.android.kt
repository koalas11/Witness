package org.wdsl.witness.util

import android.util.Base64
import java.security.MessageDigest

actual fun generateCodeChallenge(verifier: String): String {
    val bytes = verifier.toByteArray(Charsets.US_ASCII)
    val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
    return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
}
