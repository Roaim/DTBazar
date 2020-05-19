package app.roaim.dtbazar.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import app.roaim.dtbazar.api.ApiService
import app.roaim.dtbazar.api.getResult
import app.roaim.dtbazar.data.PrefDataSource
import app.roaim.dtbazar.db.dao.FoodDao
import app.roaim.dtbazar.db.dao.StoreFoodDao
import app.roaim.dtbazar.model.*
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
    private val foodDao: FoodDao,
    private val storeFoodDao: StoreFoodDao,
    private val prefDataSource: PrefDataSource
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

    fun getCachedFoods(): LiveData<Result<List<Food>>> = foodDao.findAll().map {
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

    fun saveStoreFood(storeId: String, foodBody: StoreFoodPostBody): LiveData<Result<StoreFood>> =
        liveData {
            emit(loading())
            val result = try {
                apiService.saveStoreFood(storeId, foodBody).getResult { storeFoodDao.insert(it) }
            } catch (e: Exception) {
                log("saveFood", e)
                failed<StoreFood>(e.message)
            }
            emit(result)
        }

    fun getStoreFoods(storeId: String, uid: String? = null) =
        liveData<Result<List<StoreFood>>> {
            emit(loading())
            val result = try {
                apiService.getStoreFoods(storeId)
                    .getResult { if (prefDataSource.getUid() == uid) storeFoodDao.insert(*it.toTypedArray()) }
            } catch (e: Exception) {
                log("getStoreFoods", e)
                failed<List<StoreFood>>(e.message)
            }
            emit(result)
        }

    fun getCachedStoreFoodFoods(storeId: String): LiveData<List<StoreFood>> =
        storeFoodDao.findAllByStoreId(storeId)

    fun sellFood(foodSellPostBody: FoodSellPostBody) = liveData<Result<FoodSell>> {
        emit(loading())
        val result = try {
            apiService.postFoodSell(foodSellPostBody).getResult()
        } catch (e: Exception) {
            log("sellFood", e)
            failed<FoodSell>(e.message)
        }
        emit(result)
    }
}