package app.roaim.dtbazar.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import app.roaim.dtbazar.model.Store

@Dao
interface StoreDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg store: Store)

    @Query("select * from store")
    fun findAll(): LiveData<List<Store>>

    @Query("select * from store where uid = :uid order by id desc")
    fun findAllByUid(uid: String): LiveData<List<Store>>

    @Query("select * from store where id = :id")
    fun findById(id: String): LiveData<Store>

    @Delete
    suspend fun delete(id: Store)

    @Query("delete from store")
    suspend fun deleteAll()

    @Transaction
    suspend fun refresh(vararg stores: Store) {
        deleteAll()
        insert(*stores)
    }

}