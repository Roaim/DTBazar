package app.roaim.dtbazar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import app.roaim.dtbazar.api.ApiService
import app.roaim.dtbazar.api.ApiUtils
import app.roaim.dtbazar.data.PrefDataSource
import app.roaim.dtbazar.model.Result
import app.roaim.dtbazar.model.Result.Companion.failed
import app.roaim.dtbazar.model.Result.Companion.loading
import app.roaim.dtbazar.model.Result.Companion.success
import app.roaim.dtbazar.utils.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val prefDataSource: PrefDataSource,
    private val apiUtils: ApiUtils
) {
    fun getToken(): LiveData<Result<String>> = liveData {
        emit(loading())
        emit(
            prefDataSource.getToken().token?.let { success(it) }
                ?: failed<String>(Constants.TOKEN_NOT_EXISTS)
        )
    }

    fun createToken(fbAccessToken: String): LiveData<Result<String>> = liveData {
        emit(loading())
        emit(
            try {
                val tokenResponse = apiService.getToken(fbAccessToken)
                tokenResponse.takeIf { it.isSuccessful }
                    ?.body()
                    ?.let {
                        prefDataSource.saveToken(it)
                        it.token
                    }?.let { success(it) }
                    ?: apiUtils.getErrorResult(tokenResponse, String::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                failed<String>(e.message)
            }
        )
    }
}