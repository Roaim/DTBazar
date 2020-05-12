package app.roaim.dtbazar.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.roaim.dtbazar.R
import app.roaim.dtbazar.di.Injectable
import javax.inject.Inject


class HomeFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val homeViewModel: HomeViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)

        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        homeViewModel.profile.observe(viewLifecycleOwner, Observer {
            log(it.toString())
            textView.append("\n\n$it")
        })

        homeViewModel.getProfile()

        handleBackButtonEvent()

        return root
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
