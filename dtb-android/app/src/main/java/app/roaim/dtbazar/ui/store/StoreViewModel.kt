package app.roaim.dtbazar.ui.store

import androidx.lifecycle.*
import androidx.paging.PagedList
import app.roaim.dtbazar.model.IpInfo
import app.roaim.dtbazar.model.Result
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.model.Store
import app.roaim.dtbazar.repository.InfoRepository
import app.roaim.dtbazar.repository.StoreRepository
import javax.inject.Inject

class StoreViewModel @Inject constructor(
    infoRepository: InfoRepository,
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _ipInfo: MutableLiveData<IpInfo> = MutableLiveData<IpInfo>()

    val ipInfo: LiveData<Result<IpInfo>> = infoRepository.getIpInfo().map { result ->
        result.apply {
            takeIf { it.status == Status.SUCCESS }?.data?.also {
                _ipInfo.value = it
            }
        }
    }

    /**
     *  MUST BE OBSERVED AFTER ipInfo: LiveData<Result<IpInfo>>
     *      otherwise lat lon will be 0.0
     */
    val nearbyStores: LiveData<PagedList<Store>> = _ipInfo.switchMap {
        storeRepository.getNearByStores(viewModelScope, it)
    }

    fun updateIpInfo(ipInfo: IpInfo?) {
        if (_ipInfo.value == ipInfo) return
        _ipInfo.value = ipInfo
    }
}