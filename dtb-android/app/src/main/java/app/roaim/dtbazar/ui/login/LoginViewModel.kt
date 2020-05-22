package app.roaim.dtbazar.ui.login

import androidx.lifecycle.*
import app.roaim.dtbazar.data.repository.AuthRepository
import app.roaim.dtbazar.model.Result
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.model.map
import app.roaim.dtbazar.utils.Constants
import app.roaim.dtbazar.utils.Constants.TOKEN_NOT_CREATED
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private val _fbAccessToken = MutableLiveData<String>().apply { value = Constants.TOKEN_NOT_EXISTS }

    val token: LiveData<Result<String>> = Transformations.switchMap(_fbAccessToken) {
        if (Constants.TOKEN_NOT_EXISTS == it) {
            authRepository.getCachedToken()
        } else {
            authRepository.createToken(it).map { result ->
                if (result.status==Status.SUCCESS) result.map { apiToken -> apiToken?.token!! }
                else result.map { TOKEN_NOT_CREATED }
            }
        }
    }

    fun getToken(fbAccessToken: String) {
        if (fbAccessToken == _fbAccessToken.value) return
        _fbAccessToken.value = fbAccessToken
    }
}
