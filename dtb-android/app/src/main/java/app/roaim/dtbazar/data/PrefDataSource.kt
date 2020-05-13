package app.roaim.dtbazar.data

import android.content.SharedPreferences
import app.roaim.dtbazar.model.ApiToken
import app.roaim.dtbazar.utils.Constants.KEY_IP
import app.roaim.dtbazar.utils.Constants.KEY_TOKEN
import app.roaim.dtbazar.utils.Constants.KEY_TOKEN_EXPIRE
import app.roaim.dtbazar.utils.DateUtils
import javax.inject.Inject

class PrefDataSource @Inject constructor(private val preferences: SharedPreferences) {

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

    fun clearToken() = preferences.edit().remove(KEY_TOKEN).commit()

    fun saveIp(ip: String) {
        preferences.edit().putString(KEY_IP, ip).apply()
    }

    fun getIp(): String? = preferences.getString(KEY_IP, null)
}