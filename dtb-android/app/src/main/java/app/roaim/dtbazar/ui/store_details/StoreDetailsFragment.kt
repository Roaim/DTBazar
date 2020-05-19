package app.roaim.dtbazar.ui.store_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import app.roaim.dtbazar.databinding.FragmentStoreDetailsBinding
import app.roaim.dtbazar.databinding.ViewAddNewDonationBinding
import app.roaim.dtbazar.databinding.ViewAddNewStoreFoodBinding
import app.roaim.dtbazar.di.Injectable
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.model.StoreFood
import app.roaim.dtbazar.utils.Loggable
import app.roaim.dtbazar.utils.autoCleared
import app.roaim.dtbazar.utils.log
import javax.inject.Inject

class StoreDetailsFragment : Fragment(), Injectable, Loggable, StoreFoodClickListener {

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory
    val viewModel: StoreDetailsViewModel by viewModels { vmFactory }
    private var binding by autoCleared<FragmentStoreDetailsBinding>()
    val navArgs by navArgs<StoreDetailsFragmentArgs>()
    private var adapter by autoCleared<StoreFoodAdapter>()
    var addStoreFoodDialog by autoCleared<AlertDialog>()
    var addStoreFoodBinding by autoCleared<ViewAddNewStoreFoodBinding>()
    var foodSuggestionAdapter by autoCleared<FoodSuggestionAdapter>()
    var onStoreFoodItemClickListener by autoCleared<((StoreFood?, View, Boolean) -> Unit)>()
    var addDonationBinding by autoCleared<ViewAddNewDonationBinding>()
    var addDonationDialog by autoCleared<AlertDialog>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.explode)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreDetailsBinding.inflate(inflater, container, false)
        binding.args = navArgs
        viewModel.store.observe(viewLifecycleOwner, Observer {
            log("STORE: $it")
            binding.store = it
            it.data?.apply {
                viewModel.init(uid, id)
            }
        })
        viewModel.init(navArgs.uid, navArgs.storeId)
        viewModel.isOwnStore.observe(viewLifecycleOwner, Observer {
            binding.isOwner = it
        })
        adapter = StoreFoodAdapter()
        onStoreFoodItemClickListener = getStoreFoodItemClickListener()
        adapter.setItemClickListener(onStoreFoodItemClickListener)
        addDonationBinding =
            ViewAddNewDonationBinding.inflate(LayoutInflater.from(requireContext()))
        addDonationDialog =
            AlertDialog.Builder(requireContext()).setView(addDonationBinding.root).create()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setTransitionName(binding.viewStore, navArgs.storeId)
        binding.recyclerView.adapter = adapter
        binding.retryCallback = viewModel
        viewModel.cachedStoreFoods.observe(viewLifecycleOwner, Observer(adapter::submitList))
        viewModel.storeFoods.observe(viewLifecycleOwner, Observer {
            log("STORE_FOODS: $it")
            binding.result = it
            if (it.status == Status.SUCCESS) adapter.submitList(it.data)
        })
        initAddStoreFoodDialog()
        binding.listener = this
    }

    override fun onAddStoreFoodClick() {
        addStoreFoodDialog.show()
    }

    private fun getStoreFoodItemClickListener() = { storeFood: StoreFood?, _: View, _: Boolean ->
        addDonationDialog.show()
        addDonationBinding.listener = object : ViewAddDonationClickListener {
            override fun onCancelClick() {
                addDonationDialog.dismiss()
            }

            override fun onAddDonationClick(amount: String) {
                if (amount.isNotEmpty()) {
                    viewModel.addDonation(
                        storeFood!!.id,
                        amount.toDouble(),
                        storeFood.food?.currency!!
                    ).observe(viewLifecycleOwner, Observer {
                        log("ADD_DONATION: $it")
                        addDonationBinding.donation = it
                        if (it.status == Status.SUCCESS) {
                            onCancelClick()
                        }
                    })
                }
            }
        }
    }

}
