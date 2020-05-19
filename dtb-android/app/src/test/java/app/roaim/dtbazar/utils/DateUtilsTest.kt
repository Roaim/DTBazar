package app.roaim.dtbazar.utils

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class DateUtilsTest {
    private val dateUtils = DateUtils

    private val dateStr = "2020-05-20T07:11:37.988"
    private val date = Calendar.getInstance().apply {
        set(Calendar.YEAR, 2020)
        set(Calendar.MONTH, 4)
        set(Calendar.DAY_OF_MONTH, 20)
        set(Calendar.HOUR, 7)
        set(Calendar.MINUTE, 11)
        set(Calendar.SECOND, 37)
        set(Calendar.MILLISECOND, 988)
    }.time

    @Test
    fun totDate_null() {
        val date = dateUtils.totDate(null)
        println(date)
        assertTrue(date.before(Date()))
    }

    @Test
    fun totDate() {
        val date = dateUtils.totDate(dateStr)
        println(date)
        assertTrue(date.after(Date()))
    }

    @Test
    fun totMillis_null() {
        val millis = dateUtils.totMillis(null)
        println(millis)
        assertTrue(millis < System.currentTimeMillis())
    }

    @Test
    fun totMillis() {
        val millis = dateUtils.totMillis(dateStr)
        println(millis)
        assertTrue(millis > System.currentTimeMillis())
    }

    @Test
    fun fromDate() {
        val dateStr = dateUtils.from(date)
        println(dateStr)
        assertEquals(this.dateStr, dateStr)
    }

    @Test
    fun testMillis() {
        val millis = date.time
        val dateStr = dateUtils.from(millis)
        println(dateStr)
        assertEquals(this.dateStr, dateStr)
    }
}