package app.roaim.dtbazar.api

import app.roaim.dtbazar.model.*
import retrofit2.Response
import retrofit2.http.*

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

    @GET("store/{id}")
    @Throws(Exception::class)
    suspend fun getStore(@Path("id") id: String): Response<Store>

    @DELETE("store/{id}")
    @Throws(Exception::class)
    suspend fun deleteStore(@Path("id") id: String): Response<Store>

    @GET("store/my")
    @Throws(Exception::class)
    suspend fun getMyStores(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<List<Store>>

    @GET("store/nearby")
    @Throws(Exception::class)
    suspend fun getNearByStores(
        @Query("lat") lat: Double, @Query("lon") lon: Double,
        @Query("page") page: Int, @Query("size") size: Int
    ): Response<List<Store>>

    @GET("donation/my")
    @Throws(Exception::class)
    suspend fun getMyDonations(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): Response<List<Donation>>

    @POST("food")
    @Throws(Exception::class)
    suspend fun saveFood(@Body foodPostBody: FoodPostBody): Response<Food>

    @DELETE("food/{id}")
    @Throws(Exception::class)
    suspend fun deleteFood(@Path("id") id: String): Response<Food>

    @GET("food")
    @Throws(Exception::class)
    suspend fun getFoods(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 100
    ): Response<List<Food>>

    @GET("storeFood/store/{storeId}")
    @Throws(Exception::class)
    suspend fun getStoreFoods(
        @Path("storeId") storeId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 25
    ): Response<List<StoreFood>>

    @POST("storeFood/store/{storeId}")
    @Throws(Exception::class)
    suspend fun saveStoreFood(
        @Path("storeId") storeId: String,
        @Body storeFoodPostBody: StoreFoodPostBody
    ): Response<StoreFood>

    @POST("donation")
    @Throws(Exception::class)
    suspend fun saveDonation(@Body donationPostBody: DonationPostBody): Response<Donation>

    @POST("foodSell")
    @Throws(Exception::class)
    suspend fun postFoodSell(@Body foodSellPostBody: FoodSellPostBody): Response<FoodSell>
}