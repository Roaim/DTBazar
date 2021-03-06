package app.roaim.dtbazar.data

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.roaim.dtbazar.model.ApiToken
import app.roaim.dtbazar.utils.Constants.KEY_IP
import app.roaim.dtbazar.utils.Constants.KEY_TOKEN
import app.roaim.dtbazar.utils.Constants.KEY_TOKEN_EXPIRE
import app.roaim.dtbazar.utils.Constants.KEY_UID
import app.roaim.dtbazar.utils.DateUtils
import javax.inject.Inject

class PrefDataSource @Inject constructor(private val preferences: SharedPreferences) {

    private val _ip = MutableLiveData<String>().apply {
        value = preferences.getString(KEY_IP, null) ?: "127.0.0.1"
    }

    val ip: LiveData<String> = _ip

    fun saveToken(apiToken: ApiToken) {
        val editor = preferences.edit()
        editor.putString(KEY_TOKEN, apiToken.token)
        editor.putLong(KEY_TOKEN_EXPIRE, DateUtils.totMillis(apiToken.expires))
        editor.apply()
    }

    fun getToken(): ApiToken = ApiToken(
        expires = DateUtils.from(preferences.getLong(KEY_TOKEN_EXPIRE, 0L)),
        token = preferences.getString(KEY_TOKEN, null)
    )

    fun clearSession(): Boolean {
        val editor = preferences.edit()
        editor.remove(KEY_TOKEN)
        editor.remove(KEY_UID)
        editor.remove(KEY_IP)
        editor.apply()
        return true
    }

    fun saveIp(ip: String) {
        preferences.edit().putString(KEY_IP, ip).apply()
        if (_ip.value != ip) _ip.postValue(ip)
    }

    fun saveUid(id: String) {
        preferences.edit().putString(KEY_UID, id).apply()
    }

    fun getUid(): String = preferences.getString(KEY_UID, "") ?: ""
}