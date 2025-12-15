package org.wdsl.witness.util

import org.wdsl.witness.PlatformContext

/**
 * Retrieves a recording file as a ByteArray.
 *
 * @param platformContext The platform-specific context.
 * @param fileName The name of the recording file to retrieve.
 * @return A Result containing the ByteArray of the file, or an error if retrieval fails.
 */
expect fun getRecordingFile(
    platformContext: PlatformContext,
    fileName: String,
): Result<ByteArray>

/**
 * Deletes a recording file.
 *
 * @param platformContext The platform-specific context.
 * @param fileName The name of the recording file to delete.
 * @return A Result indicating success or failure of the deletion.
 */
expect fun deleteRecordingFile(
    platformContext: PlatformContext,
    fileName: String,
): Result<Unit>
