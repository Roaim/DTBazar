package app.roaim.dtbazar.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.roaim.dtbazar.R
import app.roaim.dtbazar.databinding.LoginFragmentBinding
import app.roaim.dtbazar.di.Injectable
import app.roaim.dtbazar.model.Status
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import javax.inject.Inject


class LoginFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: LoginViewModel by viewModels { viewModelFactory }

    private var binding: LoginFragmentBinding? = null

    private lateinit var callbackManager: CallbackManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = LoginFragmentBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.ipInfo.observe(viewLifecycleOwner, Observer {
            log("${it.data}")
            binding?.ipInfo = it
        })

        viewModel.token.observe(viewLifecycleOwner, Observer {
            log("TOKEN $it")
            binding?.status = it.status
            if (it.status == Status.SUCCESS) {
                findNavController().navigate(R.id.action_loginFragment_to_navigation_home)
            } else if (it.status == Status.FAILED) {
                binding?.info?.append(it.e?.message)
                binding?.loginButton?.visibility = View.VISIBLE
            }
        })

        initFbLogin()
    }

    private fun onGetFbAccessToken(fbAccessToken: String) {
        binding?.loginButton?.visibility = View.INVISIBLE
        viewModel.getToken(fbAccessToken)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.loginButton?.unregisterCallback(callbackManager)
        binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun initFbLogin() {
        val currentAccessToken = AccessToken.getCurrentAccessToken()
        log("CurrentAccessToken: ${currentAccessToken?.token}")
        val isLoggedIn = currentAccessToken != null && !currentAccessToken.isExpired
        callbackManager = CallbackManager.Factory.create()
        if (isLoggedIn) {
            onGetFbAccessToken(currentAccessToken.token)
        } else {
            LoginManager.getInstance().logOut()
        }
        binding?.loginButton?.also { loginButton ->
            registerCallbackFbLoginButton(loginButton)
        }
    }

    private fun registerCallbackFbLoginButton(loginButton: LoginButton) {
        val email = "email"
        loginButton.setPermissions(email)
        loginButton.fragment = this
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                val token = result?.accessToken?.token
                log("LoginSuccess: $token")
                token?.also {
                    onGetFbAccessToken(it)
                }
            }

            override fun onCancel() {
                log("LoginCancel")
                loginButton.visibility = View.VISIBLE
            }

            override fun onError(error: FacebookException?) {
                log("LoginError: ${error?.message}", error?.cause)
                loginButton.visibility = View.VISIBLE
            }
        })
    }

    private fun log(msg: String, e: Throwable? = null) {
        Log.d(LoginFragment::class.simpleName, msg, e)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

}
