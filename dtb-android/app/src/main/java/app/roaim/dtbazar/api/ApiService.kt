package app.roaim.dtbazar.api

import app.roaim.dtbazar.model.IpInfo
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("ipInfo")
    @Throws(Exception::class)
    suspend fun getIpInfo(): Response<IpInfo>
}