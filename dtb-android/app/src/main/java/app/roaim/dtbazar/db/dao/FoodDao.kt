package app.roaim.dtbazar.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import app.roaim.dtbazar.model.Food

@Dao
interface FoodDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg food: Food)

    @Query("select * from food")
    fun findAll(): LiveData<List<Food>>

    @Query("select * from food where id = :id")
    fun findById(id: String): LiveData<Food>

    @Delete
    suspend fun delete(food: Food)

    @Query("delete from food")
    suspend fun deleteAll()

    @Transaction
    suspend fun refresh(foods: List<Food>) {
        deleteAll()
        insert(*foods.toTypedArray())
    }

}