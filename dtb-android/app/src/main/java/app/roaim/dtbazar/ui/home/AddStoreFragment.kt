package app.roaim.dtbazar.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.roaim.dtbazar.R
import app.roaim.dtbazar.databinding.FragmentAddNewStoreBinding
import app.roaim.dtbazar.di.Injectable
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.utils.Loggable
import app.roaim.dtbazar.utils.log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import javax.inject.Inject

class AddStoreFragment : Fragment(), Injectable, Loggable, ViewAddStoreButtonClickListener {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val homeViewModel: HomeViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentAddNewStoreBinding? = null
    private val binding get() = _binding!!
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddNewStoreBinding.inflate(inflater, container, false)
        binding.listener = this
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        googleMap = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.ipInfo.observe(viewLifecycleOwner, Observer {
            log("IpInfo: $it")
            googleMap?.moveCameraToPosition(it)
        })
        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync { map ->
            setGoogleMap(map)
            googleMap?.moveCameraToPosition(homeViewModel.ipInfo.value)
        }
    }

    private fun setGoogleMap(map: GoogleMap?) {
        googleMap = map
    }

    override fun onCancelClick() {
        findNavController().popBackStack()
    }

    override fun onAddStoreClick(mobile: String, address: String, name: String) {
        if (name.isNotEmpty() && address.isNotEmpty() && mobile.isNotEmpty()) {
            homeViewModel.saveStore(name, address, mobile, googleMap?.cameraPosition?.target)
                .observe(viewLifecycleOwner, Observer {
                    log("SAVE_STORE: $it")
                    binding.store = it
                    if (it.status == Status.SUCCESS) {
                        onCancelClick()
                        binding.etName.requestFocus()
                    }
                })

        }
    }
}