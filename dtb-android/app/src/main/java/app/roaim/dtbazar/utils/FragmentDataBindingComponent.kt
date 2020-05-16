package app.roaim.dtbazar.utils

import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.databinding.DataBindingComponent
import androidx.fragment.app.Fragment

class FragmentDataBindingComponent(fragment: Fragment, glidePlaceHolder: RoundedBitmapDrawable) :
    DataBindingComponent {

    private val adapter = FragmentBindingAdapters(fragment, glidePlaceHolder)

    override fun getFragmentBindingAdapters() = adapter
}