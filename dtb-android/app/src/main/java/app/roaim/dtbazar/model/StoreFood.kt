package app.roaim.dtbazar.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import com.google.gson.annotations.SerializedName

@Entity(tableName = "store_food", primaryKeys = ["id"], indices = [Index("storeId")])
data class StoreFood(

    @field:SerializedName("unitPrice")
    val unitPrice: Double,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("spentDonation")
    val spentDonation: Double? = null,

    @field:SerializedName("stockQty")
    val stockQty: Double? = null,

    @field:SerializedName("totalDonation")
    val totalDonation: Double? = null,

    @field:SerializedName("storeName")
    val storeName: String? = null,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("storeId")
    val storeId: String? = null,

    @field:SerializedName("food")
    @Embedded(prefix = "food_")
    val food: Food? = null
) : ListItem {
    override fun getItemId(): String = id
}
