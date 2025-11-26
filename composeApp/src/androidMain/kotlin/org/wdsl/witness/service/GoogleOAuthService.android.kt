package org.wdsl.witness.service

import android.content.Context
import androidx.browser.auth.AuthTabIntent
import androidx.core.net.toUri
import io.ktor.http.Url
import org.wdsl.witness.MainActivity
import org.wdsl.witness.PlatformContext


actual val client_id = "694883763108-ur8nl323iee8854gfdsj8jehejvg3lhr.apps.googleusercontent.com"


actual fun openOAuthCustomTab(platformContext: PlatformContext, url: Url) {
    val intent = AuthTabIntent.Builder().build()
    intent.launch(((platformContext.context as Context) as MainActivity).mLauncher, url.toString().toUri(),
        REDIRECT_URI
    )
}
