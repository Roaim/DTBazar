package app.roaim.dtbazar.utils

import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import app.roaim.dtbazar.R
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
    @BindingAdapter("profileImgUrl")
    fun loadProfileImage(iv: ImageView, url: String?) {
        url?.let {
            val placeholder = BitmapFactory.decodeResource(
                iv.resources,
                R.drawable.com_facebook_profile_picture_blank_square
            )
            val circularBitmapDrawable =
                RoundedBitmapDrawableFactory.create(iv.resources, placeholder)
            circularBitmapDrawable.isCircular = true
            Glide.with(iv).load(it)
                .placeholder(circularBitmapDrawable)
                .circleCrop()
                .into(iv)
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