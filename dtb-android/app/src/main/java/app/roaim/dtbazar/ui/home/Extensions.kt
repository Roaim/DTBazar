package app.roaim.dtbazar.ui.home

import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import app.roaim.dtbazar.R
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.model.Store
import app.roaim.dtbazar.utils.log
import app.roaim.dtbazar.utils.snackbar

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

fun Fragment.handleBackButtonEvent() {
    val activity = requireActivity()
    var lastPressedAt = 0L
    // This callback will only be called when MyFragment is at least Started.
    val callback = activity.onBackPressedDispatcher.addCallback(this) {
        // Handle the back button event
        when (findNavController().currentDestination?.id) {
            R.id.navigation_home, R.id.navigation_login -> {
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
    }
    callback.isEnabled = true
}