package org.wdsl.witness.ui.util

import org.wdsl.witness.PlatformContext

object IosFastUIActions: FastUIActions {
    override fun openAccessibilityServicesSettings(platformContext: PlatformContext) {
        TODO()
    }

    override fun openSystemAppSettings(platformContext: PlatformContext) {
        TODO()
    }
}

actual val fastUIActions: FastUIActions = IosFastUIActions
