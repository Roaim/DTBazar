package app.roaim.dtbazar.utils

import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import app.roaim.dtbazar.R
import com.bumptech.glide.Glide

class FragmentBindingAdapters(
    private val fragment: Fragment,
    private val glidePlaceholder: RoundedBitmapDrawable
) {
    @BindingAdapter("storeImgUrl")
    fun bindImage(imageView: ImageView, url: String?) {
        Glide.with(fragment).load(url)
            .placeholder(R.drawable.ic_store_black_24dp)
            .circleCrop()
            .into(imageView)
    }

    @BindingAdapter("profileImgUrl")
    fun loadProfileImage(iv: ImageView, url: String?) {
        Glide.with(fragment).load(url)
            .placeholder(glidePlaceholder)
            .circleCrop()
            .into(iv)
    }
}