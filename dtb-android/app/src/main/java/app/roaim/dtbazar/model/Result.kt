package app.roaim.dtbazar.model

data class Result<out T>(val status: Status, val data: T?, val msg: String?) {
    companion object {
        fun <T> success(data: T): Result<T> =
            Result<T>(Status.SUCCESS, data, null)

        fun <T> failed(msg: String?): Result<T> =
            Result<T>(Status.FAILED, null, msg)

        fun <T> loading(): Result<T> = Result<T>(Status.LOADING, null, null)

        fun <T> logout(): Result<T> = Result<T>(Status.LOGOUT, null, null)
    }
}

fun <T, E> Result<T>.map(block: (T?) -> E): Result<E> = Result(status, block(data), msg)