package app.roaim.dtbazar.ui.login

import android.view.View
import com.facebook.login.widget.LoginButton

fun LoginButton.hide() {
    visibility = View.INVISIBLE
}

fun LoginButton.show() {
    visibility = View.VISIBLE
}