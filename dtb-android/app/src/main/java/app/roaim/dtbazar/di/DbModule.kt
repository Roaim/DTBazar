package app.roaim.dtbazar.di

import android.app.Application
import androidx.room.Room
import app.roaim.dtbazar.db.CacheDb
import app.roaim.dtbazar.db.IpInfoDao
import app.roaim.dtbazar.db.ProfileDao
import app.roaim.dtbazar.db.StoreDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DbModule {
    @Provides
    @Singleton
    fun provideDb(app: Application): CacheDb = Room
        .databaseBuilder(app, CacheDb::class.java, "dtb_cache.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideIpInfoDao(db: CacheDb): IpInfoDao = db.ipInfoDao()

    @Provides
    @Singleton
    fun provideProfileDao(db: CacheDb): ProfileDao = db.profileDao()

    @Provides
    @Singleton
    fun provideStoreDao(db: CacheDb): StoreDao = db.storeDao()
}