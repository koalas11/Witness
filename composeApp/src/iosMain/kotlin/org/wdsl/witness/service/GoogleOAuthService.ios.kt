package org.wdsl.witness.service

import io.ktor.http.Url
import org.wdsl.witness.PlatformContext

actual fun openOAuthCustomTab(
    platformContext: PlatformContext,
    url: Url
) {
}

actual val client_id: String
    get() = TODO("Not yet implemented")
