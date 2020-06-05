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
import app.roaim.dtbazar.ui.home.handleBackButtonEvent
import app.roaim.dtbazar.utils.Loggable
import app.roaim.dtbazar.utils.autoCleared
import app.roaim.dtbazar.utils.log
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import javax.inject.Inject


class LoginFragment : Fragment(), Injectable, Loggable, FacebookCallback<LoginResult> {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: LoginViewModel by viewModels { viewModelFactory }

    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!
    private var callbackManager by autoCleared<CallbackManager>()
    private var mFragment by autoCleared<Fragment>()
    private var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        initFacebookLogin()
        handleBackButtonEvent()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        navController = findNavController()
        binding.loginButton.registerCallback(callbackManager, this)
    }

    override fun onStop() {
        super.onStop()
        navController = null
        binding.loginButton.unregisterCallback(callbackManager)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.token = viewModel.token.map {
            log("TOKEN: $it")
            when (it.status) {
                Status.SUCCESS -> {
                    navController?.navigate(R.id.action_loginFragment_to_navigation_home)
                }
                Status.FAILED, Status.LOGOUT -> {
                    checkFacebookAccessToken()
                }
                Status.LOADING -> {
                    // Handled in DataBinding
                }
            }
            it
        }
    }

    private fun onGetFacebookAccessToken(facebookAccessToken: String) {
        binding.loginButton.hide()
        viewModel.getToken(facebookAccessToken)
    }

    private fun checkFacebookAccessToken() {
        AccessToken.getCurrentAccessToken()?.takeIf { it.isExpired.not() }?.apply {
            log("CURRENT_FB_ACCESS_TOKEN: $token")
            onGetFacebookAccessToken(token)
        } ?: run {
            LoginManager.getInstance().logOut()
        }
        binding.loginButton.show()
    }

    private fun initFacebookLogin() {
        callbackManager = CallbackManager.Factory.create()
        mFragment = this
        binding.loginButton.fragment = mFragment
        binding.loginButton.setPermissions("email")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSuccess(result: LoginResult?) {
        log("LoginSuccess: $result")
        val token = result?.accessToken?.token
        token?.also {
            onGetFacebookAccessToken(token)
        }
    }

    override fun onCancel() {
        log("LoginCancel")
        binding.loginButton.show()
    }

    override fun onError(error: FacebookException?) {
        log("LoginError: ${error?.message}")
        binding.loginButton.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
        menu.add(0, 1, 0, "About").setIcon(R.drawable.ic_info)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            1 -> {
                // Navigate to AboutFragment
                navController?.navigate(R.id.action_navigation_login_to_aboutFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
