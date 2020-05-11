package app.roaim.dtbazar.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import app.roaim.dtbazar.api.ApiService
import app.roaim.dtbazar.db.IpInfoDao
import app.roaim.dtbazar.model.IpInfo
import app.roaim.dtbazar.model.Profile
import app.roaim.dtbazar.model.Token
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

    fun createToken(fbAccessToken: String): LiveData<String> = liveData {
        try {
            apiService.getToken(fbAccessToken)
                .takeIf { it.isSuccessful }
                ?.body()
                ?.token?.also {
                    emit(it)
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getProfile(token: String): LiveData<Profile> = liveData {
        try {
            apiService.getProfile("Bearer $token")
                .takeIf { it.isSuccessful }
                ?.body()
                ?.also {
                    emit(it)
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}