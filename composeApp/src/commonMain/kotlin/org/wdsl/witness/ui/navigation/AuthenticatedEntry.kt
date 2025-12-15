package org.wdsl.witness.ui.navigation

import androidx.compose.runtime.Composable
import org.wdsl.witness.LocalPlatformContext
import org.wdsl.witness.model.NotificationType
import org.wdsl.witness.ui.util.fastUIActions
import org.wdsl.witness.viewmodel.AppViewModel

/**
 * A composable that conditionally prompts for authentication before displaying content.
 *
 * @param requiredRoute The route that requires authentication.
 * @param backStack The current navigation back stack.
 * @param authenticated Whether the user is currently authenticated.
 * @param appViewModel The application view model.
 * @param content The content to display if authenticated.
 */
@Composable
fun AuthenticatedEntry(
    requiredRoute: ScreenRoute,
    backStack: List<ScreenRoute>,
    authenticated: Boolean,
    appViewModel: AppViewModel,
    content: @Composable () -> Unit
) {
    val platformContext = LocalPlatformContext.current
    val shouldPrompt = !authenticated && backStack.lastOrNull() == requiredRoute

    if (shouldPrompt) {
        fastUIActions.ShowBiometricPrompt(
            onSuccess = { appViewModel.authenticate() },
            onError = { err ->
                fastUIActions.DisplayNotification(
                    platformContext,
                    "Authentication failed: $err",
                    NotificationType.ERROR
                )
            }
        )
    } else {
        content()
    }
}
