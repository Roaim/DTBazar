package app.roaim.dtbazar.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import app.roaim.dtbazar.R
import app.roaim.dtbazar.databinding.FragmentForceUpdateBinding
import app.roaim.dtbazar.utils.Loggable
import app.roaim.dtbazar.utils.log
import app.roaim.dtbazar.utils.navigateToPlayStore

class ForceUpdateDialog : DialogFragment(), View.OnClickListener, Loggable {

    private var _binding: FragmentForceUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.UpdateDialogAnimation
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForceUpdateBinding.inflate(inflater, container, false)
        binding.listener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        val matchParent = ViewGroup.LayoutParams.MATCH_PARENT
        val wrapContent = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(matchParent, wrapContent)
    }

    override fun onDestroyView() {
        log("onDestroyView")
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        v?.navigateToPlayStore()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        requireActivity().finish()
    }
}