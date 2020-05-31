package app.roaim.dtbazar.ui.store_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import app.roaim.dtbazar.data.repository.DonationRepository
import app.roaim.dtbazar.model.Donation
import app.roaim.dtbazar.model.Result
import app.roaim.dtbazar.ui.RetryCallback
import javax.inject.Inject

class PendingDonationViewModel @Inject constructor(
    private val donationRepository: DonationRepository
) :
    ViewModel(), RetryCallback {

    private val _storeId = MutableLiveData<String>()

    private val _approve = MutableLiveData<Donation>()

    val pendingDonation: LiveData<Result<List<Donation>>> =
        _storeId.switchMap { donationRepository.getPendingDonations(it) }

    val approve: LiveData<Result<Donation>> =
        _approve.switchMap { donationRepository.approveDonation(it) }

    fun approve(donation: Donation?) {
        _approve.value = donation
    }

    fun setStoreId(storeId: String) {
        if (_storeId.value == storeId) return
        _storeId.value = storeId
    }

    override fun onRetry() {
        _storeId.value = _storeId.value
    }

}
