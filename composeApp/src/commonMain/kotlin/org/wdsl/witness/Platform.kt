package org.wdsl.witness

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/**
 * Interface representing the current Platform
 *
 * @property name The name of the Platform.
 * @property version The version of the Platform.
 */
interface Platform {
    /**
     * The name of the Platform.
     */
    val name: String

    /**
     * The version of the Platform.
     */
    val version: Int

    /**
     * Check if the system is in Dark Theme
     * @return true if system is in Dark Theme else false
     */
    @Composable
    fun isSystemInDarkTheme(): Boolean

    /**
     * Get the dynamic color scheme based on the system theme
     * @param darkTheme true if system is in Dark Theme else false
     * @return ColorScheme? The dynamic color scheme or null if not supported
     */
    @Composable
    fun getDynamicColor(darkTheme: Boolean): ColorScheme?

    /**
     * Check if the system is in Portrait mode
     * @return true if system is in Portrait mode else false
     */
    @Composable
    fun isPortrait(): Boolean

    /**
     * Check if the system is in Landscape mode
     * @return true if system is in Landscape mode else false
     */
    @Composable
    fun isLandscape(): Boolean
}

/**
 * The current platform implementation
 */
expect val platform: Platform
