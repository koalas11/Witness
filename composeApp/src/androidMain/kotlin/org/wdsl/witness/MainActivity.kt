package org.wdsl.witness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

/**
 * Main activity for the Witness application on Android.
 * This activity sets up the Compose content and provides the necessary platform context.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val appContainer = (application as WitnessApp).appContainer

        setContent {
            val context = AndroidContext(LocalContext.current)
            CompositionLocalProvider(
                LocalPlatformContext provides context
            ) {
                AppSetup(appContainer = appContainer)
            }
        }
    }
}
