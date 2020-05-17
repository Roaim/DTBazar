package app.roaim.dtbazar.ui.store

import androidx.lifecycle.*
import androidx.paging.PagedList
import app.roaim.dtbazar.data.repository.InfoRepository
import app.roaim.dtbazar.data.repository.StoreRepository
import app.roaim.dtbazar.model.IpInfo
import app.roaim.dtbazar.model.Result
import app.roaim.dtbazar.model.Store
import app.roaim.dtbazar.ui.RetryCallback
import javax.inject.Inject

class StoreViewModel @Inject constructor(
    infoRepository: InfoRepository,
    private val storeRepository: StoreRepository
) : ViewModel(), RetryCallback {

    private val _ipInfo: MutableLiveData<IpInfo> = MutableLiveData<IpInfo>()

    val ipInfo: LiveData<Result<IpInfo>> = infoRepository.getIpInfo()

    val nearByStoresResult = storeRepository.getNearByStoresResult()

    val nearbyStores: LiveData<PagedList<Store>> = _ipInfo.switchMap {
        storeRepository.getNearByStores(viewModelScope, it)
    }

    fun updateIpInfo(ipInfo: IpInfo?) {
        if (_ipInfo.value == ipInfo) return
        _ipInfo.value = ipInfo
    }

    override fun onRetry() {
        storeRepository.retryNearByStores()
    }

    override fun onCleared() {
        super.onCleared()
        storeRepository.clearDataSource()
    }
}