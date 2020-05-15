package app.roaim.dtbazar.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import app.roaim.dtbazar.databinding.ViewItemStoreBinding
import app.roaim.dtbazar.model.Store

class StorePagedAdapter : PagedListAdapter<Store, StoreViewHolder>(DiffCallback) {
    var itemClickListener: ((item: Store?, itemView: View, isLongClick: Boolean) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder =
        StoreViewHolder(ViewItemStoreBinding.inflate(LayoutInflater.from(parent.context))).apply {
            setClickListener(itemClickListener) { getItem(it) }
        }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class StoreListAdapter : ListAdapter<Store, StoreViewHolder>(DiffCallback) {
    var itemClickListener: ((item: Store?, itemView: View, isLongClick: Boolean) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder =
        StoreViewHolder(ViewItemStoreBinding.inflate(LayoutInflater.from(parent.context))).apply {
            setClickListener(itemClickListener) { getItem(it) }
        }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object DiffCallback :
    DiffUtil.ItemCallback<Store>() {
    override fun areItemsTheSame(oldItem: Store, newItem: Store): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Store, newItem: Store): Boolean = oldItem == newItem
}

class StoreViewHolder(binding: ViewItemStoreBinding) :
    RecyclerBindingViewHolder<Store, ViewItemStoreBinding>(binding) {

    override fun bind(item: Store?) {
        binding.store = item
    }
}