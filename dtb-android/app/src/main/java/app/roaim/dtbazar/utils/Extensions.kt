package app.roaim.dtbazar.utils

import android.util.Log
import android.view.View
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar

fun View.snackbar(msg: String, buttonText: String = "YES", block: () -> Unit) =
    Snackbar.make(this, msg, Snackbar.LENGTH_LONG).apply {
        setAction(buttonText) {
            block()
        }
    }.show()

fun EditText.value(): String = text.apply {
    takeIf { it.isEmpty() }?.apply {
        error = "Please enter valid $hint"
        requestFocus()
    }
}.toString()

fun Loggable.log(msg: String, e: Throwable? = null) {
    val tag = this::class.simpleName
    Log.d(tag, msg, e)
}
