package app.roaim.dtbazar.utils

import android.util.Log

interface Loggable

fun Loggable.log(msg: String, throwable: Throwable? = null, e: Boolean = false) {
    val tag = this::class.simpleName
    if (e) Log.e(tag, msg, throwable)
    else Log.d(tag, msg, throwable)
}