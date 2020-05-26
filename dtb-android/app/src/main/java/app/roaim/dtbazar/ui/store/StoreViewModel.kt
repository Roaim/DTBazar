package app.roaim.dtbazar.ui.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import app.roaim.dtbazar.data.repository.InfoRepository
import app.roaim.dtbazar.data.repository.StoreRepository
import app.roaim.dtbazar.model.Store
import app.roaim.dtbazar.ui.RetryCallback
import javax.inject.Inject

class StoreViewModel @Inject constructor(
    infoRepository: InfoRepository,
    private val storeRepository: StoreRepository
) : ViewModel(), RetryCallback {

    private val ipInfo = infoRepository.getIpInfo()

    val nearByStoresResult = storeRepository.getNearByStoresResult()

    val nearbyStores: LiveData<PagedList<Store>> =
        storeRepository.getNearByStores(viewModelScope, ipInfo.value!!)

    override fun onRetry() {
        storeRepository.retryNearByStores()
    }

    override fun onCleared() {
        super.onCleared()
        storeRepository.clearDataSource()
    }
}