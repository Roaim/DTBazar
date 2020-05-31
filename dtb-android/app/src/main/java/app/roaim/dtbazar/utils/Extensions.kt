package app.roaim.dtbazar.utils

import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import app.roaim.dtbazar.model.StoreFood
import com.google.android.material.snackbar.Snackbar

fun View.snackbar(msg: String, buttonText: String = "YES", block: (() -> Unit)? = null) =
    Snackbar.make(this, msg, Snackbar.LENGTH_LONG).apply {
        block?.apply {
            setAction(buttonText) {
                invoke()
            }
        }
    }.show()

fun EditText.value(): String = text.apply {
    takeIf { it.isEmpty() }?.apply {
        error = "Please enter valid $hint"
        requestFocus()
    }
}.toString()

fun EditText.donationQty(storeFood: StoreFood): String =
    if (text.isEmpty() || text.toString() == ".") {
        error = "Please enter valid $hint"
        requestFocus()
        ""
    } else {
        val qty = text.toString().toDouble()
        val availableDonation =
            storeFood.totalDonation?.minus(storeFood.spentDonation ?: 0.0) ?: 0.0
        val donatedStock =
            availableDonation.div(storeFood.unitPrice.times(storeFood.food?.subsidy ?: .8))
        storeFood.stockQty?.minus(donatedStock)?.takeIf { qty > it || qty <= 0 }?.let {
            error =
                "Available stock: ${it.formatted()} ${storeFood.food?.unit}. Input a valid quantity or ask the store to add more stock"
            requestFocus()
            ""
        } ?: text.toString()
    }

fun EditText.sellQty(storeFood: StoreFood): String = if (text.isEmpty() || text.toString() == ".") {
    error = "Please enter valid $hint"
    requestFocus()
    ""
} else {
    val qty = text.toString().toDouble()
    val availableDonation = storeFood.totalDonation?.minus(storeFood.spentDonation ?: 0.0) ?: 0.0
    val donatedStock =
        availableDonation.div(storeFood.unitPrice.times(storeFood.food?.subsidy ?: .8))
    if (donatedStock < qty || qty <= 0) {
        error =
            "Available donation ${donatedStock.formatted()} ${storeFood.food?.unit}. Quantity must be less than ${donatedStock.formatted()} and greater than 0"
        requestFocus()
        ""
    } else text.toString()
}

fun RadioGroup.getCheckValue() = findViewById<RadioButton>(checkedRadioButtonId).text

fun Double.formatted() = "%.2f".format(this)

fun Loggable.log(msg: String, throwable: Throwable? = null, e: Boolean = false) {
    val tag = this::class.simpleName
    if (e) Log.e(tag, msg, throwable)
    else Log.d(tag, msg, throwable)
}
