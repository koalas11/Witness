package org.wdsl.witness

import android.content.res.Configuration
import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

/**
 * Android-specific implementation of the Platform interface.
 */
object AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"

    override val version: Int = Build.VERSION.SDK_INT

    @Composable
    override fun isSystemInDarkTheme(): Boolean {
        return androidx.compose.foundation.isSystemInDarkTheme()
    }

    @Composable
    override fun getDynamicColor(darkTheme: Boolean): ColorScheme? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val context = LocalContext.current
            return if (darkTheme)
                dynamicDarkColorScheme(context)
            else
                dynamicLightColorScheme(context)
        }
        return null
    }

    @Composable
    override fun isPortrait(): Boolean {
        val configuration = LocalConfiguration.current
        return configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    @Composable
    override fun isLandscape(): Boolean {
        val configuration = LocalConfiguration.current
        return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
}

/**
 * The current platform instance for Android.
 */
actual val platform: Platform = AndroidPlatform
