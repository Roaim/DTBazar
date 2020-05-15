package app.roaim.dtbazar.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import app.roaim.dtbazar.R
import app.roaim.dtbazar.databinding.LoginFragmentBinding
import app.roaim.dtbazar.di.Injectable
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.utils.Loggable
import app.roaim.dtbazar.utils.log
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import javax.inject.Inject


class LoginFragment : Fragment(), Injectable, Loggable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: LoginViewModel by viewModels { viewModelFactory }

    private var binding: LoginFragmentBinding? = null

    private var callbackManager: CallbackManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = LoginFragmentBinding.inflate(inflater)
        binding?.lifecycleOwner = viewLifecycleOwner
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            ipInfo = viewModel.ipInfo.map {
                log("IP_INFO: $it")
                it
            }

            token = viewModel.token.map {
                log("TOKEN: $it")
                if (it.status == Status.SUCCESS) {
                    findNavController().navigate(R.id.action_loginFragment_to_navigation_home)
                } else if (it.status == Status.FAILED) {
                    initFbLogin()
                }
                it
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callbackManager?.also { binding?.loginButton?.unregisterCallback(it) }
        callbackManager = null
        binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun onGetFbAccessToken(fbAccessToken: String) {
        binding?.loginButton?.hide()
        viewModel.getToken(fbAccessToken)
    }

    private fun initFbLogin() {
        val currentAccessToken = AccessToken.getCurrentAccessToken()
        log("CurrentAccessToken: ${currentAccessToken?.token}")
        val isLoggedIn = currentAccessToken != null && !currentAccessToken.isExpired
        if (isLoggedIn) {
            onGetFbAccessToken(currentAccessToken.token)
        } else {
            LoginManager.getInstance().logOut()
        }
        binding?.loginButton?.also { loginButton ->
            loginButton.show()
            val email = "email"
            loginButton.setPermissions(email)
            loginButton.fragment = this
            callbackManager = CallbackManager.Factory.create()
            loginButton.registerCallback(callbackManager!!) { token, error ->
                log("FB_CALLBACK: token = $token | error = $error")
                if (token.isNullOrEmpty()) {
                    loginButton.show()
                } else {
                    onGetFbAccessToken(token)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

}
