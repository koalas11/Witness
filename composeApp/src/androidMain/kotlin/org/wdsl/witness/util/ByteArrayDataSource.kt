package org.wdsl.witness.util

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.TransferListener

@UnstableApi
class ByteArrayDataSource(
    private val data: ByteArray
) : DataSource {
    private var readPosition = 0
    override fun addTransferListener(transferListener: TransferListener) {
        // No-op
    }

    override fun open(dataSpec: DataSpec): Long {
        readPosition = dataSpec.position.toInt()
        return data.size.toLong() - readPosition
    }

    override fun read(buffer: ByteArray, offset: Int, readLength: Int): Int {
        val remaining = data.size - readPosition
        if (remaining <= 0) return C.RESULT_END_OF_INPUT
        val length = minOf(remaining, readLength)
        System.arraycopy(data, readPosition, buffer, offset, length)
        readPosition += length
        return length
    }

    override fun getUri(): Uri? = null
    override fun close() {}
}

@UnstableApi
class ByteArrayDataSourceFactory(private val data: ByteArray) : DataSource.Factory {
    override fun createDataSource(): DataSource {
        return ByteArrayDataSource(data)
    }
}
