package app.roaim.dtbazar.model

import androidx.room.Entity
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName

@Entity(tableName = "food", primaryKeys = ["id"])
data class Food(

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("unit")
    val unit: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("currency")
    val currency: String? = null,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("startingPrice")
    val startingPrice: Double? = null,
    @field:SerializedName("endingPrice")
    val endingPrice: Double? = null
) : ListItem {
    @Ignore
    override fun getItemId(): String = id

    @Ignore
    fun priceRange() = "$currency $startingPrice - $endingPrice / $unit"
}

data class FoodPostBody(

    @field:SerializedName("unit")
    val unit: String = "KG",

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("currency")
    val currency: String = "BDT",

    @field:SerializedName("startingPrice")
    val startingPrice: Double,
    @field:SerializedName("endingPrice")
    val endingPrice: Double
)
