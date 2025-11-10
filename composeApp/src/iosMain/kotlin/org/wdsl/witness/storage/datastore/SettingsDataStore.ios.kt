package org.wdsl.witness.storage.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.okio.OkioSerializer
import org.wdsl.witness.PlatformContext

actual fun <T> getDataStore(
    platformContext: PlatformContext,
    relative: String,
    serializer: OkioSerializer<T>
): DataStore<T> {
    TODO("Not yet implemented")
}
