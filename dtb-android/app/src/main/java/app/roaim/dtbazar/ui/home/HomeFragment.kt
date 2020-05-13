package app.roaim.dtbazar.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
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
import app.roaim.dtbazar.model.Status
import javax.inject.Inject


class HomeFragment : Fragment(), Injectable, HomeButtonClickListener {

    @Inject
    lateinit var apiUtils: ApiUtils

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val homeViewModel: HomeViewModel by viewModels { viewModelFactory }

    private var binding: FragmentHomeBinding? = null
    private var addStoreBinding: ViewAddNewStoreBinding? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        handleBackButtonEvent()
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        addStoreBinding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.listener = this
        homeViewModel.profile.observe(viewLifecycleOwner, Observer {
            log(it.toString())
            if (it.status == Status.LOGOUT) apiUtils.logout()
            binding?.profile = it
        })
        homeViewModel.myStores.observe(viewLifecycleOwner, Observer {
            log(it.toString())
        })
    }

    override fun onAddNewStoreClick() {
        addStoreBinding = ViewAddNewStoreBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = AlertDialog.Builder(requireContext())
            .setView(addStoreBinding?.root)
            .create()
        addStoreBinding?.listener = object : ViewAddStoreButtonClickListener {
            override fun onAddStoreClick(storeName: String, mobile: String) {
                homeViewModel.saveStore(storeName, mobile).observe(viewLifecycleOwner, Observer {
                    log("SaveStore: $it")
                    addStoreBinding?.store = it
                    if (it.status == Status.SUCCESS) onCancelClick()
                })
            }

            override fun onCancelClick() {
                dialog.dismiss()
            }
        }
        dialog.show()
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

    private fun log(msg: String, e: Throwable? = null) {
        Log.d(this::class.simpleName, msg, e)
    }
}
