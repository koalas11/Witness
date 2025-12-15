package org.wdsl.witness

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.auth.AuthTabIntent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import org.wdsl.witness.state.AppSettingsState
import org.wdsl.witness.usecase.GoogleIntegrationState

/**
 * Main activity for the Witness application on Android.
 * This activity sets up the Compose content and provides the necessary platform context.
 */
class MainActivity : AppCompatActivity() {
    lateinit var mLauncher: ActivityResultLauncher<Intent>

    private fun handleAuthResult(result: AuthTabIntent.AuthResult) {
        Log.d(TAG, "handleAuthResult called with resultCode=${result.resultCode}")

        var message = when (result.resultCode) {
            AuthTabIntent.RESULT_OK -> "Received auth result."
            AuthTabIntent.RESULT_CANCELED -> "AuthTab canceled."
            AuthTabIntent.RESULT_VERIFICATION_FAILED -> "Verification failed."
            AuthTabIntent.RESULT_VERIFICATION_TIMED_OUT -> "Verification timed out."
            else -> "Unknown result code: ${result.resultCode}"
        }

        if (result.resultCode == AuthTabIntent.RESULT_OK) {
            message += " Uri: " + result.resultUri
            Log.d(TAG, message)
            val uri = result.resultUri
            processAuthUri(uri)
        }
    }

    private fun processAuthUri(uri: Uri?) {
        val appContainer = (application as WitnessApp).appContainer
        if (appContainer.googleIntegrationUseCase.googleIntegrationState.value !is GoogleIntegrationState.OAuthInProgress) {
            Log.w(
                TAG,
                "Ignoring auth response because OAuth state is not InProgress. Current state: ${appContainer.googleIntegrationUseCase.googleIntegrationState.value}"
            )
            return
        }

        appContainer.googleIntegrationUseCase.setOAuthResponseData(
            state = uri?.getQueryParameter("state") ?: "",
            code = uri?.getQueryParameter("code") ?: ""
        )
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        Log.d(TAG, "onNewIntent: intent.data=${intent.data}")
        processAuthUri(intent.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }

        val appContainer = (application as WitnessApp).appContainer

        mLauncher = AuthTabIntent.registerActivityResultLauncher(this, this::handleAuthResult)

        setContent {
            val context = AndroidContext(LocalContext.current)
            CompositionLocalProvider(
                LocalPlatformContext provides context
            ) {
                AppSetup(appContainer = appContainer)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        AppSettingsState.notifySettingsChanged()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
