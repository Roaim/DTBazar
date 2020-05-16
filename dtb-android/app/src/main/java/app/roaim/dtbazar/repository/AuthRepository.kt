package app.roaim.dtbazar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import app.roaim.dtbazar.api.ApiService
import app.roaim.dtbazar.api.getResult
import app.roaim.dtbazar.data.PrefDataSource
import app.roaim.dtbazar.model.ApiToken
import app.roaim.dtbazar.model.Result
import app.roaim.dtbazar.model.Result.Companion.failed
import app.roaim.dtbazar.model.Result.Companion.loading
import app.roaim.dtbazar.model.Result.Companion.success
import app.roaim.dtbazar.utils.Constants
import app.roaim.dtbazar.utils.Loggable
import app.roaim.dtbazar.utils.log
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("RemoveExplicitTypeArguments")
@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val prefDataSource: PrefDataSource
) : Loggable {
    fun getCachedToken(): LiveData<Result<String>> = liveData {
        emit(loading())
        emit(prefDataSource.getToken().token?.let { success(it) }
            ?: failed<String>(Constants.TOKEN_NOT_EXISTS))
    }

    fun createToken(fbAccessToken: String): LiveData<Result<ApiToken>> = liveData {
        emit(loading())
        emit(
            try {
                apiService.getToken(fbAccessToken).getResult { prefDataSource.saveToken(it) }
            } catch (e: Exception) {
                log("createToken", e)
                failed<ApiToken>(e.message)
            }
        )
    }
}