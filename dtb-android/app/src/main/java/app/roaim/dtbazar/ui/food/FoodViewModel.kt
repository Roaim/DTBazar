package app.roaim.dtbazar.ui.food

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.data.repository.InfoRepository
import app.roaim.dtbazar.ui.RetryCallback
import javax.inject.Inject

class FoodViewModel @Inject constructor(infoRepository: InfoRepository) :
    ViewModel(), RetryCallback {
    private val _retry = MutableLiveData<Boolean>().apply { value = false }

    val foodList = _retry.switchMap {
        infoRepository.getFoods()
    }.map {
        it.apply {
            if (status == Status.FAILED) {
                retry(false)
            }
        }
    }

    private fun retry(value: Boolean) {
        if (_retry.value == value) return
        _retry.value = value
    }

    override fun onRetry() {
        retry(true)
    }
}