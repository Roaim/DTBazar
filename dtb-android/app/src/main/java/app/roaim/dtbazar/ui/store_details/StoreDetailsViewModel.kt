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
    private val _uid = MutableLiveData<String>()

    val cachedStoreFoods: LiveData<List<StoreFood>> = _storeId.switchMap {
        foodRepository.getCachedStoreFoodFoods(it)
    }

    val storeFoods: LiveData<Result<List<StoreFood>>> = _storeId.switchMap {
        foodRepository.getStoreFoods(it, store.value?.data?.uid)
    }

    val cachedFoodList: LiveData<Result<List<Food>>> = foodRepository.getCachedFoods()

    val store: LiveData<Result<Store>> = _storeId.switchMap { storeRepository.getStoryBy(it) }

    val isOwnStore = _uid.switchMap(storeRepository::isOwnStore)

    fun init(uid: String?, storeId: String?) {
        if (uid != _uid.value) {
            _uid.value = uid
        }
        if (storeId != _storeId.value) {
            _storeId.value = storeId
        }
    }

    fun getFoodList(): LiveData<Result<List<Food>>> = foodRepository.getFoods()


    override fun onRetry() {
        _storeId.value?.let {
            _storeId.value = it
        }
    }

    fun saveStoreFood(storeId: String, foodId: String, stockQty: String, unitPrice: String) =
        foodRepository.saveStoreFood(
            storeId,
            StoreFoodPostBody(
                unitPrice = unitPrice.toDouble(),
                foodId = foodId,
                stockQty = stockQty.toDouble()
            )
        ).map {
            if (it.status == Status.SUCCESS) onRetry()
            it
        }

    fun addDonation(
        storeFoodId: String,
        amount: Double,
        currency: String
    ): LiveData<Result<Donation>> {
        val donationPostBody = DonationPostBody(amount, storeFoodId, currency)
        return donationRepository.addDonation(donationPostBody).map {
            if (it.status == Status.SUCCESS) onRetry()
            it
        }
    }

    fun sellFood(storeFoodId: String, name: String, nid: String, qty: Double) = FoodSellPostBody(
        storeFoodId = storeFoodId, qty = qty, nid = nid, buyerName = name
    ).let {
        storeRepository.sellFood(it)
    }.map {
        if (it.status == Status.SUCCESS) onRetry()
        it
    }

    fun addStock(storeFoodId: String, quantity: Double, unitPrice: Double) = StockPatchBody(
        unitPrice, quantity
    ).let { storeRepository.addStock(storeFoodId, it) }.map {
        if (it.status == Status.SUCCESS) onRetry()
        it
    }

    fun deleteStoreFood(storeFood: StoreFood) = foodRepository.deleteStoreFood(storeFood).map {
        if (it.status == Status.SUCCESS) onRetry()
        it
    }

}
