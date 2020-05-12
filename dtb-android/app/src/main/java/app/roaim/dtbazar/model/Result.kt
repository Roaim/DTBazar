package app.roaim.dtbazar.model

data class Result<out T>(val status: Status, val e: Exception?, val data: T?) {
    companion object {
        fun <T> success(data: T): Result<T> =
            Result(Status.SUCCESS, null, data)

        fun <T> failed(e: Exception): Result<T> =
            Result(Status.FAILED, e, null)

        fun <T> loading(): Result<T> = Result(Status.LOADING, null, null)
    }
}