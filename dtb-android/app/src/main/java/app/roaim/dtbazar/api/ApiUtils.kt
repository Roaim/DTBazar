package app.roaim.dtbazar.api

import android.app.Application
import android.content.Intent
import app.roaim.dtbazar.MainActivity
import app.roaim.dtbazar.data.PrefDataSource
import app.roaim.dtbazar.model.ErrorBody
import app.roaim.dtbazar.model.Result
import com.facebook.login.LoginManager
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import java.lang.reflect.Type
import javax.inject.Inject

class ApiUtils @Inject constructor(
    private val app: Application,
    private val prefDataSource: PrefDataSource
) {

    fun logout(): Boolean = try {
        LoginManager.getInstance().logOut()
        val intent = Intent(app, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        app.startActivity(intent)
        prefDataSource.clearToken()
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    suspend fun <T> getErrorResult(response: Response<*>, type: Type): Result<T> = getErrorResult(response, type)

    suspend fun <T> getErrorResult(response: Response<*>, clazz: Class<T>): Result<T> =
        if (response.code() == 400) {
            response.errorBody()?.let {
                getBadRequestResult(getErrorBody(it), clazz)
            } ?: getFailedResult(response, clazz)
        } else {
            getFailedResult(response, clazz)
        }

    private suspend fun getErrorBody(errorBody: ResponseBody): ErrorBody =
        withContext(Dispatchers.IO) {
            Gson().fromJson(errorBody.string(), ErrorBody::class.java)
        }

    private fun <T> getFailedResult(response: Response<*>, clazz: Class<T>) =
        Result.failed<T>("${response.code()} : ${response.message()}")

    companion object {
        fun <T> getBadRequestResult(errorBody: ErrorBody, clazz: Class<T>): Result<T>? =
            errorBody.message.takeIf {
                it.contains(
                    Regex(
                        "(Authorization|JWT|Bearer)",
                        RegexOption.IGNORE_CASE
                    )
                )
            }?.let {
                Result.logout()
            }
    }
}