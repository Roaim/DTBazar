package app.roaim.dtbazar

import android.app.Application
import app.roaim.dtbazar.di.AppInjector
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class DtBazarApp : Application(), HasAndroidInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
        AppEventsLogger.activateApp(this)
    }

}