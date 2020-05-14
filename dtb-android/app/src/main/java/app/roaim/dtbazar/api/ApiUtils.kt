@file:Suppress("RemoveExplicitTypeArguments")

package app.roaim.dtbazar.api

import android.app.Application
import android.content.Intent
import app.roaim.dtbazar.MainActivity
import app.roaim.dtbazar.data.PrefDataSource
import app.roaim.dtbazar.model.ErrorBody
import app.roaim.dtbazar.model.Result
import com.facebook.login.LoginManager
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

    companion object {
        fun <T> getBadRequestResult(errorBody: ErrorBody): Result<T>? =
            if (errorBody.isTokenError()) Result.logout() else null
    }
}