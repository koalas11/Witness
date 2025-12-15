package org.wdsl.witness.wearable.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

object VibrationUtil {

    /**
     *
     * @param context The application or component context.
     * @param effect The VibrationEffect object to play.
     */
    fun vibrate(context: Context, effect: VibrationEffect) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (vibrator.hasVibrator()) {
            vibrator.vibrate(effect)
        } else {
            Log.w("VibrationUtil", "This device does not have a vibrator.")
        }
    }

    /**
     * Vibrates the device with a simple one-shot effect.
     *
     * @param context The application or component context.
     * @param durationMillis The duration of the vibration in milliseconds.
     * @param amplitude The amplitude of the vibration. Defaults to [VibrationEffect.DEFAULT_AMPLITUDE].
     */
    fun vibrate(context: Context, durationMillis: Long, amplitude: Int = VibrationEffect.DEFAULT_AMPLITUDE) {
        val effect = VibrationEffect.createOneShot(durationMillis, amplitude)
        vibrate(context, effect)
    }

}