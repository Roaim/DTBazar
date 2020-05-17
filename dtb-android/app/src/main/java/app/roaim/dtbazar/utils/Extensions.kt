package app.roaim.dtbazar.utils

import android.util.Log
import android.view.View
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar

fun View.snackbar(msg: String, buttonText: String = "YES", block: (() -> Unit)? = null) =
    Snackbar.make(this, msg, Snackbar.LENGTH_LONG).apply {
        setAction(buttonText) {
            block?.invoke()
        }
    }.show()

fun EditText.value(): String = text.apply {
    takeIf { it.isEmpty() }?.apply {
        error = "Please enter valid $hint"
        requestFocus()
    }
}.toString()

fun Loggable.log(msg: String, throwable: Throwable? = null, e: Boolean = false) {
    val tag = this::class.simpleName
    if (e) Log.e(tag, msg, throwable)
    else Log.d(tag, msg, throwable)
}
