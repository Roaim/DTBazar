package app.roaim.dtbazar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import app.roaim.dtbazar.api.ApiService
import app.roaim.dtbazar.api.getResult
import app.roaim.dtbazar.data.PrefDataSource
import app.roaim.dtbazar.db.IpInfoDao
import app.roaim.dtbazar.db.ProfileDao
import app.roaim.dtbazar.model.IpInfo
import app.roaim.dtbazar.model.Profile
import app.roaim.dtbazar.model.Result
import app.roaim.dtbazar.model.Result.Companion.failed
import app.roaim.dtbazar.model.Result.Companion.loading
import app.roaim.dtbazar.model.Result.Companion.success
import app.roaim.dtbazar.utils.Loggable
import app.roaim.dtbazar.utils.log
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("RemoveExplicitTypeArguments")
@Singleton
class InfoRepository @Inject constructor(
    private val ipInfoDao: IpInfoDao,
    private val profileDao: ProfileDao,
    private val apiService: ApiService,
    private val prefDataSource: PrefDataSource
) : Loggable {

    fun getProfile(): LiveData<Result<Profile>> =
        prefDataSource.getUid()?.let { uid ->
            profileDao.findById(uid).map {
                success(it)
            }
        } ?: liveData {
        emit(loading())
        val result = try {
            apiService.getProfile().getResult {
                profileDao.insert(it)
                prefDataSource.saveUid(it.id)
            }
        } catch (e: Exception) {
            log("getProfile", e)
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
                log("getIpInfo", e)
                failed<IpInfo>(e.message)
            }
            emit(result)
        }

    private suspend fun saveIpInfo(ipInfo: IpInfo) {
        ipInfoDao.insert(ipInfo)
        prefDataSource.saveIp(ipInfo.ip)
    }
}