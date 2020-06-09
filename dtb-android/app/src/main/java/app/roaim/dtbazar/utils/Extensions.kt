package app.roaim.dtbazar.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import app.roaim.dtbazar.BuildConfig
import app.roaim.dtbazar.R
import app.roaim.dtbazar.model.Food
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
                "Max Quantity: ${it.toInt()} ${storeFood.food?.unit}. Input a valid quantity or ask the store to add more stock"
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

fun EditText.foodUnitPrice(food: Food): String = if (text.isEmpty() || text.toString() == ".") {
    error = "Please enter valid $hint"
    requestFocus()
    ""
} else {
    val amount = text.toString().toDouble()
    if (amount > food.endingPrice!!) {
        error = "Unit Price must not be greater than ${food.currency} ${food.endingPrice}"
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

fun View.openInMap(lat: Double?, lon: Double?) {
    val uri = "https://www.google.com/maps/search/?api=1&query=$lat,$lon".toUri()
    Intent(Intent.ACTION_VIEW, uri).let {
        context.startActivity(it)
    }
}

fun String.openInCustomTab(context: Context) {
    CustomTabsIntent.Builder()
        .setToolbarColor(context.getColorCompat(R.color.colorPrimary))
        .build()
        .launchUrl(context, toUri())
}

fun View.navigateToPlayStore() {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(
            "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
        )
        setPackage("com.android.vending")
    }
    context.startActivity(intent)
}

fun Context.getColorCompat(resId: Int): Int = ContextCompat.getColor(this, resId)
