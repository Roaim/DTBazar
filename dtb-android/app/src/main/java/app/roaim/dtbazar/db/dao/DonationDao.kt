package app.roaim.dtbazar.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.roaim.dtbazar.model.Donation

@Dao
interface DonationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg donation: Donation)

    @Query("select * from donation order by id desc")
    fun findAll(): LiveData<List<Donation>>

    @Query("select * from donation where donorId = :uid order by id desc")
    fun findAllByUid(uid: String): LiveData<List<Donation>>

    @Query("select * from donation where id = :id")
    fun findById(id: String): LiveData<Donation>
}