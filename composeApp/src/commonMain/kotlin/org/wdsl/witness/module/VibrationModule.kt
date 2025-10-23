package org.wdsl.witness.module

interface VibrationModule {
    fun supportVibration(): Boolean

    fun vibrate(durationMs: Long)

    fun vibratePattern(pattern: LongArray)
}
