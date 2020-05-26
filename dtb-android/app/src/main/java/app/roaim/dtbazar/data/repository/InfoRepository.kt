package app.roaim.dtbazar.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
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
import app.roaim.dtbazar.model.Status
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

    private val _ipInfo = MediatorLiveData<IpInfo>().apply {
        value = IpInfo(ip = "127.0.0.1", lat = 0.0, lon = 0.0)
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

    private val ipInfoRemote = liveData<Result<IpInfo>> {
        val result = try {
            apiService.getIpInfo().getResult { saveIpInfo(it) }
        } catch (e: Exception) {
            log("getIpInfo", e)
            failed<IpInfo>(e.message)
        }
        emit(result)
    }

    fun getIpInfo(): LiveData<IpInfo> {
        prefDataSource.getIp()?.let { ip ->
            val ipInfoLocal = ipInfoDao.findByIp(ip)
            _ipInfo.addSource(ipInfoLocal) {
                if (it != null ) _ipInfo.postValue(it)
                _ipInfo.removeSource(ipInfoLocal)
            }
        }
        _ipInfo.removeSource(ipInfoRemote)
        _ipInfo.addSource(ipInfoRemote) {
            if (it.status == Status.SUCCESS) _ipInfo.postValue(it.data)
            _ipInfo.removeSource(ipInfoRemote)
        }
        return _ipInfo
    }

    private suspend fun saveIpInfo(ipInfo: IpInfo) {
        ipInfoDao.insert(ipInfo)
        prefDataSource.saveIp(ipInfo.ip)
    }
}