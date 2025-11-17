package org.wdsl.witness

import android.content.Context

/**
 * Android-specific implementation of the PlatformContext.
 * @param context The Android context.
 */
data class AndroidContext(override val context: Context) : PlatformContext
