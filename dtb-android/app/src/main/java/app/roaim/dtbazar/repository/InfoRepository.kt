package app.roaim.dtbazar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import app.roaim.dtbazar.api.ApiService
import app.roaim.dtbazar.api.ApiUtils
import app.roaim.dtbazar.data.PrefDataSource
import app.roaim.dtbazar.db.IpInfoDao
import app.roaim.dtbazar.db.StoreDao
import app.roaim.dtbazar.model.*
import app.roaim.dtbazar.model.Result.Companion.failed
import app.roaim.dtbazar.model.Result.Companion.loading
import app.roaim.dtbazar.model.Result.Companion.success
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InfoRepository @Inject constructor(
    private val ipInfoDao: IpInfoDao,
    private val apiService: ApiService,
    private val prefDataSource: PrefDataSource,
    private val apiUtils: ApiUtils,
    private val storeDao: StoreDao
) {

    fun getProfile(): LiveData<Result<Profile>> = liveData {
        emit(loading())
        emit(
            try {
                val profileResponse = apiService.getProfile()
                profileResponse
                    .takeIf { it.isSuccessful }
                    ?.body()
                    ?.let { success(it) }
                    ?: apiUtils.getErrorResult(profileResponse, Profile::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                failed<Profile>(e.message)
            }
        )
    }

    fun getIpInfo(): LiveData<Result<IpInfo>> = prefDataSource.getIp()
        ?.let { ip ->
            ipInfoDao.findByIp(ip).map { ipInfo ->
            success(ipInfo)
        }
    } ?: liveData {
        emit(loading())
        emit(
            try {
                val ipInfoResponse = apiService.getIpInfo()
                ipInfoResponse.takeIf { it.isSuccessful }
                    ?.body()
                    ?.let {
                        saveIpInfo(it)
                        success(it)
                    } ?: apiUtils.getErrorResult(ipInfoResponse, IpInfo::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                failed<IpInfo>(e.message)
            }
        )
    }

    private suspend fun saveIpInfo(ipInfo: IpInfo) {
        ipInfoDao.insert(ipInfo)
        prefDataSource.saveIp(ipInfo.ip)
    }

    //    TODO move to store repository

    fun getMyStores(): LiveData<Result<List<Store>>> = liveData {
        emit(loading())
        emit(
            try {
                val response = apiService.getMyStores()
                response.takeIf { it.isSuccessful }
                    ?.body()
                    ?.let {
                        saveStoreToDb(*it.toTypedArray())
                        success(it)
                    } ?: apiUtils.getErrorResult(
                    response,
                    object : TypeToken<List<Store>>() {}.type
                )
            } catch (e: Exception) {
                e.printStackTrace()
                failed<List<Store>>(e.message)
            }
        )
    }

    fun saveStore(storeBody: StorePostBody): LiveData<Result<Store>> = liveData {
        emit(loading())
        emit(
            try {
                val storeResponse = apiService.saveStore(storeBody)
                storeResponse.takeIf { it.isSuccessful }
                    ?.body()
                    ?.let {
                        saveStoreToDb(it)
                        success(it)
                    } ?: apiUtils.getErrorResult(storeResponse, Store::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                failed<Store>(e.message)
            }
        )
    }

    private suspend fun saveStoreToDb(vararg store: Store) {
        storeDao.insert(*store)
    }
}