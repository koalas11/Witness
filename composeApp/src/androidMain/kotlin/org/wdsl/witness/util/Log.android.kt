package org.wdsl.witness.util

import android.util.Log

/**
 * Logs a message to the Android log system.
 * @param level The log level.
 * @param tag The log tag.
 * @param msg The log message.
 * @param tr An optional throwable associated with the log message.
 */
actual fun logNative(level: Level, tag: String?, msg: String, tr: Throwable?) {
    when (level) {
        is Level.INFO -> Log.i(tag, msg, tr)
        is Level.DEBUG -> Log.d(tag, msg, tr)
        is Level.WARN -> Log.w(tag, msg, tr)
        is Level.ERROR -> Log.e(tag, msg, tr)
    }
}
