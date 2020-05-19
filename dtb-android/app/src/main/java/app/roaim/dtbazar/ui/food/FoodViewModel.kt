package app.roaim.dtbazar.ui.food

import androidx.lifecycle.*
import app.roaim.dtbazar.data.repository.FoodRepository
import app.roaim.dtbazar.model.Food
import app.roaim.dtbazar.model.FoodPostBody
import app.roaim.dtbazar.model.Result
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.ui.RetryCallback
import javax.inject.Inject

class FoodViewModel @Inject constructor(private val foodRepository: FoodRepository) :
    ViewModel(), RetryCallback {
    private val _retry = MutableLiveData<Boolean>().apply { value = false }

    val foodList = _retry.switchMap {
        foodRepository.getFoods()
    }.map {
        it.apply {
            if (status == Status.FAILED) {
                retry(false)
            }
        }
    }

    val cachedFoods = foodRepository.getCachedFoods()

    private fun retry(value: Boolean) {
        if (_retry.value == value) return
        _retry.value = value
    }

    override fun onRetry() {
        retry(true)
    }

    fun saveFood(
        name: String,
        currency: String,
        unit: String,
        startingPrice: Double,
        endingPrice: Double
    ): LiveData<Result<Food>> =
        foodRepository.saveFood(
            FoodPostBody(
                name = name,
                unit = unit,
                currency = currency,
                startingPrice = startingPrice,
                endingPrice = endingPrice
            )
        )

    fun deleteFood(food: Food) = foodRepository.deleteFood(food)
}