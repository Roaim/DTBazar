package app.roaim.dtbazar.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import app.roaim.dtbazar.model.Profile
import app.roaim.dtbazar.model.Result
import app.roaim.dtbazar.model.Store
import app.roaim.dtbazar.model.StorePostBody
import app.roaim.dtbazar.repository.DonationRepository
import app.roaim.dtbazar.repository.InfoRepository
import app.roaim.dtbazar.repository.StoreRepository
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    infoRepository: InfoRepository,
    private val storeRepository: StoreRepository,
    private val donationRepository: DonationRepository
) : ViewModel() {

    val profile: LiveData<Result<Profile>> = infoRepository.getProfile()

    val ipInfo = infoRepository.getIpInfo()

    private val myDonations = donationRepository.getMyDonations()

    val myCachedDonations =
        Transformations.switchMap(myDonations) { donationRepository.getMyCachedDonations() }

    private val myStores = storeRepository.getMyStores()

    val myCachedStores = Transformations.switchMap(myStores) { storeRepository.getMyCachedStores() }

    fun saveStore(name: String, address: String, mobile: String): LiveData<Result<Store>> =
        Transformations.switchMap(ipInfo) {
            storeRepository.saveStore(
                StorePostBody(
                    name = name,
                    address = address,
                    mobile = mobile,
                    location = listOf(it.data?.lat, it.data?.lon)
                )
            )
        }

    fun deleteStore(store: Store): LiveData<Result<Store>> = storeRepository.deleteStore(store)

}