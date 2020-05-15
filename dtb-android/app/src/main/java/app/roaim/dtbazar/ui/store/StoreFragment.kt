package app.roaim.dtbazar.ui.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import app.roaim.dtbazar.R
import app.roaim.dtbazar.di.Injectable
import app.roaim.dtbazar.ui.StorePagedAdapter
import javax.inject.Inject

class StoreFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val storeViewModel: StoreViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_store, container, false)
        val textView: TextView = root.findViewById(R.id.text_store)
        val rvStore: RecyclerView = root.findViewById(R.id.rvStore)
        val storePagedAdapter = StorePagedAdapter()
        rvStore.adapter = storePagedAdapter

        storeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        storeViewModel.nearbyStores.observe(
            viewLifecycleOwner,
            Observer(storePagedAdapter::submitList)
        )
        return root
    }
}
