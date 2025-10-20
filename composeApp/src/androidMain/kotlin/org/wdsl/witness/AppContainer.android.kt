package org.wdsl.witness

import android.content.Context

/**
 * Android-specific implementation of the AppContainer.
 * @param context The Android context.
 */
class AndroidAppContainer(
    private val context: Context,
): AppContainerImpl(AndroidContext(context)), PlatformAppContainer {

}
