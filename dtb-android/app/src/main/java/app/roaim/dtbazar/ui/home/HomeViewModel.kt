package app.roaim.dtbazar.ui.home

import androidx.lifecycle.*
import app.roaim.dtbazar.data.repository.DonationRepository
import app.roaim.dtbazar.data.repository.InfoRepository
import app.roaim.dtbazar.data.repository.StoreRepository
import app.roaim.dtbazar.model.*
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    infoRepository: InfoRepository,
    private val storeRepository: StoreRepository,
    private val donationRepository: DonationRepository
) : ViewModel() {

    private val _uid = MutableLiveData<String>()
    private val _myStores = MediatorLiveData<List<Store>>()
    private val _myDonations = MediatorLiveData<List<Donation>>()
    private val _logout = MutableLiveData<Boolean>()

    val logout: LiveData<Boolean> = _logout

    val profile: LiveData<Result<Profile>> = infoRepository.getProfile().map {
        if (it.status == Status.SUCCESS) _uid.postValue(it.data?.id)
        if (it.status == Status.LOGOUT) _logout.postValue(true)
        it
    }

    val ipInfo = infoRepository.ipInfo

    val myDonations: LiveData<List<Donation>> = _uid.switchMap { uid ->
        if (uid != null) _myDonations.addSource(donationRepository.getMyCachedDonations(uid)) {
            _myDonations.value = it
        }
        val myRemoteDonations = donationRepository.getMyDonations()
        _myDonations.addSource(myRemoteDonations) {
            if (it.status == Status.SUCCESS) _myDonations.value = it.data
            if (it.status == Status.LOGOUT) _logout.postValue(true)
            if (it.status != Status.LOADING) _myDonations.removeSource(myRemoteDonations)
        }
        _myDonations
    }

    val myStores = _uid.switchMap { uid ->
        if (uid != null) _myStores.addSource(storeRepository.getMyCachedStores(uid)) {
            _myStores.value = it
        }
        val myRemoteStores = storeRepository.getMyStores()
        _myStores.addSource(myRemoteStores) {
            if (it.status == Status.SUCCESS) _myStores.value = it.data
            if (it.status == Status.LOGOUT) _logout.postValue(true)
            if (it.status != Status.LOADING) _myStores.removeSource(myRemoteStores)
        }
        _myStores
    }

    fun saveStore(
        name: String,
        address: String,
        mobile: String,
        target: LatLng?
    ): LiveData<Result<Store>> = storeRepository.saveStore(
        StorePostBody(
            name = name,
            address = address,
            mobile = mobile,
            location = listOf(
                target?.latitude ?: ipInfo.value?.lat,
                target?.longitude ?: ipInfo.value?.lon
            )
        )
    )

    fun deleteStore(store: Store): LiveData<Result<Store>> = storeRepository.deleteStore(store)
}