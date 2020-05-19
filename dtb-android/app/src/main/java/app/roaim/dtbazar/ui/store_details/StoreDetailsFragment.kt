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
import app.roaim.dtbazar.databinding.ViewAddNewDonationSellBinding
import app.roaim.dtbazar.databinding.ViewAddNewStoreFoodBinding
import app.roaim.dtbazar.databinding.ViewFoodSellInvoiceBinding
import app.roaim.dtbazar.di.Injectable
import app.roaim.dtbazar.model.FoodSell
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.model.StoreFood
import app.roaim.dtbazar.utils.Loggable
import app.roaim.dtbazar.utils.autoCleared
import app.roaim.dtbazar.utils.log
import app.roaim.dtbazar.utils.snackbar
import com.google.android.material.snackbar.Snackbar
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
    var addDonationSellBinding by autoCleared<ViewAddNewDonationSellBinding>()
    var addDonationSellDialog by autoCleared<AlertDialog>()
    var invoiceBinding by autoCleared<ViewFoodSellInvoiceBinding>()
    var invoiceDialog by autoCleared<AlertDialog>()

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
            addDonationSellBinding.isOwner = it
            addDonationSellBinding.isSell = it &&
                    addDonationSellBinding.rg.checkedRadioButtonId == addDonationSellBinding.rbSell.id
        })
        adapter = StoreFoodAdapter()
        onStoreFoodItemClickListener = getStoreFoodItemClickListener()
        adapter.setItemClickListener(onStoreFoodItemClickListener)
        addDonationSellBinding =
            ViewAddNewDonationSellBinding.inflate(LayoutInflater.from(requireContext()))
        addDonationSellBinding.rg.setOnCheckedChangeListener { group, checkedId ->
            addDonationSellBinding.isSell = checkedId == addDonationSellBinding.rbSell.id
        }
        addDonationSellDialog =
            AlertDialog.Builder(requireContext()).setView(addDonationSellBinding.root).create()
        invoiceBinding = ViewFoodSellInvoiceBinding.inflate(LayoutInflater.from(requireContext()))
        invoiceDialog = AlertDialog.Builder(requireContext()).setView(invoiceBinding.root).create()
        invoiceBinding.btPay.setOnClickListener {
            invoiceDialog.dismiss()
        }
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

    private fun getStoreFoodItemClickListener() =
        { storeFood: StoreFood?, itemView: View, longClick: Boolean ->
            if (longClick) {
                deleteStoreFood(storeFood, itemView)
            } else {
                addDonationSellDialog.show()
                addDonationSellBinding.listener = object : ViewAddDonationSellClickListener {
                    override fun onCancelClick() {
                        addDonationSellDialog.dismiss()
                        addDonationSellBinding.etBuyerName.requestFocus()
                    }

                    override fun onAddDonationClick(amount: String) {
                        if (amount.isNotEmpty() && storeFood != null) {
                            viewModel.addDonation(
                                storeFood.id,
                                amount.toDouble(),
                                storeFood.food?.currency!!
                            ).observe(viewLifecycleOwner, Observer {
                                log("ADD_DONATION: $it")
                                addDonationSellBinding.result = it
                                if (it.status == Status.SUCCESS) {
                                    onCancelClick()
                                }
                            })
                        }
                    }

                    override fun onAddSellClick(qty: String, nid: String, name: String) {
//                log("qty: $qty; nid: $nid; name: $name")
                        if (qty.isNotEmpty() && nid.isNotEmpty() && name.isNotEmpty() && storeFood != null) {
                            viewModel.sellFood(storeFood.id, name, nid, qty.toDouble())
                                .observe(viewLifecycleOwner, Observer {
                                    log("FOOD_SELL: $it")
                                    addDonationSellBinding.result = it
                                    if (it.status == Status.SUCCESS) {
                                        onCancelClick()
                                        showInvoice(it.data!!, storeFood)
                                    }
                                })
                        }
                }
            }
        }
    }

    private fun deleteStoreFood(storeFood: StoreFood?, itemView: View) {
        if (storeFood != null) {
            itemView.snackbar("Delete: ${storeFood.food?.name}?") {
                viewModel.deleteStoreFood(storeFood).observe(viewLifecycleOwner, Observer {
                    log("DELETE_STORE_FOOD: $it")
                    if (it.status != Status.SUCCESS) itemView.snackbar("${it.msg}. ${storeFood.food?.name} can't be deleted.", "OK")
                })
            }
        }
    }

    private fun showInvoice(foodSell: FoodSell, storeFood: StoreFood) {
        invoiceBinding.foodSell = foodSell
        invoiceBinding.storeFood = storeFood
        invoiceDialog.show()
    }

}
