package app.roaim.dtbazar.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import app.roaim.dtbazar.databinding.ViewItemDonationBinding
import app.roaim.dtbazar.model.Donation
import app.roaim.dtbazar.ui.RecyclerBindingAdapter
import app.roaim.dtbazar.ui.RecyclerBindingViewHolder

class HomeDonationAdapter :
    RecyclerBindingAdapter<Donation, HomeDonationViewHolder, ViewItemDonationBinding>() {
    override fun onCreateViewHolder(
        binding: ViewItemDonationBinding,
        viewType: Int
    ): HomeDonationViewHolder = HomeDonationViewHolder(binding)

    override fun onGetViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): ViewItemDonationBinding = ViewItemDonationBinding.inflate(layoutInflater, parent, false)

}

class HomeDonationViewHolder(binding: ViewItemDonationBinding) :
    RecyclerBindingViewHolder<Donation, ViewItemDonationBinding>(binding) {
    override fun bind(item: Donation) {
        binding.donation = item
    }
}