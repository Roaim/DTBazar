package app.roaim.dtbazar.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import app.roaim.dtbazar.api.ApiService
import app.roaim.dtbazar.db.IpInfoDao
import app.roaim.dtbazar.model.IpInfo
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

    fun getIpInfo(): LiveData<IpInfo> = pref.getString(KEY_IP, null)?.let {
        ipInfoDao.findById(it)
    } ?: liveData {
        try {
            apiService.getIpInfo()
                .takeIf { it.isSuccessful }
                ?.body()
                ?.also {
                    saveIpInfo(it)
                    emit(it)
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun saveIpInfo(ipInfo: IpInfo) {
        ipInfoDao.insert(ipInfo)
        pref.edit().putString(KEY_IP, ipInfo.ip).apply()
    }
}