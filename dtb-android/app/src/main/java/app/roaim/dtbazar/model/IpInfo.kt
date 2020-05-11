package app.roaim.dtbazar.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["ip"])
data class IpInfo(

    @field:SerializedName("zip")
    @ColumnInfo(name = "zip")
    val zip: String? = null,

    @ColumnInfo(name = "country")
    @field:SerializedName("country")
    val country: String? = null,

    @ColumnInfo(name = "city")
    @field:SerializedName("city")
    val city: String? = null,

    @ColumnInfo(name = "org")
    @field:SerializedName("org")
    val org: String? = null,

    @ColumnInfo(name = "timezone")
    @field:SerializedName("timezone")
    val timezone: String? = null,

    @ColumnInfo(name = "regionName")
    @field:SerializedName("regionName")
    val regionName: String? = null,

    @ColumnInfo(name = "isp")
    @field:SerializedName("isp")
    val isp: String? = null,

    @ColumnInfo(name = "ip")
    @field:SerializedName("query")
    val ip: String,

    @ColumnInfo(name = "lon")
    @field:SerializedName("lon")
    val lon: Double? = null,

    @ColumnInfo(name = "as")
    @field:SerializedName("as")
    val mAs: String? = null,

    @ColumnInfo(name = "countryCode")
    @field:SerializedName("countryCode")
    val countryCode: String? = null,

    @ColumnInfo(name = "region")
    @field:SerializedName("region")
    val region: String? = null,

    @ColumnInfo(name = "lat")
    @field:SerializedName("lat")
    val lat: Double? = null,

    @ColumnInfo(name = "status")
    @field:SerializedName("status")
    val status: String? = null
)
