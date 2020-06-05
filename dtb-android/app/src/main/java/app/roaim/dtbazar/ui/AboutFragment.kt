package app.roaim.dtbazar.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import app.roaim.dtbazar.BuildConfig
import app.roaim.dtbazar.databinding.FragmentAboutBinding
import app.roaim.dtbazar.ui.store_details.AboutFragmentClickListener
import app.roaim.dtbazar.utils.openInCustomTab
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity


class AboutFragment : Fragment(), AboutFragmentClickListener {

    companion object {
        const val BASE_URL =
            "${BuildConfig.API_HOST}${BuildConfig.API_ENDPOINT_PREFIX}${BuildConfig.API_VERSION}"
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
        startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
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
