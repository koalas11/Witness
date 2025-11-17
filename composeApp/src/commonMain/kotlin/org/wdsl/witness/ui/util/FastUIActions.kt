package org.wdsl.witness.ui.util

import org.wdsl.witness.PlatformContext

interface FastUIActions {
    fun openAccessibilityServicesSettings(platformContext: PlatformContext)

    fun openSystemAppSettings(platformContext: PlatformContext)
}

expect val fastUIActions: FastUIActions
