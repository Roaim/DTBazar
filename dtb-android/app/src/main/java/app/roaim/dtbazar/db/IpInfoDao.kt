package app.roaim.dtbazar.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.roaim.dtbazar.model.IpInfo

@Dao
interface IpInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ipInfo: IpInfo)

    @Query("select * from ipinfo where ip = :ip limit 1")
    fun findByIp(ip: String): LiveData<IpInfo>
}