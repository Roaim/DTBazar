package app.roaim.dtbazar.ui.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import app.roaim.dtbazar.R
import app.roaim.dtbazar.databinding.ViewDialogFilterStoreBinding
import app.roaim.dtbazar.di.Injectable
import app.roaim.dtbazar.ui.home.moveCameraToPosition
import app.roaim.dtbazar.utils.Loggable
import app.roaim.dtbazar.utils.log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import javax.inject.Inject

class FilterStoreDialog : DialogFragment(), Injectable, View.OnClickListener, Loggable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val storeViewModel: StoreViewModel by viewModels { viewModelFactory }

    private var _binding: ViewDialogFilterStoreBinding? = null
    private val binding get() = _binding!!
    private var map: GoogleMap? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ViewDialogFilterStoreBinding.inflate(inflater, container, false)
        binding.listener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storeViewModel.ipInfo.observe(viewLifecycleOwner, Observer {
            log("$it")
            map?.moveCameraToPosition(it)
        })
        (childFragmentManager.findFragmentById(R.id.mapFilter) as SupportMapFragment).getMapAsync {
            it.moveCameraToPosition(storeViewModel.ipInfo.value)
            map = it
        }
    }

    override fun onStart() {
        super.onStart()
        val matchParent = ViewGroup.LayoutParams.MATCH_PARENT
        val wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(matchParent, wrapContent)
    }

    override fun onDestroyView() {
        log("onDestroyView")
        super.onDestroyView()
        _binding = null
        map = null
    }

    override fun onClick(v: View?) {
        if (binding.btFilter == v) {
            map?.cameraPosition?.target?.apply {
                storeViewModel.updateIpInfo(latitude, longitude)
            }
        }
        dismiss()
    }
}