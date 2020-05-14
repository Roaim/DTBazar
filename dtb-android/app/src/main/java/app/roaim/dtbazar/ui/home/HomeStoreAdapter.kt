package app.roaim.dtbazar.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import app.roaim.dtbazar.databinding.ViewItemStoreBinding
import app.roaim.dtbazar.model.Store
import app.roaim.dtbazar.ui.RecyclerBindingAdapter
import app.roaim.dtbazar.ui.RecyclerBindingViewHolder

class HomeStoreAdapter :
    RecyclerBindingAdapter<Store, HomeStoreViewHolder, ViewItemStoreBinding>() {
    override fun onCreateViewHolder(
        binding: ViewItemStoreBinding,
        viewType: Int
    ): HomeStoreViewHolder = HomeStoreViewHolder(binding)

    override fun onGetViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): ViewItemStoreBinding = ViewItemStoreBinding.inflate(layoutInflater, parent, false)

}

class HomeStoreViewHolder(binding: ViewItemStoreBinding) :
    RecyclerBindingViewHolder<Store, ViewItemStoreBinding>(binding) {
    override fun bind(item: Store) {
        binding.store = item
    }
}