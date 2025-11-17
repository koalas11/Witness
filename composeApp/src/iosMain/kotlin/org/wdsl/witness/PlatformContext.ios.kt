package org.wdsl.witness

import platform.UIKit.UIApplication

data class IosContext(
    override val context: UIApplication = UIApplication.sharedApplication
): PlatformContext
