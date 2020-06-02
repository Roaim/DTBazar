package app.roaim.dtbazar.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import app.roaim.dtbazar.api.ApiService
import app.roaim.dtbazar.api.getResult
import app.roaim.dtbazar.data.PrefDataSource
import app.roaim.dtbazar.data.StoreDataSourceFactory
import app.roaim.dtbazar.db.dao.StoreDao
import app.roaim.dtbazar.model.*
import app.roaim.dtbazar.model.Result.Companion.failed
import app.roaim.dtbazar.model.Result.Companion.loading
import app.roaim.dtbazar.utils.Constants.STORE_PAGE_INITIAL_SIZE
import app.roaim.dtbazar.utils.Constants.STORE_PAGE_LOAD_SIZE
import app.roaim.dtbazar.utils.Constants.STORE_PAGE_PREFETCH
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
    private val storeDataSourceFactory: StoreDataSourceFactory,
    private val prefDataSource: PrefDataSource
) : Loggable {

    private val pageConfig = PagedList.Config.Builder()
        .setInitialLoadSizeHint(STORE_PAGE_INITIAL_SIZE)
        .setPrefetchDistance(STORE_PAGE_PREFETCH)
        .setPageSize(STORE_PAGE_LOAD_SIZE)
        .build()

    fun isOwnStore(storeUid: String): LiveData<Boolean> = liveData { emit(prefDataSource.getUid() == storeUid) }

    fun getNearByStores(coroutineScope: CoroutineScope, ipInfo: IpInfo) = LivePagedListBuilder(
        storeDataSourceFactory.apply {
            setCoroutineScope(coroutineScope)
            setLatLon(ipInfo.lat, ipInfo.lon)
        }, pageConfig
    ).build()

    fun getNearByStoresResult() = storeDataSourceFactory.result()

    fun retryNearByStores() = storeDataSourceFactory.retry()

    fun getMyCachedStores(uid: String) = storeDao.findAllByUid(uid)

    fun getMyStores(): LiveData<Result<List<Store>>> =
        liveData {
            emit(loading())
            val result = try {
                apiService.getMyStores().getResult { storeDao.refresh(*it.toTypedArray()) }
            } catch (e: Exception) {
                log("getMyStores", e)
                failed<List<Store>>(e.message)
            }
            emit(result)
        }

    fun getStoryBy(storeId: String) = liveData<Result<Store>> {
        emit(loading())
        val result = try {
            apiService.getStore(storeId)
                .getResult { if (prefDataSource.getUid() == it.uid) storeDao.insert(it) }
        } catch (e: Exception) {
            log("getStoreById", e)
            failed<Store>(e.message)
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

    fun clearDataSource() {
        storeDataSourceFactory.clear()
    }

    fun sellFood(foodSellPostBody: FoodSellPostBody) = liveData<Result<FoodSell>> {
        emit(loading())
        val result = try {
            apiService.postFoodSell(foodSellPostBody).getResult()
        } catch (e: Exception) {
            log("sellFood", e)
            failed<FoodSell>(e.message)
        }
        emit(result)
    }

    fun addStock(storeFoodId: String, stockPatchBody: StockPatchBody) =
        liveData<Result<StoreFood>> {
            emit(loading())
            val result = try {
                apiService.addStock(storeFoodId, stockPatchBody).getResult()
            } catch (e: Exception) {
                log("addStock", e)
                failed<StoreFood>(e.message)
            }
            emit(result)
        }
}