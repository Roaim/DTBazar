package app.roaim.dtbazar.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun totDate(dateStr: String?): Date = dateStr?.let { getDateFormat().parse(it) } ?: Date(0L)

    fun totMillis(dateStr: String?): Long = totDate(dateStr).time

    fun from(date: Date): String = getDateFormat().format(date)

    fun from(millis: Long) = from(Date(millis))

    private fun getDateFormat() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
}