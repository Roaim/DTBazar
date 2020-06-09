package app.roaim.dtbazar

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import app.roaim.dtbazar.api.ApiUtils
import app.roaim.dtbazar.ui.ForceUpdateDialog
import app.roaim.dtbazar.utils.Loggable
import app.roaim.dtbazar.utils.log
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasAndroidInjector, Loggable {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var apiUtils: ApiUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_login,
                R.id.navigation_home,
                R.id.navigation_store,
                R.id.navigation_food
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_login || destination.id == R.id.navigation_about) {
                navView.visibility = View.GONE
            } else {
                navView.visibility = View.VISIBLE
            }
        }

        val remoteConfig = Firebase.remoteConfig
        val remoteConfigSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 36
        }
        remoteConfig.setConfigSettingsAsync(remoteConfigSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            log("RemoteConfig.Fetch: isSuccessful = ${it.isSuccessful}; result = ${it.result}")
        }

        if (remoteConfig.getBoolean("force_update")) {
            ForceUpdateDialog().show(supportFragmentManager, null)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_logout -> apiUtils.logout()
        R.id.menu_about -> navController.navigate(
            R.id.navigation_about,
            null,
            NavOptions.Builder().apply {
                setEnterAnim(R.anim.fade_in)
                setExitAnim(R.anim.fade_out)
                setPopEnterAnim(R.anim.fade_in)
                setPopExitAnim(R.anim.fade_out)
            }.build()
        ).run { true }
        else -> super.onOptionsItemSelected(item)
    }

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector
}
