package app.roaim.dtbazar.db

import androidx.room.TypeConverter

class LocationTypeConverter {
    @TypeConverter
    fun fromString(value: String?): List<Double>? = value?.split(":")?.map { it.toDouble() }

    @TypeConverter
    fun fromList(list: List<Double>?): String? = list?.let { "${it[0]}:${it[1]}" }
}