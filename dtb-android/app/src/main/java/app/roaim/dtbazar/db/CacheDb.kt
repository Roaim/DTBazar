package app.roaim.dtbazar.db

import androidx.room.Database
import androidx.room.RoomDatabase
import app.roaim.dtbazar.model.IpInfo
import app.roaim.dtbazar.model.Profile

@Database(
    entities = [
        Profile::class,
        IpInfo::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CacheDb : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun ipInfoDao(): IpInfoDao
}