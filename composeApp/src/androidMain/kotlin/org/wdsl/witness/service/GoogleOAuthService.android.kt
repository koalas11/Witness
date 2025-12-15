package org.wdsl.witness.service

import android.content.Context
import androidx.browser.auth.AuthTabIntent
import androidx.core.net.toUri
import io.ktor.http.Url
import org.wdsl.witness.MainActivity
import org.wdsl.witness.PlatformContext

/**
 * Client ID for Google OAuth in Android platform.
 */
actual val client_id = "694883763108-ur8nl323iee8854gfdsj8jehejvg3lhr.apps.googleusercontent.com"

/**
 * Opens a custom tab for OAuth authentication using the provided URL.
 *
 * @param platformContext The platform context containing the Android context.
 * @param url The URL to open in the custom tab.
 */
actual fun openOAuthCustomTab(platformContext: PlatformContext, url: Url) {
    val intent = AuthTabIntent.Builder().build()
    intent.launch(((platformContext.context as Context) as MainActivity).mLauncher, url.toString().toUri(),
        REDIRECT_URI
    )
}
