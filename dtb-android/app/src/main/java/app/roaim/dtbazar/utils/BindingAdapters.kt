package app.roaim.dtbazar.utils

import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import app.roaim.dtbazar.model.Result
import app.roaim.dtbazar.model.Status
import com.bumptech.glide.Glide


object BindingAdapters {
    @JvmStatic
    @BindingAdapter("imgUrl")
    fun loadImage(iv: ImageView, url: String?) {
        url?.let {
            Glide.with(iv.context).load(url).circleCrop().into(iv)
        }
    }

    @JvmStatic
    @BindingAdapter("clearText")
    fun clearText(et: EditText, result: Result<*>?) {
        if (result?.status == Status.SUCCESS) {
            et.setText("")
        }
    }

    @JvmStatic
    @BindingAdapter("tokenError")
    fun showTokenError(tv: TextView, error: LiveData<Result<String>>) {
        error.value?.takeIf { it.status == Status.FAILED || it.status == Status.LOGOUT }?.msg?.also {
            if (!(it == Constants.TOKEN_NOT_EXISTS || it == Constants.TOKEN_NOT_CREATED)){
                tv.append("$it\n")
            }

        }
    }

    @JvmStatic
    @BindingAdapter("showError")
    fun showTokenError(tv: TextView, error: Result<*>?) {
        error?.takeIf { it.status == Status.FAILED || it.status == Status.LOGOUT }?.msg?.also {
            tv.text = it
        }
    }
}