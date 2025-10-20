package org.wdsl.witness

import android.app.Application

/**
 * Android-specific implementation of the WitnessApp.
 */
class WitnessAppAndroid: WitnessApp, Application() {
    override lateinit var appContainer: PlatformAppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AndroidAppContainer(this)
    }
}
