package app.roaim.dtbazar.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import app.roaim.dtbazar.model.Profile
import app.roaim.dtbazar.model.Result
import app.roaim.dtbazar.model.Store
import app.roaim.dtbazar.model.StorePostBody
import app.roaim.dtbazar.repository.InfoRepository
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val infoRepository: InfoRepository
) : ViewModel() {

    val profile: LiveData<Result<Profile>> = infoRepository.getProfile()

    val ipInfo = infoRepository.getIpInfo()

    private val myDonations = infoRepository.getMyDonations()

    val myCachedDonations = Transformations.switchMap(myDonations) { infoRepository.getMyCachedDonations() }

    private val myStores = infoRepository.getMyStores()

    val myCachedStores = Transformations.switchMap(myStores) { infoRepository.getMyCachedStores() }

    fun saveStore(name: String, mobile: String): LiveData<Result<Store>> =
        Transformations.switchMap(ipInfo) {
            infoRepository.saveStore(
                StorePostBody(
                    name = name,
                    mobile = mobile,
                    location = listOf(it.data?.lat, it.data?.lon)
                )
            )
        }

}