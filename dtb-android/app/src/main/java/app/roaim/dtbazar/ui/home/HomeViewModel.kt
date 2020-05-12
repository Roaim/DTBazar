package app.roaim.dtbazar.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import app.roaim.dtbazar.model.Profile
import app.roaim.dtbazar.repository.AuthProfileRepository
import app.roaim.dtbazar.utils.AbsentLiveData
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val authProfileRepository: AuthProfileRepository
) : ViewModel() {

    private val _profileToken: MutableLiveData<String> = MutableLiveData<String>()

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    val text: LiveData<String> = _text

    val profile: LiveData<Profile> = Transformations.switchMap(_profileToken) {
        if (it.isNullOrBlank()) {
            AbsentLiveData.create()
        } else {
            authProfileRepository.getProfile(it)
        }
    }

    fun getProfile(token: String? = authProfileRepository.token.value) {
        if (token == _profileToken.value) return
        _profileToken.value = token
    }
}