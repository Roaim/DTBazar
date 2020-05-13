package app.roaim.dtbazar.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.roaim.dtbazar.model.Store

@Dao
interface StoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg store: Store)

    @Query("select * from store order by id desc")
    fun findAll(): LiveData<List<Store>>

    @Query("select * from store where uid = :uid order by id desc")
    fun findAllByUid(uid: String): LiveData<List<Store>>

    @Query("select * from store where id = :id")
    fun findById(id: String): LiveData<Store>
}