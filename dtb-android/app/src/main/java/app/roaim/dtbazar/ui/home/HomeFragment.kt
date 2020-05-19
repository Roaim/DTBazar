package app.roaim.dtbazar.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.roaim.dtbazar.R
import app.roaim.dtbazar.api.ApiUtils
import app.roaim.dtbazar.databinding.FragmentHomeBinding
import app.roaim.dtbazar.databinding.ViewAddNewStoreBinding
import app.roaim.dtbazar.di.Injectable
import app.roaim.dtbazar.model.Donation
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.model.Store
import app.roaim.dtbazar.utils.FragmentDataBindingComponent
import app.roaim.dtbazar.utils.Loggable
import app.roaim.dtbazar.utils.autoCleared
import app.roaim.dtbazar.utils.log
import javax.inject.Inject


class HomeFragment : Fragment(), Injectable, Loggable, HomeButtonClickListener {

    @Inject
    lateinit var apiUtils: ApiUtils

    @Inject
    lateinit var glidePlaceHolder: RoundedBitmapDrawable

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val homeViewModel: HomeViewModel by viewModels { viewModelFactory }

    private var bindingComponent by autoCleared<DataBindingComponent>()
    private var binding by autoCleared<FragmentHomeBinding>()
    var addStoreBinding by autoCleared<ViewAddNewStoreBinding>()
    var addStoreDialog by autoCleared<AlertDialog>()
    private var storeAdapter by autoCleared<HomeStoreAdapter>()
    private var donationAdapter by autoCleared<HomeDonationAdapter>()
    var storeItemClickListener by autoCleared<((Store?, View, Boolean) -> Unit)>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingComponent = FragmentDataBindingComponent(this, glidePlaceHolder)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home,
            container,
            false,
            bindingComponent
        )
        handleBackButtonEvent()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listener = this
        storeAdapter = HomeStoreAdapter(bindingComponent)
        donationAdapter = HomeDonationAdapter()
        storeAdapter.setItemClickListener { store: Store?, itemView: View, isLongClick: Boolean ->
            if (store != null) {
                if (isLongClick) deleteStore(store, itemView)
                else navigateToStoreDetails(
                    itemView,
                    store.id,
                    store.name,
                    store.uid,
                    store.proprietor,
                    store.mobile,
                    store.allFoodPrice?.toFloat(),
                    store.totalDonation?.toFloat(),
                    store.spentDonation?.toFloat()
                )
            }
        }
        donationAdapter.setItemClickListener { donation: Donation?, itemView: View, longClick: Boolean ->
            navigateToStoreDetails(
                itemView,
                donation?.storeId!!,
                donation.storeName,
                "",
                "",
                "",
                null,
                null,
                null
            )
        }
        binding.rvStore.adapter = storeAdapter
        binding.rvDonation.adapter = donationAdapter

        homeViewModel.profile.observe(viewLifecycleOwner, Observer {
            log("PROFILE: $it")
            if (it.status == Status.LOGOUT) apiUtils.logout()
            binding.profile = it
        })

        homeViewModel.myStores.observe(viewLifecycleOwner, Observer(storeAdapter::submitList))

        homeViewModel.myDonations.observe(viewLifecycleOwner, Observer(donationAdapter::submitList))

        initAddStoreDialog()
    }

    override fun onAddNewStoreClick() {
        addStoreDialog.show()
    }

    override fun onMakeDonationClick() {
        findNavController().navigate(R.id.action_navigation_home_to_navigation_store)
    }

}
