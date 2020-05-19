package app.roaim.dtbazar.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import com.google.gson.annotations.SerializedName

@Entity(tableName = "store", primaryKeys = ["id"], indices = [Index("uid")])
data class Store(

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("uid")
    val uid: String,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("spentDonation")
    val spentDonation: Double? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("mobile")
    val mobile: String? = null,

    @field:SerializedName("totalDonation")
    val totalDonation: Double? = null,

    @field:SerializedName("location")
    val location: List<Double?>? = null,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("proprietor")
    val proprietor: String? = null,

    @Embedded(prefix = "sfi_")
    @field:SerializedName("storeFrontImage")
    val storeFrontImage: StoreFrontImage? = null,

    @field:SerializedName("allFoodPrice")
    val allFoodPrice: Double? = null
) : ListItem {
    @Ignore
    override fun getItemId(): String = id

    @Ignore
    fun totalDonationFloat() = totalDonation?.toFloat()

    @Ignore
    fun spentDonationFloat() = spentDonation?.toFloat()
}

data class StoreFrontImage(

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("source")
    val source: String? = null,

    @field:SerializedName("url")
    val url: String? = null
)

data class StorePostBody(

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("mobile")
    val mobile: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("storeFrontImage")
    val storeFrontImage: StoreFrontImage? = null,

    @field:SerializedName("location")
    val location: List<Double?>? = null
)
