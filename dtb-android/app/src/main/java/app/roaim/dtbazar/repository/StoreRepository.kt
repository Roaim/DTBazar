package app.roaim.dtbazar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.LivePagedListBuilder
import app.roaim.dtbazar.api.ApiService
import app.roaim.dtbazar.api.getResult
import app.roaim.dtbazar.data.StoreDataSourceFactory
import app.roaim.dtbazar.db.StoreDao
import app.roaim.dtbazar.model.IpInfo
import app.roaim.dtbazar.model.Result
import app.roaim.dtbazar.model.Result.Companion.failed
import app.roaim.dtbazar.model.Result.Companion.loading
import app.roaim.dtbazar.model.Store
import app.roaim.dtbazar.model.StorePostBody
import app.roaim.dtbazar.utils.Constants
import app.roaim.dtbazar.utils.Loggable
import app.roaim.dtbazar.utils.log
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("RemoveExplicitTypeArguments")
@Singleton
class StoreRepository @Inject constructor(
    private val storeDao: StoreDao,
    private val apiService: ApiService,
    private val storeDataSourceFactory: StoreDataSourceFactory
) : Loggable {

    fun getNearByStores(coroutineScope: CoroutineScope, ipInfo: IpInfo) = LivePagedListBuilder(
        storeDataSourceFactory.apply {
            setCoroutineScope(coroutineScope)
            setLatLon(ipInfo)
        }, Constants.STORE_PAGE_LOAD_SIZE
    ).build()

    fun getMyCachedStores() = storeDao.findAll()

    fun getMyStores(): LiveData<Result<List<Store>>> =
        liveData {
            emit(loading())
            val result = try {
                apiService.getMyStores().getResult { storeDao.insert(*it.toTypedArray()) }
            } catch (e: Exception) {
                log("getMyStores", e)
                failed<List<Store>>(e.message)
            }
            emit(result)
        }

    fun saveStore(storeBody: StorePostBody): LiveData<Result<Store>> = liveData {
        emit(loading())
        val result = try {
            apiService.saveStore(storeBody).getResult { storeDao.insert(it) }
        } catch (e: Exception) {
            log("saveStore", e)
            failed<Store>(e.message)
        }
        emit(result)
    }

    fun deleteStore(store: Store): LiveData<Result<Store>> = liveData {
        emit(loading<Store>())
        val result = try {
            storeDao.delete(store)
            apiService.deleteStore(store.id).getResult()
        } catch (e: Exception) {
            log("", e)
            failed<Store>(e.message)
        }
        emit(result)
    }
}