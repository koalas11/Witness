package org.wdsl.witness.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import org.wdsl.witness.AppContainerKey
import org.wdsl.witness.LocalWitnessExtras
import org.wdsl.witness.PlatformAppContainer
import org.wdsl.witness.WitnessApp

/**
 * Extension function to queries for [WitnessApp] object and returns an instance of
 * [PlatformAppContainer].
 */
fun CreationExtras.witnessAppContainer(): PlatformAppContainer = this[AppContainerKey]!!

/**
 * Returns an existing [ViewModel] or creates a new one in a composable.
 *
 * This function is a convenience wrapper around the standard `viewModel` function,
 * pre-configured to use the [LocalWitnessExtras] for ViewModel creation extras.
 *
 * @param VM The type of the ViewModel to retrieve or create.
 * @param viewModelStoreOwner The owner of the ViewModel store. Defaults to the current
 * [LocalViewModelStoreOwner].
 * @param key An optional key to identify the ViewModel.
 * @param factory An optional factory to create the ViewModel.
 * @param extras The creation extras for the ViewModel. Defaults to [LocalWitnessExtras].
 * @return An instance of the requested ViewModel.
 */
@Composable
inline fun <reified VM : ViewModel> witnessViewModel(
    viewModelStoreOwner: ViewModelStoreOwner =
        checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
    key: String? = null,
    factory: ViewModelProvider.Factory? = null,
    extras: CreationExtras = LocalWitnessExtras.current,
): VM = viewModel(VM::class, viewModelStoreOwner, key, factory, extras)
