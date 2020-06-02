package app.roaim.dtbazar.data.repository

import androidx.lifecycle.*
import app.roaim.dtbazar.api.ApiService
import app.roaim.dtbazar.api.getResult
import app.roaim.dtbazar.data.PrefDataSource
import app.roaim.dtbazar.db.dao.IpInfoDao
import app.roaim.dtbazar.db.dao.ProfileDao
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

    private val _ipInfo = MediatorLiveData<IpInfo>()

    // Remote Api Info will only be fetched on App re-create due to api request limit of 45/min
    private val ipInfoRemote = liveData<Result<IpInfo>> {
        val result = try {
            apiService.getIpInfo().getResult { saveIpInfo(it) }
        } catch (e: Exception) {
            log("getIpInfo", e)
            failed<IpInfo>(e.message)
        }
        emit(result)
    }

    val ipInfo: LiveData<IpInfo> = prefDataSource.ip.switchMap { ip ->
        val ipInfoCached = ipInfoDao.findByIp(ip)
        _ipInfo.addSource(ipInfoCached) { ipInfo ->
            _ipInfo.removeSource(ipInfoCached)
            if (ipInfo != null) _ipInfo.postValue(ipInfo)
            else _ipInfo.postValue(IpInfo(ip = ip, lat = 0.0, lon = 0.0))
        }

        _ipInfo.addSource(ipInfoRemote) {
            _ipInfo.removeSource(ipInfoRemote)
        }
        _ipInfo
    }

    private suspend fun saveIpInfo(ipInfo: IpInfo) {
        if (ipInfo.ip == prefDataSource.ip.value) return
        ipInfoDao.insert(ipInfo)
        prefDataSource.saveIp(ipInfo.ip)
    }

    fun getProfile(): LiveData<Result<Profile>> = prefDataSource.getUid()
        .takeIf { it.isNotEmpty() }
        ?.let { profileDao.findById(it) }
        ?.map { success(it) }
        ?: liveData {
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
}