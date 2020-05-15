package app.roaim.dtbazar.ui.store

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import app.roaim.dtbazar.api.ApiService
import app.roaim.dtbazar.data.StoreDataSource
import app.roaim.dtbazar.data.StoreDataSourceFactory
import app.roaim.dtbazar.model.Store
import app.roaim.dtbazar.repository.InfoRepository
import javax.inject.Inject

class StoreViewModel @Inject constructor(
    private val infoRepository: InfoRepository,
    private val apiService: ApiService
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is store Fragment"
    }
    val text: LiveData<String> = _text


    val nearbyStores: LiveData<PagedList<Store>> = infoRepository.getIpInfo()
        .switchMap {
            LivePagedListBuilder(
                StoreDataSourceFactory(
                    StoreDataSource(
                        apiService,
                        viewModelScope,
                        it.data?.lat ?: 0.0,
                        it.data?.lon ?: 0.0
                    )
                ), 10
            ).build()
        }

}