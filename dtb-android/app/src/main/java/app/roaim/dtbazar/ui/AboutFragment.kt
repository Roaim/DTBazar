package app.roaim.dtbazar.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import app.roaim.dtbazar.BuildConfig
import app.roaim.dtbazar.R
import app.roaim.dtbazar.databinding.FragmentAboutBinding
import app.roaim.dtbazar.ui.store_details.AboutFragmentClickListener
import app.roaim.dtbazar.utils.openInCustomTab
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig


class AboutFragment : Fragment(), AboutFragmentClickListener {

    companion object {
        const val BASE_URL =
            "${BuildConfig.API_HOST}${BuildConfig.API_ENDPOINT_PREFIX}${BuildConfig.API_VERSION}"
        private var ossOpenedAt = 0L
    }

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listener = this
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOssLicencesClick() {
        /*
         we don't want firebase test lab's bot to enter into a 3rd party Activity
         (which we don't have control over) and crash our app due to that libraries memory leak
        */
        if (ossOpenedAt.plus(
                Firebase.remoteConfig.getLong("oss_open_interval_second").times(1000)
            ) > System.currentTimeMillis()
        ) {
            Toast.makeText(
                requireContext(),
                getString(R.string.toast_try_agin),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        })
        ossOpenedAt = System.currentTimeMillis()
    }

    override fun onTermsClick() {
        "$BASE_URL/terms.html".openInCustomTab(requireContext())
    }

    override fun onPrivacyClick() {
        "$BASE_URL/privacy.html".openInCustomTab(requireContext())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

}
