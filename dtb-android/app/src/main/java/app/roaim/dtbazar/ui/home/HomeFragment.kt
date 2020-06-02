package app.roaim.dtbazar.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import app.roaim.dtbazar.di.Injectable
import app.roaim.dtbazar.model.Donation
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.model.Store
import app.roaim.dtbazar.ui.ListItemClickListener
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
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var _storeAdapter: HomeStoreAdapter? = null
    private val storeAdapter get() = _storeAdapter!!
    private var _donationAdapter: HomeDonationAdapter? = null
    private val donationAdapter get() = _donationAdapter!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingComponent = FragmentDataBindingComponent(this, glidePlaceHolder)
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home,
            container,
            false,
            bindingComponent
        )
        handleBackButtonEvent()
        homeViewModel.ipInfo.observe(viewLifecycleOwner, Observer {
            log("IP_INFO: $it")
        })
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _donationAdapter = null
        _storeAdapter = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listener = this
        _storeAdapter = HomeStoreAdapter(bindingComponent)
        _donationAdapter = HomeDonationAdapter()
        storeAdapter.setItemClickListener(ListItemClickListener(this::onStoreItemClick))
        donationAdapter.setItemClickListener(ListItemClickListener(this::onDonationItemClick))
        binding.rvStore.adapter = storeAdapter
        binding.rvDonation.adapter = donationAdapter

        homeViewModel.profile.observe(viewLifecycleOwner, Observer {
            log("PROFILE: $it")
            if (it.status == Status.LOGOUT) apiUtils.logout()
            binding.profile = it
        })

        homeViewModel.myStores.observe(viewLifecycleOwner, Observer(storeAdapter::submitList))

        homeViewModel.myDonations.observe(viewLifecycleOwner, Observer(donationAdapter::submitList))

    }

    override fun onAddNewStoreClick() {
        findNavController().navigate(R.id.action_navigation_home_to_addStoreFragment)
    }

    override fun onMakeDonationClick() {
        findNavController().navigate(R.id.action_navigation_home_to_navigation_store)
    }

    private fun onStoreItemClick(item: Store?, itemView: View, isLongClick: Boolean) {
        if (item != null) {
            if (isLongClick) deleteStore(item, itemView)
            else navigateToStoreDetails(
                itemView,
                item.id,
                item.name,
                item.uid,
                item.proprietor,
                item.mobile,
                item.allFoodPrice?.toFloat(),
                item.totalDonation?.toFloat(),
                item.spentDonation?.toFloat()
            )
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onDonationItemClick(item: Donation?, itemView: View, isLongClick: Boolean) {
        navigateToStoreDetails(
            itemView,
            item?.storeId!!,
            item.storeName,
            "",
            "",
            "",
            null,
            null,
            null
        )
    }

}
