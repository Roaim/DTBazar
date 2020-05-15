package app.roaim.dtbazar.data

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import app.roaim.dtbazar.api.ApiService
import app.roaim.dtbazar.api.getResult
import app.roaim.dtbazar.model.Store
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class StoreDataSource(
    private val apiService: ApiService,
    private val coroutineScope: CoroutineScope,
    private val lat: Double,
    private val lon: Double
) : PageKeyedDataSource<Int, Store>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Store>
    ) {
        coroutineScope.launch {
            apiService.getNearByStores(lat, lon, 0, params.requestedLoadSize).getResult {
                callback.onResult(it, 0, 1)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Store>) {
        coroutineScope.launch {
            apiService.getNearByStores(lat, lon, params.key, params.requestedLoadSize).getResult {
                callback.onResult(it, params.key.inc())
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Store>) {
        // IGNORED
    }
}

class StoreDataSourceFactory(private val dataSource: StoreDataSource) :
    DataSource.Factory<Int, Store>() {
    override fun create(): DataSource<Int, Store> = dataSource
}