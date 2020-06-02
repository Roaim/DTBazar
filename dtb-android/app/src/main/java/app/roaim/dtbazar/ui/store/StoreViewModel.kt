package app.roaim.dtbazar.ui.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import app.roaim.dtbazar.data.repository.InfoRepository
import app.roaim.dtbazar.data.repository.StoreRepository
import app.roaim.dtbazar.model.Store
import app.roaim.dtbazar.ui.RetryCallback
import javax.inject.Inject

class StoreViewModel @Inject constructor(
    private val infoRepository: InfoRepository,
    private val storeRepository: StoreRepository
) : ViewModel(), RetryCallback {

    val nearByStoresResult = storeRepository.getNearByStoresResult()

    val ipInfo = infoRepository.ipInfo

    val nearbyStores: LiveData<PagedList<Store>> = ipInfo.switchMap {
        storeRepository.getNearByStores(viewModelScope, it)
    }

    fun updateIpInfo(lat: Double?, lon: Double?) {
        infoRepository.updateIpLocation(lat, lon)
    }

    override fun onRetry() {
        storeRepository.retryNearByStores()
    }

    override fun onCleared() {
        super.onCleared()
        storeRepository.clearDataSource()
    }
}