package app.roaim.dtbazar.ui.login

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import app.roaim.dtbazar.R
import com.facebook.login.widget.LoginButton

class FacebookLoginButton : LoginButton {
    constructor(context: Context?) : super(context) {
        customizeFbLoginButton()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        customizeFbLoginButton()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        customizeFbLoginButton()
    }

    private fun customizeFbLoginButton() {
        val fbIconScale = 1.45f
        val drawable: Drawable? = ResourcesCompat.getDrawable(
            resources,
            com.facebook.login.R.drawable.com_facebook_button_icon,
            null
        )
        drawable?.setBounds(
            0, 0, (drawable.intrinsicWidth * fbIconScale).toInt(),
            (drawable.intrinsicHeight * fbIconScale).toInt()
        )
        setCompoundDrawables(drawable, null, null, null)
        compoundDrawablePadding = activity.resources
            .getDimensionPixelSize(R.dimen.fb_margin_override_textpadding)
        setPadding(
            resources.getDimensionPixelSize(
                R.dimen.fb_margin_override_lr
            ),
            resources.getDimensionPixelSize(
                R.dimen.fb_margin_override_top
            ),
            resources.getDimensionPixelSize(
                R.dimen.fb_margin_override_lr
            ),
            resources.getDimensionPixelSize(
                R.dimen.fb_margin_override_bottom
            )
        )

    }
}