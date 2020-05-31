package app.roaim.dtbazar.ui.store_details

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import app.roaim.dtbazar.R
import app.roaim.dtbazar.databinding.FragmentPendingDonationBinding
import app.roaim.dtbazar.di.Injectable
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.ui.home.HomeDonationAdapter
import app.roaim.dtbazar.utils.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class PendingDonationFragment : Fragment(), Injectable, Loggable {

    @Inject
    lateinit var glidePlaceHolder: RoundedBitmapDrawable

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: PendingDonationViewModel by viewModels { viewModelFactory }

    private val navArgs by navArgs<PendingDonationFragmentArgs>()
    private var bindingComponent by autoCleared<DataBindingComponent>()
    private var binding by autoCleared<FragmentPendingDonationBinding>()
    private var adapter by autoCleared<HomeDonationAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingComponent = FragmentDataBindingComponent(this, glidePlaceHolder)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_pending_donation,
            container,
            false,
            bindingComponent
        )
        adapter = HomeDonationAdapter()
        binding.rvDonation.adapter = adapter
        adapter.setItemClickListener { donation, _, isLongClick ->
            log("ItemClick: $donation")
            AlertDialog.Builder(binding.root.context)
                .setMessage("Accept donation ${donation?.currency} ${donation?.amount?.formatted()} to ${donation?.foodName} from ${donation?.donorName}")
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.approve(donation)
                }
                .setNegativeButton("No", null)
                .show()
        }
        viewModel.setStoreId(navArgs.storeId)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.pendingDonation.observe(viewLifecycleOwner, Observer {
            log("PendingDonation: $it")
            if (it.status == Status.SUCCESS) adapter.submitList(it.data)
        })
        viewModel.approve.observe(viewLifecycleOwner, Observer {
            log("ApproveDonation: $it")
            if (it.status == Status.SUCCESS) {
                val list = adapter.currentList.toMutableList()
                list.remove(it.data)
                adapter.submitList(list)
                Toast.makeText(requireContext(), "Approved", Toast.LENGTH_SHORT).show()
            } else if (it.status == Status.FAILED) {
                Toast.makeText(requireContext(), "Failed to Approve. ${it.msg}", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val menuReload = menu.add("Reload")
        menuReload.setIcon(R.drawable.ic_refresh)
        menuReload.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        menuReload.setOnMenuItemClickListener {
            viewModel.onRetry()
            true
        }
    }

}
