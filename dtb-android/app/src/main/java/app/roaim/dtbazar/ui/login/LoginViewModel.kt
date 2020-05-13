package app.roaim.dtbazar.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import app.roaim.dtbazar.model.Result
import app.roaim.dtbazar.repository.AuthRepository
import app.roaim.dtbazar.repository.InfoRepository
import app.roaim.dtbazar.utils.Constants
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    infoRepository: InfoRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _fbAccessToken = MutableLiveData<String>().apply { value = Constants.TOKEN_NOT_EXISTS }

    val ipInfo = infoRepository.getIpInfo()

    val token: LiveData<Result<String>> = Transformations.switchMap(_fbAccessToken) {
        if (Constants.TOKEN_NOT_EXISTS == it) {
            authRepository.getToken()
        } else {
            authRepository.createToken(it)
        }
    }

    fun getToken(fbAccessToken: String) {
        if (fbAccessToken == _fbAccessToken.value) return
        _fbAccessToken.value = fbAccessToken
    }
}
