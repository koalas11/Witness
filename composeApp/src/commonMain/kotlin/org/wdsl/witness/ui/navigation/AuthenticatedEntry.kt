package org.wdsl.witness.ui.navigation

import androidx.compose.runtime.Composable
import org.wdsl.witness.LocalPlatformContext
import org.wdsl.witness.model.NotificationType
import org.wdsl.witness.ui.util.fastUIActions
import org.wdsl.witness.viewmodel.AppViewModel

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
