package app.roaim.dtbazar.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import app.roaim.dtbazar.BuildConfig
import app.roaim.dtbazar.R
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
    fun provideOkHttpClient(prefDataSource: PrefDataSource): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${prefDataSource.getToken().token}")
                .build()
            chain.proceed(request)
        }.apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor().setLevel(
                        HttpLoggingInterceptor.Level.BODY
                    )
                )
            }
        }.build()


    @Provides
    @Singleton
    fun provideApiService(
        @Named("apiBaseUrl") apiBaseUrl: String,
        okHttpClient: OkHttpClient
    ): ApiService {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(apiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFbLogger(app: Application): AppEventsLogger = AppEventsLogger.newLogger(app)

    @Provides
    @Singleton
    fun provideGlidePlaceholder(app: Application): RoundedBitmapDrawable {
        val placeholder = BitmapFactory.decodeResource(
            app.resources,
            R.drawable.com_facebook_profile_picture_blank_square
        )
        val circularBitmapDrawable =
            RoundedBitmapDrawableFactory.create(app.resources, placeholder)
        circularBitmapDrawable.isCircular = true
        return circularBitmapDrawable
    }
}
