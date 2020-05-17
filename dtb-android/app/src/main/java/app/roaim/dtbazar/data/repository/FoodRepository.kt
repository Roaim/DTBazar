package app.roaim.dtbazar.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import app.roaim.dtbazar.api.ApiService
import app.roaim.dtbazar.api.getResult
import app.roaim.dtbazar.db.dao.FoodDao
import app.roaim.dtbazar.model.Food
import app.roaim.dtbazar.model.FoodPostBody
import app.roaim.dtbazar.model.Result
import app.roaim.dtbazar.model.Result.Companion.failed
import app.roaim.dtbazar.model.Result.Companion.loading
import app.roaim.dtbazar.utils.Loggable
import app.roaim.dtbazar.utils.log
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("RemoveExplicitTypeArguments")
@Singleton
class FoodRepository @Inject constructor(
    private val apiService: ApiService,
    private val foodDao: FoodDao
) : Loggable {

    fun saveFood(foodBody: FoodPostBody): LiveData<Result<Food>> = liveData {
        emit(loading())
        val result = try {
            apiService.saveFood(foodBody).getResult { foodDao.insert(it) }
        } catch (e: Exception) {
            log("saveFood", e)
            failed<Food>(e.message)
        }
        emit(result)
    }

    fun getFoods() = liveData<Result<List<Food>>> {
        emit(loading())
        val result = try {
            apiService.getFoods().getResult { foodDao.refresh(it) }
        } catch (e: Exception) {
            log("getFoods", e)
            failed<List<Food>>(e.message)
        }
        emit(result)
    }

    fun getCachedFoodFoods(): LiveData<Result<List<Food>>> = foodDao.findAll().map {
        Result.success(it)
    }

    fun deleteFood(food: Food): LiveData<Result<Food>> = liveData {
        emit(loading<Food>())
        val result = try {
            apiService.deleteFood(food.id).getResult { foodDao.delete(food) }
        } catch (e: Exception) {
            log("", e)
            failed<Food>(e.message)
        }
        emit(result)
    }
}