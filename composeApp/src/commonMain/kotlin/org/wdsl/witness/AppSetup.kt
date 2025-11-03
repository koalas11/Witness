package org.wdsl.witness

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import org.wdsl.witness.model.NotificationsSetting
import org.wdsl.witness.ui.App

/**
 * Key to access the [PlatformAppContainer] from [CreationExtras]
 */
val AppContainerKey = object : CreationExtras.Key<PlatformAppContainer> {}

/**
 * CompositionLocal to provide [CreationExtras] to composables
 */
val LocalWitnessExtras = staticCompositionLocalOf<CreationExtras> {
    error("No CreationExtras provided")
}

/**
 * CompositionLocal to provide [PlatformContext] to composables
 */
val LocalPlatformContext = staticCompositionLocalOf<PlatformContext> {
    error("No PlatformContext provided")
}

val LocalNotificationsSetting = staticCompositionLocalOf<NotificationsSetting> {
    error("No NotificationSettings provided")
}

/**
 * Entry point of the application
 */
@Composable
fun AppSetup(
    modifier: Modifier = Modifier,
    appContainer: PlatformAppContainer,
) {
    val viewModelStoreOwner = LocalViewModelStoreOwner.current
    val oldCreationExtras = if (viewModelStoreOwner is HasDefaultViewModelProviderFactory) {
        viewModelStoreOwner.defaultViewModelCreationExtras
    } else {
        CreationExtras.Empty
    }

    val creationExtras = MutableCreationExtras(oldCreationExtras).apply {
        this[AppContainerKey] = appContainer
    }

    CompositionLocalProvider(LocalWitnessExtras provides creationExtras) {
        App(
            modifier = modifier,
        )
    }
}
