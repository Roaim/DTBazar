package app.roaim.dtbazar

import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@FixMethodOrder(MethodSorters.JVM)
class BuildConfigParamTest {
    @Test
    fun checkBuildConfigParams() {
        println("Flavor:        ${BuildConfig.FLAVOR}")
        println("App Id:        ${BuildConfig.APPLICATION_ID}")
        println("Build Type:    ${BuildConfig.BUILD_TYPE}")
        println("Version Name:  ${BuildConfig.VERSION_NAME}")
    }

    @Test
    fun checkApiParams() {
        val apiHost = BuildConfig.API_HOST
        val apiVersion = BuildConfig.API_VERSION
        val apiEndpointPrefix = BuildConfig.API_ENDPOINT_PREFIX
        val apiBaseUrl =
            "${BuildConfig.API_HOST}${BuildConfig.API_ENDPOINT_PREFIX}${BuildConfig.API_VERSION}"

        println("Api Base Url:  $apiBaseUrl")
        println("Api Host:      $apiHost")
        println("Api Version:   $apiVersion")
        println("Api Endpoint Prefix:   $apiEndpointPrefix")
    }
}
