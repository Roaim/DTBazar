package app.roaim.dtbazar.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import app.roaim.dtbazar.model.Donation

@Dao
interface DonationDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg donation: Donation)

    @Query("select * from donation order by id desc")
    fun findAll(): LiveData<List<Donation>>

    @Query("select * from donation where donorId = :uid order by id desc")
    fun findAllByUid(uid: String): LiveData<List<Donation>>

    @Query("select * from donation where id = :id")
    fun findById(id: String): LiveData<Donation>

    @Transaction
    @Query("delete from donation")
    suspend fun deleteAll()

    @Transaction
    suspend fun refresh(vararg donation: Donation) {
        deleteAll()
        insert(*donation)
    }
}