package app.roaim.dtbazar.model

data class Result<out T>(val status: Status, val data: T?, val msg: String?) {
    companion object {
        fun <T> success(data: T): Result<T> =
            Result(Status.SUCCESS, data, null)

        fun <T> failed(msg: String?): Result<T> =
            Result(Status.FAILED, null, msg)

        fun <T> loading(): Result<T> = Result(Status.LOADING, null, null)

        fun <T> logout(): Result<T> = Result(Status.LOGOUT, null, null)
    }
}