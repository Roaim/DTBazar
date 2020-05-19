package app.roaim.dtbazar.ui.food

import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import app.roaim.dtbazar.databinding.ViewAddNewFoodBinding
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.utils.log

fun FoodFragment.initAddFoodDialog() {
    addFoodBinding = ViewAddNewFoodBinding.inflate(LayoutInflater.from(requireContext()))
    addFoodDialog = AlertDialog.Builder(requireContext())
        .setView(addFoodBinding.root)
        .create()
    addFoodBinding.listener = object : ViewAddFoodClickListener {
        override fun onAddFoodClick(
            ePrice: String,
            currency: CharSequence,
            unit: String,
            sPrice: String,
            name: String
        ) {
            if (name.isNotEmpty() && sPrice.isNotEmpty() && ePrice.isNotEmpty()) {
                foodViewModel.saveFood(name, currency.toString(), unit, sPrice.toDouble(), ePrice.toDouble())
                    .observe(viewLifecycleOwner, Observer {
                        log("SAVE_FOOD: $it")
                        addFoodBinding.food = it
                        if (it.status == Status.SUCCESS) {
                            onCancelClick()
                            addFoodBinding.etName.requestFocus()
                        }
                    })
            }
        }

        override fun onCancelClick() {
            addFoodDialog.dismiss()
        }
    }
}