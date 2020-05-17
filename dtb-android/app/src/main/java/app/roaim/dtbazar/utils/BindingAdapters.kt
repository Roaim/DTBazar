package app.roaim.dtbazar.utils

import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import app.roaim.dtbazar.model.IpInfo
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
    @BindingAdapter("ipInfoError")
    fun showError(tv: TextView, ipInfoError: LiveData<Result<IpInfo>>) {
        ipInfoError.value?.takeIf { it.status == Status.FAILED }?.msg?.also {
            tv.append("$it\n")
        }
    }

    @JvmStatic
    @BindingAdapter("tokenError")
    fun showIpInfoTokenError(tv: TextView, tokenError: LiveData<Result<String>>) {
        tokenError.value?.takeIf { it.status == Status.FAILED }?.msg?.also {
            if (!(it == Constants.TOKEN_NOT_EXISTS || it == Constants.TOKEN_NOT_CREATED)){
                tv.append("$it\n")
            }

        }
    }
}