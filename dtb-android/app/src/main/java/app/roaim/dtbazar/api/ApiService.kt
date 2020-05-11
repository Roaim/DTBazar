package app.roaim.dtbazar.api

import app.roaim.dtbazar.model.IpInfo
import app.roaim.dtbazar.model.Profile
import app.roaim.dtbazar.model.Token
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @GET("ipInfo")
    @Throws(Exception::class)
    suspend fun getIpInfo(): Response<IpInfo>

    @POST("auth/token")
    @Throws(Exception::class)
    suspend fun getToken(@Header("X-Facebook-Access-Token") fbAccessToken: String): Response<Token>

    @GET("auth/profile")
    @Throws(Exception::class)
    suspend fun getProfile(@Header("Authorization") bearerToken: String): Response<Profile>
}