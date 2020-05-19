package app.roaim.dtbazar.ui.store_details

import androidx.lifecycle.*
import app.roaim.dtbazar.data.repository.DonationRepository
import app.roaim.dtbazar.data.repository.FoodRepository
import app.roaim.dtbazar.data.repository.StoreRepository
import app.roaim.dtbazar.model.*
import app.roaim.dtbazar.ui.RetryCallback
import javax.inject.Inject

class StoreDetailsViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val storeRepository: StoreRepository,
    private val donationRepository: DonationRepository
) :
    ViewModel(), RetryCallback {

    private val _storeId = MutableLiveData<String>()

    val cachedStoreFoods: LiveData<List<StoreFood>> = _storeId.switchMap {
        foodRepository.getCachedStoreFoodFoods(it)
    }

    val storeFoods: LiveData<Result<List<StoreFood>>> = _storeId.switchMap {
        foodRepository.getStoreFoods(it, store.value?.data?.uid)
    }

    val cachedFoodList: LiveData<Result<List<Food>>> = foodRepository.getCachedFoods()

    val store: LiveData<Result<Store>> = _storeId.switchMap { storeRepository.getStoryBy(it) }

    fun getStoreFood(storeId: String) {
        if (storeId == _storeId.value) return
        _storeId.value = storeId
    }

    fun getFoodList(): LiveData<Result<List<Food>>> = foodRepository.getFoods()


    override fun onRetry() {
        _storeId.postValue(_storeId.value)
    }

    fun saveStoreFood(storeId: String, foodId: String, stockQty: String, unitPrice: String) =
        foodRepository.saveStoreFood(
            storeId,
            StoreFoodPostBody(
                unitPrice = unitPrice.toDouble(),
                foodId = foodId,
                stockQty = stockQty.toDouble()
            )
        )

    fun getUid(): LiveData<String> {
        foodRepository.getUid()
        return foodRepository.uid
    }

    fun addDonation(
        storeFoodId: String,
        amount: Double,
        currency: String
    ): LiveData<Result<Donation>> {
        val donationPostBody = DonationPostBody(amount, storeFoodId, currency)
        return donationRepository.addDonation(donationPostBody).map {
            onRetry()
            it
        }
    }

}
