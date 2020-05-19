package app.roaim.dtbazar.ui.home

import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import app.roaim.dtbazar.R
import app.roaim.dtbazar.databinding.ViewAddNewStoreBinding
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.model.Store
import app.roaim.dtbazar.utils.log
import app.roaim.dtbazar.utils.snackbar

fun HomeFragment.initAddStoreDialog() {
    addStoreBinding = ViewAddNewStoreBinding.inflate(LayoutInflater.from(requireContext()))
    addStoreDialog = AlertDialog.Builder(requireContext())
        .setView(addStoreBinding.root)
        .create()
    addStoreBinding.listener = object : ViewAddStoreButtonClickListener {
        override fun onAddStoreClick(mobile: String, address: String, name: String) {
            if (name.isNotEmpty() && address.isNotEmpty() && mobile.isNotEmpty()) {
                homeViewModel.saveStore(name, address, mobile)
                    .observe(viewLifecycleOwner, Observer {
                    log("SAVE_STORE: $it")
                    addStoreBinding.store = it
                        if (it.status == Status.SUCCESS) {
                            onCancelClick()
                            addStoreBinding.etName.requestFocus()
                        }
                })
            }
        }

        override fun onCancelClick() {
            addStoreDialog.dismiss()
        }
    }
}

fun navigateToStoreDetails(
    itemView: View,
    id: String,
    name: String?,
    uid: String,
    proprietor: String?,
    mobile: String?,
    allFoodPrice: Float?,
    totalDonation: Float?,
    spentDonation: Float?
) {
    val actionNavigationHomeToStoreDetailsFragment =
        HomeFragmentDirections.actionNavigationHomeToStoreDetailsFragment(
            id, name, uid, proprietor, mobile,
            allFoodPrice ?: 0f,
            totalDonation ?: 0f,
            spentDonation ?: 0f
        )
    ViewCompat.setTransitionName(itemView, id)
    val extras =
        FragmentNavigatorExtras(itemView to id)
    itemView.findNavController()
        .navigate(actionNavigationHomeToStoreDetailsFragment, extras)
}

fun HomeFragment.deleteStore(store: Store, itemView: View) {
    itemView.snackbar("Delete: ${store.name}?") {
        homeViewModel.deleteStore(store).observe(viewLifecycleOwner, Observer {
            log("DELETE_STORE: $it")
            if (it.status == Status.FAILED) Toast.makeText(
                requireContext(),
                it.msg,
                Toast.LENGTH_LONG
            ).show()
        })
    }
}

fun HomeFragment.handleBackButtonEvent() {
    val activity = requireActivity()
    var lastPressedAt = 0L
    // This callback will only be called when MyFragment is at least Started.
    val callback = activity.onBackPressedDispatcher.addCallback(this) {
        // Handle the back button event
        if (findNavController().currentDestination?.id == R.id.navigation_home) {
            if (lastPressedAt + 2000 > System.currentTimeMillis()) {
                activity.finish()
            } else {
                lastPressedAt = System.currentTimeMillis()
                Toast.makeText(
                    activity,
                    "Press again within 2 second to exit",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    callback.isEnabled = true
}