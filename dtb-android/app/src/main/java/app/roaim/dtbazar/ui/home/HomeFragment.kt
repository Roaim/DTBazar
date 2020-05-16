package app.roaim.dtbazar.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.databinding.DataBindingComponent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.roaim.dtbazar.R
import app.roaim.dtbazar.api.ApiUtils
import app.roaim.dtbazar.databinding.FragmentHomeBinding
import app.roaim.dtbazar.di.Injectable
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.model.Store
import app.roaim.dtbazar.ui.StoreListAdapter
import app.roaim.dtbazar.utils.*
import javax.inject.Inject


class HomeFragment : Fragment(), Injectable, Loggable, HomeButtonClickListener {

    @Inject
    lateinit var apiUtils: ApiUtils

    @Inject
    lateinit var glidePlaceHolder: RoundedBitmapDrawable

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val homeViewModel: HomeViewModel by viewModels { viewModelFactory }

    private var binding by autoCleared<FragmentHomeBinding>()
    private var bindingComponent by autoCleared<DataBindingComponent>()
    private var storeAdapter by autoCleared<StoreListAdapter>()
    private var donationAdapter by autoCleared<HomeDonationAdapter>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        bindingComponent = FragmentDataBindingComponent(this, glidePlaceHolder)
        binding = FragmentHomeBinding.inflate(inflater, bindingComponent)
        handleBackButtonEvent()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listener = this
        storeAdapter = StoreListAdapter(bindingComponent).apply {
            itemClickListener = storeItemClickListener
        }
        donationAdapter = HomeDonationAdapter()
        binding.rvStore.adapter = storeAdapter
        binding.rvDonation.adapter = donationAdapter
        homeViewModel.profile.observe(viewLifecycleOwner, Observer {
            log(it.toString())
            if (it.status == Status.LOGOUT) apiUtils.logout()
            binding.profile = it
        })

        homeViewModel.myCachedStores.observe(viewLifecycleOwner, Observer {
            storeAdapter.submitList(it)
        })

        homeViewModel.myCachedDonations.observe(viewLifecycleOwner, Observer {
            donationAdapter.reload(it)
        })
    }

    override fun onAddNewStoreClick() {
        showAddStoreDialog { name, address, mobile ->
            homeViewModel.saveStore(name, address, mobile)
        }
    }

    override fun onMakeDonationClick() {

    }

    private fun handleBackButtonEvent() {
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

    private val storeItemClickListener = { store: Store?, itemView: View, isLongClick: Boolean ->
        if (isLongClick && store != null) {
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
    }

}
