package app.roaim.dtbazar.ui.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import app.roaim.dtbazar.R
import app.roaim.dtbazar.databinding.FragmentStoreBinding
import app.roaim.dtbazar.di.Injectable
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.model.Store
import app.roaim.dtbazar.utils.FragmentDataBindingComponent
import app.roaim.dtbazar.utils.Loggable
import app.roaim.dtbazar.utils.autoCleared
import app.roaim.dtbazar.utils.log
import javax.inject.Inject

class StoreFragment : Fragment(), Injectable, Loggable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var glidePlaceHolder: RoundedBitmapDrawable

    private val storeViewModel: StoreViewModel by viewModels { viewModelFactory }

    private var binding by autoCleared<FragmentStoreBinding>()
    private var bindingComponent by autoCleared<DataBindingComponent>()
    private var storePagedAdapter by autoCleared<StorePagedAdapter>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        bindingComponent = FragmentDataBindingComponent(this, glidePlaceHolder)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_store,
            container,
            false,
            bindingComponent
        )

        storePagedAdapter = StorePagedAdapter(bindingComponent)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.retryCallback = storeViewModel
        storePagedAdapter.setItemClickListener(storeItemClickListener)
        binding.rvStore.adapter = storePagedAdapter

        storeViewModel.ipInfo.observe(viewLifecycleOwner, Observer {
            log("IP_INFO: $it")
            if (it.status == Status.SUCCESS) storeViewModel.updateIpInfo(it.data)
        })

        storeViewModel.nearbyStores.observe(
            viewLifecycleOwner, Observer(storePagedAdapter::submitList)
        )

        storeViewModel.nearByStoresResult.observe(viewLifecycleOwner, Observer {
            log("STORE_LIST: $it")
            binding.result = it
        })
    }

    private val storeItemClickListener = { store: Store?, itemView: View, _: Boolean ->
        log("storeItemClick $store")
        if (store != null) {
            val actionNavigationHomeToStoreDetailsFragment =
                StoreFragmentDirections.actionNavigationStoreToStoreDetailsFragment(
                    store.id,
                    store.name,
                    store.uid,
                    store.proprietor,
                    store.mobile,
                    store.allFoodPrice?.toFloat() ?: 0f,
                    store.totalDonation?.toFloat() ?: 0f,
                    store.spentDonation?.toFloat() ?: 0f
                )
            ViewCompat.setTransitionName(itemView, store.id)
            val extras =
                FragmentNavigatorExtras(itemView to store.id)
            itemView.findNavController()
                .navigate(actionNavigationHomeToStoreDetailsFragment, extras)

        }
    }
}
