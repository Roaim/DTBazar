package app.roaim.dtbazar.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import app.roaim.dtbazar.db.dao.*
import app.roaim.dtbazar.model.*

@Database(
    entities = [
        Profile::class,
        IpInfo::class,
        Store::class,
        Donation::class,
        Food::class,
        StoreFood::class
    ],
    version = 18,
    exportSchema = true
)
@TypeConverters(LocationTypeConverter::class)
abstract class CacheDb : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun ipInfoDao(): IpInfoDao
    abstract fun storeDao(): StoreDao
    abstract fun donationDao(): DonationDao
    abstract fun foodDao(): FoodDao
    abstract fun storeFoodDao(): StoreFoodDao
}