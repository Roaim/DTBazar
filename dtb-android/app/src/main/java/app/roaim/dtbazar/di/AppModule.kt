package app.roaim.dtbazar.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import app.roaim.dtbazar.BuildConfig
import app.roaim.dtbazar.api.ApiService
import app.roaim.dtbazar.data.PrefDataSource
import com.facebook.appevents.AppEventsLogger
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class, DbModule::class])
class AppModule {

    @Provides
    @Singleton
    @Named("apiBaseUrl")
    fun provideApiBaseUrl(): String =
        "${BuildConfig.API_HOST}${BuildConfig.API_ENDPOINT_PREFIX}${BuildConfig.API_VERSION}/"

    @Provides
    @Singleton
    fun provideSharedPref(app: Application): SharedPreferences = app.getSharedPreferences(
        "main",
        MODE_PRIVATE
    )

    @Provides
    @Singleton
    fun provideApiService(
        @Named("apiBaseUrl") apiBaseUrl: String,
        prefDataSource: PrefDataSource
    ): ApiService {
        val clientBuilder = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${prefDataSource.getToken().token}")
                    .build()
                chain.proceed(request)
            }
        if (BuildConfig.DEBUG) {
            clientBuilder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }
        return Retrofit.Builder()
            .client(clientBuilder.build())
            .baseUrl(apiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFbLogger(app: Application): AppEventsLogger = AppEventsLogger.newLogger(app)
}
