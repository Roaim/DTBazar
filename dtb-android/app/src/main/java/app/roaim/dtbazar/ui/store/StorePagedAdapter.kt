package app.roaim.dtbazar.ui.store

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import app.roaim.dtbazar.R
import app.roaim.dtbazar.databinding.ViewItemStoreBinding
import app.roaim.dtbazar.model.Store
import app.roaim.dtbazar.ui.BasePagedListAdapter

class StorePagedAdapter(bindingComponent: DataBindingComponent) :
    BasePagedListAdapter<Store, ViewItemStoreBinding>(bindingComponent) {

    override fun onCreateBinding(
        bindingComponent: DataBindingComponent?,
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewItemStoreBinding = DataBindingUtil.inflate(
        layoutInflater,
        R.layout.view_item_store,
        parent,
        false,
        bindingComponent
    )

    override fun bind(binding: ViewItemStoreBinding, item: Store?) {
        binding.store = item
    }
}