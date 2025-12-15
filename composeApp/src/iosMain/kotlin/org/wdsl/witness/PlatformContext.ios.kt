package org.wdsl.witness

import platform.UIKit.UIApplication

data class IosContext(
    override val context: UIApplication = UIApplication.sharedApplication
): PlatformContext {
    override val witnessApp: WitnessApp
        get() = TODO("Not yet implemented")

    override fun sendNotification(
        channelId: String,
        title: String,
        message: String,
        priority: Int,
    ) {
        TODO("Not yet implemented")
    }

    override fun checkRequiredPermissionsForEmergencyRecording(): Boolean {
        TODO("Not yet implemented")
    }
}
