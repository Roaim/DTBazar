package app.roaim.dtbazar.ui.store_details

import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import app.roaim.dtbazar.databinding.ViewAddNewStoreFoodBinding
import app.roaim.dtbazar.model.Food
import app.roaim.dtbazar.model.Result
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.model.StoreFood
import app.roaim.dtbazar.utils.log

fun StoreDetailsFragment.initAddStoreFoodDialog() {
    addStoreFoodBinding = ViewAddNewStoreFoodBinding.inflate(LayoutInflater.from(requireContext()))
    addStoreFoodBinding.spinnerFood.onItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                addStoreFoodBinding.food = parent?.getItemAtPosition(0) as Food
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                addStoreFoodBinding.food = parent?.getItemAtPosition(position) as Food
            }
        }
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
                if (foodSuggestionAdapter.isEmpty) {
                    addStoreFoodBinding.storeFood =
                        Result.failed<StoreFood>("Please add a food definition from the bottom right \"Food\" tab.")
                    return
                }
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