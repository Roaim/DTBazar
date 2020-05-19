package app.roaim.dtbazar.ui.store_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import app.roaim.dtbazar.databinding.ViewItemStoreFoodBinding
import app.roaim.dtbazar.model.StoreFood
import app.roaim.dtbazar.ui.BaseListAdapter

class StoreFoodAdapter : BaseListAdapter<StoreFood, ViewItemStoreFoodBinding>() {
    override fun onCreateBinding(
        bindingComponent: DataBindingComponent?,
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewItemStoreFoodBinding = ViewItemStoreFoodBinding.inflate(
        layoutInflater, parent, false
    )

    override fun bind(binding: ViewItemStoreFoodBinding, item: StoreFood) {
        binding.storeFood = item
    }
}