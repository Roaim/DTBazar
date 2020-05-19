package app.roaim.dtbazar.ui.store_details

import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import app.roaim.dtbazar.databinding.ViewAddNewStoreFoodBinding
import app.roaim.dtbazar.model.Food
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.utils.log

fun StoreDetailsFragment.initAddStoreFoodDialog() {
    addStoreFoodBinding = ViewAddNewStoreFoodBinding.inflate(LayoutInflater.from(requireContext()))
    addStoreFoodDialog = AlertDialog.Builder(requireContext())
        .setView(addStoreFoodBinding.root)
        .create()
    viewModel.getFoodList().observe(viewLifecycleOwner, Observer {
        log("FOOD_LIST: $it")
        addStoreFoodBinding.storeFood = it
    })
    foodSuggestionAdapter = FoodSuggestionAdapter(requireContext())
    addStoreFoodBinding.spinnerFood.adapter = foodSuggestionAdapter
    viewModel.cachedFoodList.observe(viewLifecycleOwner, Observer {
        if (it.status == Status.SUCCESS) {
            foodSuggestionAdapter.clear()
            foodSuggestionAdapter.addAll(it.data!!.toMutableList())
            foodSuggestionAdapter.notifyDataSetChanged()
        }
    })
    addStoreFoodBinding.listener = object : ViewAddStoreFoodClickListener {

        override fun onCancelClick() {
            addStoreFoodDialog.dismiss()
        }

        override fun onAddStoreFoodClick(stockQty: String, unitPrice: String) {
            if (stockQty.isNotEmpty() && unitPrice.isNotEmpty()) {
                val food = addStoreFoodBinding.spinnerFood.selectedItem as Food
                viewModel.saveStoreFood(navArgs.storeId, food.id, stockQty, unitPrice)
                    .observe(viewLifecycleOwner, Observer {
                        log("ADD_STORE_FOOD: $it")
                        addStoreFoodBinding.storeFood = it
                        if (it.status == Status.SUCCESS) {
                            onCancelClick()
                            addStoreFoodBinding.etUnitPrice.requestFocus()
                        }
                    })
            }
        }
    }
}