package app.roaim.dtbazar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import app.roaim.dtbazar.api.ApiService
import app.roaim.dtbazar.model.Profile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthProfileRepository @Inject constructor(private val apiService: ApiService) {
    //    TODO remove in memory caching
    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    fun createToken(fbAccessToken: String): LiveData<String> = liveData {
        try {
            apiService.getToken(fbAccessToken)
                .takeIf { it.isSuccessful }
                ?.body()
                ?.token?.also {
                    _token.value = it
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