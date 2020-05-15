package app.roaim.dtbazar.ui.home

import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import app.roaim.dtbazar.databinding.ViewAddNewStoreBinding
import app.roaim.dtbazar.model.Result
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.model.Store
import app.roaim.dtbazar.utils.log

fun HomeFragment.showAddStoreDialog(
    listener: (name: String, address: String, mobile: String) -> LiveData<Result<Store>>
) {
    val addStoreBinding = ViewAddNewStoreBinding.inflate(LayoutInflater.from(requireContext()))
    val dialog = AlertDialog.Builder(requireContext())
        .setView(addStoreBinding.root)
        .create()
    addStoreBinding.listener = object : ViewAddStoreButtonClickListener {
        override fun onAddStoreClick(mobile: String, address: String, name: String) {
            if (name.isNotEmpty() && address.isNotEmpty() && mobile.isNotEmpty()) {
                listener(name, address, mobile).observe(viewLifecycleOwner, Observer {
                    log("SAVE_STORE: $it")
                    addStoreBinding.store = it
                    if (it.status == Status.SUCCESS) onCancelClick()
                })
            }
        }

        override fun onCancelClick() {
            dialog.dismiss()
        }
    }
    dialog.show()
}