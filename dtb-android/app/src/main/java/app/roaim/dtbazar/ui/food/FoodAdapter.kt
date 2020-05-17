package app.roaim.dtbazar.ui.food

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import app.roaim.dtbazar.databinding.ViewItemFoodBinding
import app.roaim.dtbazar.model.Food
import app.roaim.dtbazar.ui.BaseListAdapter

class FoodAdapter : BaseListAdapter<Food, ViewItemFoodBinding>() {
    override fun onCreateBinding(
        bindingComponent: DataBindingComponent?,
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewItemFoodBinding = ViewItemFoodBinding.inflate(layoutInflater, parent, false)

    override fun bind(binding: ViewItemFoodBinding, item: Food) {
        binding.food = item
    }
}