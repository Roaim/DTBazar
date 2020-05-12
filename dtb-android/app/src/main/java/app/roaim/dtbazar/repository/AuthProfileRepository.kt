package app.roaim.dtbazar.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import app.roaim.dtbazar.api.ApiService
import app.roaim.dtbazar.model.Profile
import app.roaim.dtbazar.model.Result
import app.roaim.dtbazar.model.Result.Companion.failed
import app.roaim.dtbazar.model.Result.Companion.loading
import app.roaim.dtbazar.model.Result.Companion.success
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthProfileRepository @Inject constructor(
    private val apiService: ApiService,
    private val preferences: SharedPreferences
) {
    //    TODO remove in memory caching
    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    companion object {
        const val KEY_TOKEN = "token"
    }

    fun createToken(fbAccessToken: String): LiveData<Result<String>> = liveData {
        emit(loading())
        try {
            apiService.getToken(fbAccessToken)
                .takeIf { it.isSuccessful }
                ?.body()
                ?.token?.also {
                    _token.value = it
                    emit(success(it))
                }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(failed<String>(e))
        }
    }

    fun getProfile(token: String): LiveData<Result<Profile>> = liveData {
        try {
            emit(loading())
            apiService.getProfile("Bearer $token")
                .takeIf { it.isSuccessful }
                ?.body()
                ?.also {
                    emit(success(it))
                }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(failed<Profile>(e))
        }
    }
}