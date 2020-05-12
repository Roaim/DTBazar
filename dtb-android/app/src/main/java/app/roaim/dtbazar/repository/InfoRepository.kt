package app.roaim.dtbazar.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import app.roaim.dtbazar.api.ApiService
import app.roaim.dtbazar.db.IpInfoDao
import app.roaim.dtbazar.model.IpInfo
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
    private val pref: SharedPreferences
) {
    companion object {
        const val KEY_IP = "ip"
    }

    fun getIpInfo(): LiveData<Result<IpInfo>> = pref.getString(KEY_IP, null)?.let { ip ->
        ipInfoDao.findById(ip).map { ipInfo ->
            success(ipInfo)
        }
    } ?: liveData {
        emit(loading())
        try {
            apiService.getIpInfo()
                .takeIf { it.isSuccessful }
                ?.body()
                ?.also {
                    saveIpInfo(it)
                    emit(success(it))
                }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(failed(e))
        }
    }

    private suspend fun saveIpInfo(ipInfo: IpInfo) {
        ipInfoDao.insert(ipInfo)
        pref.edit().putString(KEY_IP, ipInfo.ip).apply()
    }
}