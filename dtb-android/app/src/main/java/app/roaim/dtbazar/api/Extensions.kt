@file:Suppress("RemoveExplicitTypeArguments")

package app.roaim.dtbazar.api

import app.roaim.dtbazar.model.ErrorBody
import app.roaim.dtbazar.model.Result
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Response

@Throws(Exception::class)
suspend fun <T> Response<T>.getResult(block: (suspend (T) -> Unit)? = null): Result<T> =
    takeIf { it.isSuccessful }
        ?.body()?.let {
            block?.invoke(it)
            Result.success(it)
        } ?: getErrorResult()

@Throws(Exception::class)
fun <T> Response<*>.getErrorResult(): Result<T> = when (code()) {
    400, 403 -> {
        getErrorBodyResult<T> { eb ->
            eb.takeIf { it.isTokenError() }?.let { _ ->
                Result.logout<T>(eb.message)
            }
        }
    }
    500 -> getErrorBodyResult<T>()
    else -> null
} ?: Result.failed<T>("${code()} :: ${message()}")

@Throws(Exception::class)
fun <T> Response<*>.getErrorBodyResult(
    block: (ErrorBody) -> Result<T>? = { Result.failed<T>("${code()} :: ${it.error} :: ${it.message}") }
): Result<T>? = errorBody()?.toErrorBody()?.let {
    block(it)
}

@Throws(Exception::class)
fun ResponseBody.toErrorBody(): ErrorBody? =
    Gson().fromJson(string(), ErrorBody::class.java)

fun ErrorBody.isTokenError() =
    message.contains(Regex("(Authorization|JWT|Bearer|blocked)", RegexOption.IGNORE_CASE))

