package app.roaim.dtbazar.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import app.roaim.dtbazar.api.ApiService
import app.roaim.dtbazar.api.getResult
import app.roaim.dtbazar.model.IpInfo
import app.roaim.dtbazar.model.Result
import app.roaim.dtbazar.model.Store
import app.roaim.dtbazar.utils.Constants.STORE_PAGE_LOAD_SIZE
import app.roaim.dtbazar.utils.Loggable
import app.roaim.dtbazar.utils.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("RemoveExplicitTypeArguments")
class StoreDataSource @Inject constructor(private val apiService: ApiService) :
    PageKeyedDataSource<Int, Store>(), Loggable {

    private var coroutineScope: CoroutineScope? = null
    private val _result = MutableLiveData<Result<List<Store>>>()
    val result: LiveData<Result<List<Store>>> = _result
    private var lat = 0.0
    private var lon = 0.0
    private var retry: (() -> Any)? = null

    fun setCoroutineScope(coroutineScope: CoroutineScope) {
        this.coroutineScope = coroutineScope
    }

    fun setLatLon(ipInfo: IpInfo) = setLatLon(ipInfo.lat, ipInfo.lon)

    fun retry() {
        val prevRetry = retry
        retry = null
        prevRetry?.invoke()
    }

    private fun setLatLon(lat: Double?, lon: Double?) {
        if (lat != null) {
            this.lat = lat
        }
        if (lon != null) {
            this.lon = lon
        }
    }

    fun clear() {
        retry = null
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Store>
    ) {
        coroutineScope?.launch {
            _result.postValue(Result.loading())
            val res = try {
                apiService.getNearByStores(lat, lon, 0, params.requestedLoadSize).getResult {
                    callback.onResult(
                        it,
                        0,
                        params.requestedLoadSize.div(STORE_PAGE_LOAD_SIZE)
                    )
                }
            } catch (e: Exception) {
                log("loadInitial", e)
                retry = {
                    loadInitial(params, callback)
                }
                Result.failed<List<Store>>(e.message)
            }
            _result.postValue(res)
        } ?: log("coroutineScope is not set", e = true)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Store>) {
        coroutineScope?.launch {
            _result.postValue(Result.loading())
            val res = try {
                apiService.getNearByStores(lat, lon, params.key, params.requestedLoadSize)
                    .getResult {
                        callback.onResult(
                            it,
                            params.key.plus(params.requestedLoadSize.div(STORE_PAGE_LOAD_SIZE))
                        )
                    }
            } catch (e: Exception) {
                log("loadAfter", e)
                retry = {
                    loadAfter(params, callback)
                }
                Result.failed<List<Store>>(e.message)
            }
            _result.postValue(res)
        } ?: log("coroutineScope is not set", e = true)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Store>) {
        // IGNORED
    }
}

class StoreDataSourceFactory @Inject constructor(private val dataSource: StoreDataSource) :
    DataSource.Factory<Int, Store>() {
    override fun create(): DataSource<Int, Store> = dataSource

    fun setCoroutineScope(coroutineScope: CoroutineScope) {
        dataSource.setCoroutineScope(coroutineScope)
    }

    fun setLatLon(ipInfo: IpInfo) {
        dataSource.setLatLon(ipInfo)
    }

    fun retry() = dataSource.retry()

    fun result() = dataSource.result

    fun clear() = dataSource.clear()
}