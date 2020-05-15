package app.roaim.dtbazar.ui.login

import android.view.View
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton

fun LoginButton.hide() {
    visibility = View.INVISIBLE
}

fun LoginButton.show() {
    visibility = View.VISIBLE
}

fun LoginButton.registerCallback(
    callbackManager: CallbackManager,
    block: (token: String?, error: String?) -> Unit
) {
    this.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
        override fun onSuccess(result: LoginResult?) {
            val token = result?.accessToken?.token
            token?.also {
                block(it, null)
            }
        }

        override fun onCancel() {
            block(null, "LoginCancel")
        }

        override fun onError(error: FacebookException?) {
            block(null, "LoginError: ${error?.message}")
        }
    })
}