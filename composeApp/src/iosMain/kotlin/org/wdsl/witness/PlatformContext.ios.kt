package org.wdsl.witness

import platform.UIKit.UIApplication

class IosContext: PlatformContext {
    override val context: UIApplication = UIApplication.sharedApplication
}
