package app.roaim.dtbazar.ui.login

import androidx.lifecycle.*
import app.roaim.dtbazar.model.Result
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.model.map
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
            authRepository.createToken(it).map { result ->
                if (result.status==Status.SUCCESS) result.map { apiToken -> apiToken?.token!! }
                else result.map { "error: create token" }
            }
        }
    }

    fun getToken(fbAccessToken: String) {
        if (fbAccessToken == _fbAccessToken.value) return
        _fbAccessToken.value = fbAccessToken
    }
}
