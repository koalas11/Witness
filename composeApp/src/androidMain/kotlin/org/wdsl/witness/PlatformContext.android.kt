package org.wdsl.witness

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Android-specific implementation of the PlatformContext.
 * @param context The Android context.
 */
data class AndroidContext(override val context: Context) : PlatformContext

@Composable
actual fun getPlatformContext(): PlatformContext {
    val context = LocalContext.current
    return AndroidContext(context)
}