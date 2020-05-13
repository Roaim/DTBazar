package app.roaim.dtbazar.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import app.roaim.dtbazar.model.IpInfo
import app.roaim.dtbazar.model.Profile
import app.roaim.dtbazar.model.Store

@Database(
    entities = [
        Profile::class,
        IpInfo::class,
        Store::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(LocationTypeConverter::class)
abstract class CacheDb : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun ipInfoDao(): IpInfoDao
    abstract fun storeDao(): StoreDao
}