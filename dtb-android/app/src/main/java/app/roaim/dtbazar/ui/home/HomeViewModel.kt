package app.roaim.dtbazar.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import app.roaim.dtbazar.model.Profile
import app.roaim.dtbazar.model.Result
import app.roaim.dtbazar.repository.InfoRepository
import app.roaim.dtbazar.utils.AbsentLiveData
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val infoRepository: InfoRepository
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    val text: LiveData<String> = _text

    val profile: LiveData<Result<Profile>> = infoRepository.getProfile()

}