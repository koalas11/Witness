package org.wdsl.witness.wearable.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object ConfirmationMessageState {
    private val _isConfirmed = MutableStateFlow(false)
    val isConfirmed = _isConfirmed.asStateFlow()

    fun setConfirmed(confirmed: Boolean) {
        _isConfirmed.value = confirmed
    }
}