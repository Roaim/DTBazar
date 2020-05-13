package app.roaim.dtbazar.api

import app.roaim.dtbazar.model.ErrorBody
import app.roaim.dtbazar.model.Status
import org.junit.Assert.*
import org.junit.Test

class ApiUtilsTest {

    @Test
    fun getBadRequestResult_JWT_logout() {
        val msg =
            "JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted."
        val errorBody = ErrorBody(message = msg)
        val badRequestResult = ApiUtils.getBadRequestResult(errorBody, String::class.java)
        assertNotNull(badRequestResult)
        assertEquals(badRequestResult!!.status, Status.LOGOUT)
    }

    @Test
    fun getBadRequestResult_jwt_logout() {
        val msg =
            "jwt expired at 2020-05-10T03:14:08Z. Current time: 2020-05-13T06:04:42Z, a difference of 269434411 milliseconds.  Allowed clock skew: 0 milliseconds."
        val errorBody = ErrorBody(message = msg)
        val badRequestResult = ApiUtils.getBadRequestResult(errorBody, String::class.java)
        assertNotNull(badRequestResult)
        assertEquals(badRequestResult!!.status, Status.LOGOUT)
    }

    @Test
    fun getBadRequestResult_Authorization_logout() {
        val msg =
            "Missing request header 'Authorization' for method parameter of type String"
        val errorBody = ErrorBody(message = msg)
        val badRequestResult = ApiUtils.getBadRequestResult(errorBody, String::class.java)
        assertNotNull(badRequestResult)
        assertEquals(badRequestResult!!.status, Status.LOGOUT)
    }

    @Test
    fun getBadRequestResult_authorization_logout() {
        val msg =
            "authorization header must start with Bearer"
        val errorBody = ErrorBody(message = msg)
        val badRequestResult = ApiUtils.getBadRequestResult(errorBody, String::class.java)
        println(badRequestResult)
        assertNotNull(badRequestResult)
        assertEquals(badRequestResult!!.status, Status.LOGOUT)
    }

    @Test
    fun getBadRequestResult_null() {
        val msg = "Bad Request"
        val errorBody = ErrorBody(message = msg)
        val badRequestResult = ApiUtils.getBadRequestResult(errorBody, String::class.java)
        println(badRequestResult)
        assertNull(badRequestResult)
    }
}