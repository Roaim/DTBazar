package app.roaim.dtbazar.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import app.roaim.dtbazar.R
import app.roaim.dtbazar.di.Injectable
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import javax.inject.Inject


class HomeFragment : Fragment(), Injectable {

    private lateinit var callbackManager: CallbackManager

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val homeViewModel: HomeViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        homeViewModel.ipInfo.observe(viewLifecycleOwner, Observer {
            textView.text = it.toString()
        })

        homeViewModel.token.observe(viewLifecycleOwner, Observer {
            Log.d("fb-login", it)
            textView.append("\n\ntoken: $it")
            homeViewModel.getProfile(it)
        })

        homeViewModel.profile.observe(viewLifecycleOwner, Observer {
            Log.d("fb-login", it.toString())
            textView.append("\n\n$it")
        })
        initFacebookLogin(root)

        return root
    }

    private fun initFacebookLogin(root: View) {
        val currentAccessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = currentAccessToken != null && !currentAccessToken.isExpired
        callbackManager = CallbackManager.Factory.create()
        val email = "email"
        val loginButton = root.findViewById(R.id.login_button) as LoginButton
        loginButton.setReadPermissions(listOf(email))
        loginButton.fragment = this
        if (!isLoggedIn) {
            loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    result?.accessToken?.token?.also {
                        Log.d("fb-login", it)
                        onGetFbAccessToken(it)
                    }

                }

                override fun onCancel() {
                    Log.d("fb-login", "cancelled")
                }

                override fun onError(error: FacebookException?) {
                    Log.d("fb-login", "error", error)
                }
            })
        } else {
            currentAccessToken.token.also {
                Log.d("fb-login", it)
                onGetFbAccessToken(it)
            }
        }
    }

    private fun onGetFbAccessToken(fbAccessToken: String) {
        homeViewModel.getToken(fbAccessToken)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}
