package app.roaim.dtbazar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import app.roaim.dtbazar.api.ApiService
import app.roaim.dtbazar.api.getResult
import app.roaim.dtbazar.data.PrefDataSource
import app.roaim.dtbazar.db.IpInfoDao
import app.roaim.dtbazar.db.StoreDao
import app.roaim.dtbazar.model.*
import app.roaim.dtbazar.model.Result.Companion.failed
import app.roaim.dtbazar.model.Result.Companion.loading
import app.roaim.dtbazar.model.Result.Companion.success
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("RemoveExplicitTypeArguments")
@Singleton
class InfoRepository @Inject constructor(
    private val ipInfoDao: IpInfoDao,
    private val apiService: ApiService,
    private val prefDataSource: PrefDataSource,
    private val storeDao: StoreDao
) {

    fun getProfile(): LiveData<Result<Profile>> = liveData {
        emit(loading())
        val result = try {
            apiService.getProfile().getResult()
        } catch (e: Exception) {
            e.printStackTrace()
            failed<Profile>(e.message)
        }
        emit(result)
    }

    fun getIpInfo(): LiveData<Result<IpInfo>> =
        prefDataSource.getIp()?.let { ip ->
            ipInfoDao.findByIp(ip).map { ipInfo ->
                success(ipInfo)
            }
        } ?: liveData {
            emit(loading())
            val result = try {
                apiService.getIpInfo().getResult { saveIpInfo(it) }
            } catch (e: Exception) {
                e.printStackTrace()
                failed<IpInfo>(e.message)
            }
            emit(result)
        }

    private suspend fun saveIpInfo(ipInfo: IpInfo) {
        ipInfoDao.insert(ipInfo)
        prefDataSource.saveIp(ipInfo.ip)
    }

    //    TODO move to store repository

    fun getMyStores(): LiveData<Result<List<Store>>> = liveData {
        emit(loading())
        val result = try {
            apiService.getMyStores().getResult { storeDao.insert(*it.toTypedArray()) }
        } catch (e: Exception) {
            e.printStackTrace()
            failed<List<Store>>(e.message)
        }
        emit(result)
    }

    fun saveStore(storeBody: StorePostBody): LiveData<Result<Store>> = liveData {
        emit(loading())
        val result = try {
            apiService.saveStore(storeBody).getResult { storeDao.insert(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            failed<Store>(e.message)
        }
        emit(result)
    }
}