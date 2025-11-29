package org.wdsl.witness.module

import org.wdsl.witness.util.Result

interface SoundAlertModule {
    fun playAlertSound(): Result<Unit>
    fun stopAlertSound(): Result<Unit>
}
