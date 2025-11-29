package org.wdsl.witness.util

import org.wdsl.witness.PlatformContext

expect fun getRecordingFile(
    platformContext: PlatformContext,
    fileName: String,
): Result<ByteArray>
