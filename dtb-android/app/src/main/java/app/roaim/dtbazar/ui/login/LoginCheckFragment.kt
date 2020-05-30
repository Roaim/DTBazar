package app.roaim.dtbazar.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import app.roaim.dtbazar.R
import app.roaim.dtbazar.di.Injectable
import app.roaim.dtbazar.model.Status
import app.roaim.dtbazar.utils.Loggable
import app.roaim.dtbazar.utils.log
import javax.inject.Inject

class LoginCheckFragment : Fragment(), Injectable, Loggable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: LoginViewModel by viewModels { viewModelFactory }
    private var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = findNavController()
        viewModel.token.observe(this, Observer {
            log("TOKEN: $it")
            when (it.status) {
                Status.SUCCESS -> {
                    navController?.navigate(R.id.action_loginCheckFragment_to_navigation_home)
                }
                Status.FAILED, Status.LOGOUT -> {
                    navController?.navigate(R.id.action_loginCheckFragment_to_navigation_login)
                }
                Status.LOADING -> {
                }
            }
        })
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        navController = null
    }
}