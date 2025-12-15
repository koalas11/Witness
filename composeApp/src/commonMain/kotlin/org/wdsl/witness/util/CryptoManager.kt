package org.wdsl.witness.util

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.okio.asKotlinxIoRawSink
import kotlinx.io.okio.asKotlinxIoRawSource
import okio.BufferedSink
import okio.BufferedSource

/**
 * Interface for managing encryption and decryption of byte arrays.
 */
interface CryptoManager {
    /**
     * Encrypts the given byte array and writes the result to the provided BufferedSink.
     *
     * @param bytes The byte array to encrypt.
     * @param sink The BufferedSink to write the encrypted data to.
     */
    fun encrypt(bytes: ByteArray, sink: BufferedSink) {
        encrypt(bytes, sink.asKotlinxIoRawSink().buffered())
    }

    /**
     * Encrypts the given byte array and writes the result to the provided Sink.
     *
     * @param bytes The byte array to encrypt.
     * @param sink The Sink to write the encrypted data to.
     */
    fun encrypt(bytes: ByteArray, sink: Sink)

    /**
     * Decrypts data from the given BufferedSource and returns the result as a byte array.
     *
     * @param source The BufferedSource to read the encrypted data from.
     * @return The decrypted byte array.
     */
    fun decrypt(source: BufferedSource): ByteArray {
        return decrypt(source.asKotlinxIoRawSource().buffered())
    }

    /**
     * Decrypts data from the given Source and returns the result as a byte array.
     *
     * @param source The Source to read the encrypted data from.
     * @return The decrypted byte array.
     */
    fun decrypt(source: Source): ByteArray
}

/**
 * Platform-specific implementation of CryptoManager.
 */
expect val cryptoManager: CryptoManager
