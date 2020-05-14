package app.roaim.dtbazar.utils

import android.widget.EditText

fun EditText.value(): String? = text?.let {
    it.takeIf { it.isEmpty() }?.apply { error = "Please enter $hint" }
    it.takeIf { it.isNotEmpty() }?.toString()
}