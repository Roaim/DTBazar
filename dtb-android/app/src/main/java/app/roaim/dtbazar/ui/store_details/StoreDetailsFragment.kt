package app.roaim.dtbazar.ui.store_details

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import app.roaim.dtbazar.R
import app.roaim.dtbazar.api.ApiUtils
import app.roaim.dtbazar.databinding.FragmentStoreDetailsBinding
import app.roaim.dtbazar.databinding.ViewAddNewDonationSellBinding
import app.roaim.dtbazar.databinding.ViewAddNewStoreFoodBinding
import app.roaim.dtbazar.databinding.ViewFoodSellInvoiceBinding
import app.roaim.dtbazar.di.Injectable
import app.roaim.dtbazar.model.FoodSell
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.model.StoreFood
import app.roaim.dtbazar.ui.ListItemClickListener
import app.roaim.dtbazar.utils.Loggable
import app.roaim.dtbazar.utils.autoCleared
import app.roaim.dtbazar.utils.log
import app.roaim.dtbazar.utils.snackbar
import javax.inject.Inject

class StoreDetailsFragment : Fragment(), Injectable, Loggable, StoreFoodClickListener,
    ListItemClickListener<StoreFood>, ViewAddDonationSellClickListener {

    @Inject
    lateinit var apiUtils: ApiUtils

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory
    val viewModel: StoreDetailsViewModel by viewModels { vmFactory }
    private var _binding: FragmentStoreDetailsBinding? = null
    private val binding get() = _binding!!
    val navArgs by navArgs<StoreDetailsFragmentArgs>()
    private var _adapter: StoreFoodAdapter? = null
    private val adapter get() = _adapter!!
    var addStoreFoodDialog by autoCleared<AlertDialog>()
    var addStoreFoodBinding by autoCleared<ViewAddNewStoreFoodBinding>()
    var foodSuggestionAdapter by autoCleared<FoodSuggestionAdapter>()
    var addDonationSellBinding by autoCleared<ViewAddNewDonationSellBinding>()
    var addDonationSellDialog by autoCleared<AlertDialog>()
    var invoiceBinding by autoCleared<ViewFoodSellInvoiceBinding>()
    var invoiceDialog by autoCleared<AlertDialog>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.explode)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStoreDetailsBinding.inflate(inflater, container, false)
        binding.args = navArgs
        viewModel.store.observe(viewLifecycleOwner, Observer {
            log("STORE: $it")
            if (it.status == Status.LOGOUT) apiUtils.logout()
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
            addDonationSellBinding.isAddStock = it &&
                    addDonationSellBinding.rg.checkedRadioButtonId == addDonationSellBinding.rbStock.id
        })
        _adapter = StoreFoodAdapter()
        adapter.setItemClickListener(this)
        addDonationSellBinding =
            ViewAddNewDonationSellBinding.inflate(LayoutInflater.from(requireContext()))
        addDonationSellBinding.rg.setOnCheckedChangeListener { _, checkedId ->
            addDonationSellBinding.isSell = checkedId == addDonationSellBinding.rbSell.id
            addDonationSellBinding.isAddStock = checkedId == addDonationSellBinding.rbStock.id
        }
        addDonationSellBinding.etAmount.addTextChangedListener { input ->
            addDonationSellBinding.quantity =
                input.toString().takeIf { it.isNotEmpty() && it != "." }?.toDouble() ?: 0.0
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _adapter = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setTransitionName(binding.viewStore, navArgs.storeId)
        binding.recyclerView.adapter = adapter
        binding.retryCallback = viewModel
        viewModel.cachedStoreFoods.observe(viewLifecycleOwner, Observer(adapter::submitList))
        viewModel.storeFoods.observe(viewLifecycleOwner, Observer {
            log("STORE_FOODS: $it")
            if (it.status == Status.LOGOUT) apiUtils.logout()
            binding.result = it
            if (it.status == Status.SUCCESS) adapter.submitList(it.data)
        })
        initAddStoreFoodDialog()
        binding.listener = this
    }

    override fun onAddStoreFoodClick() {
        addStoreFoodDialog.show()
    }

    override fun onItemClick(storeFood: StoreFood?, itemView: View, isLongClick: Boolean) {
        if (isLongClick) {
            deleteStoreFood(storeFood, itemView)
        } else {
            addDonationSellDialog.show()
            addDonationSellBinding.storeFood = storeFood
            addDonationSellBinding.listener = this
        }
    }

    override fun onDialogCancelClick() {
        addDonationSellDialog.dismiss()
        addDonationSellBinding.etBuyerName.requestFocus()
    }

    override fun onAddDonationClick(storeFood: StoreFood?, quantity: String) {
        if (quantity.isNotEmpty() && storeFood != null) {
            viewModel.addDonation(
                storeFood.id,
                quantity.toDouble().times(storeFood.unitPrice).times(storeFood.food?.subsidy ?: .8),
                storeFood.food?.currency!!
            ).observe(viewLifecycleOwner, Observer {
                log("ADD_DONATION: $it")
                addDonationSellBinding.result = it
                if (it.status == Status.SUCCESS) {
                    if (viewModel.isOwnStore.value == false) {
                        AlertDialog.Builder(binding.root.context)
                            .setTitle("Donation Pending!")
                            .setMessage("Ask the store owner to accept your donation")
                            .setPositiveButton("Ok", null)
                            .show()
                    }
                    onDialogCancelClick()
                }
            })
        }
    }

    override fun onAddSellClick(storeFood: StoreFood?, qty: String, nid: String, name: String) {
        if (qty.isNotEmpty() && nid.isNotEmpty() && name.isNotEmpty() && storeFood != null) {
            viewModel.sellFood(storeFood.id, name, nid, qty.toDouble())
                .observe(viewLifecycleOwner, Observer {
                    log("FOOD_SELL: $it")
                    addDonationSellBinding.result = it
                    if (it.status == Status.SUCCESS) {
                        onDialogCancelClick()
                        showInvoice(it.data!!, storeFood)
                    }
                })
        }
    }

    override fun onAddStockClick(storeFood: StoreFood?, qty: String, unitPrice: String) {
        if (qty.isNotEmpty() && unitPrice.isNotEmpty() && storeFood != null) {
            viewModel.addStock(storeFood.id, qty.toDouble(), unitPrice.toDouble())
                .observe(
                    viewLifecycleOwner, Observer {
                        log("AddStock: $it")
                        addDonationSellBinding.result = it
                        if (it.status == Status.SUCCESS) onDialogCancelClick()
                    }
                )
        }
    }

    private fun deleteStoreFood(storeFood: StoreFood?, itemView: View) {
        if (storeFood != null && viewModel.isOwnStore.value == true) {
            itemView.snackbar("Delete: ${storeFood.food?.name}?") {
                viewModel.deleteStoreFood(storeFood).observe(viewLifecycleOwner, Observer {
                    log("DELETE_STORE_FOOD: $it")
                    if (it.status == Status.FAILED) itemView.snackbar(
                        "${it.msg}. ${storeFood.food?.name} can't be deleted.",
                        "OK"
                    )
                })
            }
        }
    }

    private fun showInvoice(foodSell: FoodSell, storeFood: StoreFood) {
        invoiceBinding.foodSell = foodSell
        invoiceBinding.storeFood = storeFood
        invoiceDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_store_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_reload -> onReloadClick()
            R.id.menu_pending_donation -> onPendingDonationClick()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onPendingDonationClick(): Boolean = true.apply {
        viewModel.isOwnStore.observe(viewLifecycleOwner, Observer {
            StoreDetailsFragmentDirections.actionNavigationStoreDetailsToPendingDonationFragment(
                navArgs.storeId, it
            ).let {
                findNavController().navigate(it)
            }
        })
    }

    private fun onReloadClick(): Boolean = true.apply {
        viewModel.onRetry()
    }

}
