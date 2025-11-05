package org.wdsl.witness

import androidx.compose.runtime.Composable
import platform.UIKit.UIApplication

data class IosContext(
    override val context: UIApplication = UIApplication.sharedApplication
): PlatformContext

@Composable
actual fun getPlatformContext(): PlatformContext {
    return IosContext()
}
