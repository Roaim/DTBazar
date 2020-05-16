package app.roaim.dtbazar.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import app.roaim.dtbazar.R
import app.roaim.dtbazar.databinding.LoginFragmentBinding
import app.roaim.dtbazar.di.Injectable
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.utils.Loggable
import app.roaim.dtbazar.utils.autoCleared
import app.roaim.dtbazar.utils.log
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.facebook.login.widget.LoginButton
import javax.inject.Inject


class LoginFragment : Fragment(), Injectable, Loggable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: LoginViewModel by viewModels { viewModelFactory }

    private var binding by autoCleared<LoginFragmentBinding>()
    private var findNavController by autoCleared<NavController>()

    private var callbackManager by autoCleared<CallbackManager>()
    private var loginButton by autoCleared<LoginButton>()
    private var loginManager by autoCleared<LoginManager>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = LoginFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        initFacebookLogin()
        return binding.root
    }

    override fun onDestroyView() {
        loginButton.unregisterCallback(callbackManager)
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController = findNavController()
        binding.ipInfo = viewModel.ipInfo.map {
            log("IP_INFO: $it")
            it
        }
        binding.token = viewModel.token.map {
            log("TOKEN: $it")
            if (it.status == Status.SUCCESS) {
                findNavController.navigate(R.id.action_loginFragment_to_navigation_home)
            } else if (it.status == Status.FAILED) {
                checkFacebookAccessToken()
            }
            it
        }
    }

    private fun onGetFacebookAccessToken(facebookAccessToken: String) {
        loginButton.hide()
        viewModel.getToken(facebookAccessToken)
    }

    private fun checkFacebookAccessToken() {
        AccessToken.getCurrentAccessToken()?.takeIf { it.isExpired.not() }?.apply {
            log("CURRENT_FB_ACCESS_TOKEN: $token")
            onGetFacebookAccessToken(token)
        } ?: run {
            loginManager.logOut()
        }
        loginButton.show()
    }

    private fun initFacebookLogin() {
        loginButton = binding.loginButton
        loginManager = LoginManager.getInstance()
        callbackManager = CallbackManager.Factory.create()
        val email = "email"
        loginButton.setPermissions(email)
        loginButton.fragment = this
        loginButton.registerCallback(callbackManager) { token, error ->
            log("FB_CALLBACK: token = $token | error = $error")
            if (token.isNullOrEmpty()) {
                loginButton.show()
            } else {
                onGetFacebookAccessToken(token)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

}
