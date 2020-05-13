package app.roaim.dtbazar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import app.roaim.dtbazar.api.ApiService
import app.roaim.dtbazar.api.ApiUtils
import app.roaim.dtbazar.data.PrefDataSource
import app.roaim.dtbazar.db.IpInfoDao
import app.roaim.dtbazar.model.IpInfo
import app.roaim.dtbazar.model.Profile
import app.roaim.dtbazar.model.Result
import app.roaim.dtbazar.model.Result.Companion.failed
import app.roaim.dtbazar.model.Result.Companion.loading
import app.roaim.dtbazar.model.Result.Companion.success
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InfoRepository @Inject constructor(
    private val ipInfoDao: IpInfoDao,
    private val apiService: ApiService,
    private val prefDataSource: PrefDataSource,
    private val apiUtils: ApiUtils
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
}