package app.roaim.dtbazar.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import app.roaim.dtbazar.api.ApiUtils
import app.roaim.dtbazar.db.CacheDb
import app.roaim.dtbazar.db.dao.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DbModule {
    @Provides
    @Singleton
    fun provideDb(app: Application, apiUtils: ApiUtils): CacheDb = Room
        .databaseBuilder(app, CacheDb::class.java, "dtb_cache.db")
        .addCallback(roomCallback(apiUtils))
        .fallbackToDestructiveMigration()
        .build()

    private fun roomCallback(apiUtils: ApiUtils): RoomDatabase.Callback =
        object : RoomDatabase.Callback() {
            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                super.onDestructiveMigration(db)
                apiUtils.logout()
            }
        }

    @Provides
    @Singleton
    fun provideIpInfoDao(db: CacheDb): IpInfoDao = db.ipInfoDao()

    @Provides
    @Singleton
    fun provideProfileDao(db: CacheDb): ProfileDao = db.profileDao()

    @Provides
    @Singleton
    fun provideStoreDao(db: CacheDb): StoreDao = db.storeDao()

    @Provides
    @Singleton
    fun provideDonationDao(db: CacheDb): DonationDao = db.donationDao()

    @Provides
    @Singleton
    fun provideFoodDao(db: CacheDb): FoodDao = db.foodDao()

    @Provides
    @Singleton
    fun provideStoreFoodDao(db: CacheDb): StoreFoodDao = db.storeFoodDao()
}
