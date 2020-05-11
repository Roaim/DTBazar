package app.roaim.dtbazar.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import app.roaim.dtbazar.model.Profile
import app.roaim.dtbazar.repository.InfoRepository
import app.roaim.dtbazar.utils.AbsentLiveData
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val repository: InfoRepository) : ViewModel() {


    private val _fbAccessToken = MutableLiveData<String>()
    private val _profileToken = MutableLiveData<String>()

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    val ipInfo = repository.getIpInfo()

    val token: LiveData<String> = Transformations.switchMap(_fbAccessToken) {
        if (it.isNullOrBlank()) {
            AbsentLiveData.create()
        } else {
            repository.createToken(it)
        }
    }

    fun getToken(fbAccessToken: String) {
        if (fbAccessToken == _fbAccessToken.value) return
        _fbAccessToken.value = fbAccessToken
    }

    val profile: LiveData<Profile> = Transformations.switchMap(_profileToken) {
        if (it.isNullOrBlank()) {
            AbsentLiveData.create()
        } else {
            repository.getProfile(it)
        }
    }

    fun getProfile(token: String) {
        if (token == _profileToken.value) return
        _profileToken.value = token
    }
}