package app.roaim.dtbazar.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import app.roaim.dtbazar.BuildConfig
import app.roaim.dtbazar.api.ApiService
import app.roaim.dtbazar.db.CacheDb
import app.roaim.dtbazar.db.IpInfoDao
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {

    @Provides
    @Singleton
    @Named("apiBaseUrl")
    fun provideApiBaseUrl(): String =
        "${BuildConfig.API_HOST}${BuildConfig.API_ENDPOINT_PREFIX}${BuildConfig.API_VERSION}/"

    @Provides
    @Singleton
    fun provideApiService(@Named("apiBaseUrl") apiBaseUrl: String): ApiService {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        return Retrofit.Builder()
            .client(client)
            .baseUrl(apiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSharedPref(app: Application): SharedPreferences = app.getSharedPreferences(
        "main",
        MODE_PRIVATE
    )

    @Provides
    @Singleton
    fun provideDb(app: Application): CacheDb = Room
        .databaseBuilder(app, CacheDb::class.java, "dtb_cache.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideProfileDao(db: CacheDb): IpInfoDao = db.ipInfoDao()
}
