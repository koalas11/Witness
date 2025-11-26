package org.wdsl.witness.util

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.okio.asKotlinxIoRawSink
import kotlinx.io.okio.asKotlinxIoRawSource
import okio.BufferedSink
import okio.BufferedSource

interface CryptoManager {
    fun encrypt(bytes: ByteArray, sink: BufferedSink) {
        encrypt(bytes, sink.asKotlinxIoRawSink().buffered())
    }

    fun encrypt(bytes: ByteArray, sink: Sink)

    fun decrypt(source: BufferedSource): ByteArray {
        return decrypt(source.asKotlinxIoRawSource().buffered())
    }

    fun decrypt(source: Source): ByteArray
}

expect val cryptoManager: CryptoManager
