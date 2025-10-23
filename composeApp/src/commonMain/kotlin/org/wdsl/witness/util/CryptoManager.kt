package org.wdsl.witness.util

import kotlinx.io.Buffer
import kotlinx.io.Source

interface CryptoManager {

    fun encrypt(bytes: ByteArray, outputStream: Buffer): ByteArray

    fun decrypt(inputStream: Source): ByteArray
}
