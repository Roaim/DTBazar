package app.roaim.dtbazar.ui.login

import androidx.lifecycle.*
import app.roaim.dtbazar.repository.AuthProfileRepository
import app.roaim.dtbazar.repository.InfoRepository
import app.roaim.dtbazar.utils.AbsentLiveData
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val infoRepository: InfoRepository,
    private val authProfileRepository: AuthProfileRepository
) : ViewModel() {

    private val _fbAccessToken = MutableLiveData<String>()

    val ipInfo = infoRepository.getIpInfo()

    val token: LiveData<String> = Transformations.switchMap(_fbAccessToken) {
        if (it.isNullOrBlank()) {
            AbsentLiveData.create()
        } else {
            authProfileRepository.createToken(it)
        }
    }

    fun getToken(fbAccessToken: String) {
        if (fbAccessToken == _fbAccessToken.value) return
        _fbAccessToken.value = fbAccessToken
    }
}
