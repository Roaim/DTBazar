package app.roaim.dtbazar.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import app.roaim.dtbazar.model.StoreFood

@Dao
interface StoreFoodDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg storeFood: StoreFood)

    @Query("select * from store_food where storeId = :storeId")
    fun findAllByStoreId(storeId: String): LiveData<List<StoreFood>>

    @Query("select * from store_food where id = :id")
    fun findById(id: String): LiveData<StoreFood>

    @Delete
    suspend fun delete(storeFood: StoreFood)

    @Query("delete from store_food")
    suspend fun deleteAll()

    @Transaction
    suspend fun refresh(storeFoods: List<StoreFood>) {
        deleteAll()
        insert(*storeFoods.toTypedArray())
    }

}