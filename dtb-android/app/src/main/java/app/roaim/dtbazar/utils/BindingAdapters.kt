package app.roaim.dtbazar.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("imgUrl")
    fun loadImage(iv: ImageView, url: String?) {
        url?.let {
            Glide.with(iv.context).load(url).circleCrop().into(iv)
        }
    }
}