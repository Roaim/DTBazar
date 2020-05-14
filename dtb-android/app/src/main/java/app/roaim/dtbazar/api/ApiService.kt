package app.roaim.dtbazar.api

import app.roaim.dtbazar.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @GET("ipInfo")
    @Throws(Exception::class)
    suspend fun getIpInfo(): Response<IpInfo>

    @POST("auth/token")
    @Throws(Exception::class)
    suspend fun getToken(@Header("X-Facebook-Access-Token") fbAccessToken: String): Response<ApiToken>

    @GET("auth/profile")
    @Throws(Exception::class)
    suspend fun getProfile(): Response<Profile>

    @POST("store")
    @Throws(Exception::class)
    suspend fun saveStore(@Body storePostBody: StorePostBody): Response<Store>

    @GET("store/my")
    @Throws(Exception::class)
    suspend fun getMyStores(): Response<List<Store>>

    @GET("donation/my")
    @Throws(Exception::class)
    suspend fun getMyDonations(): Response<List<Donation>>
}