package app.roaim.dtbazar.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import app.roaim.dtbazar.databinding.ViewItemDonationBinding
import app.roaim.dtbazar.model.Donation
import app.roaim.dtbazar.ui.BaseListAdapter

class HomeDonationAdapter :
    BaseListAdapter<Donation, ViewItemDonationBinding>() {

    override fun onCreateBinding(
        bindingComponent: DataBindingComponent?,
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewItemDonationBinding = ViewItemDonationBinding.inflate(layoutInflater, parent, false)

    override fun bind(binding: ViewItemDonationBinding, item: Donation) {
        binding.donation = item
    }

}