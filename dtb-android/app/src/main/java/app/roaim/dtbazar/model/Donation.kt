package app.roaim.dtbazar.model

import androidx.room.Entity
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName

@Entity(tableName = "donation", primaryKeys = ["id"])
data class Donation(

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("donorName")
    val donorName: String? = null,

    @field:SerializedName("amount")
    val amount: Int? = null,

    @field:SerializedName("foodName")
    val foodName: String? = null,

    @field:SerializedName("storeFoodId")
    val storeFoodId: String? = null,

    @field:SerializedName("currency")
    val currency: String? = null,

    @field:SerializedName("donorId")
    val donorId: String? = null,

    @field:SerializedName("storeName")
    val storeName: String? = null,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("storeId")
    val storeId: String? = null
) : ListItem {
    @Ignore
    override fun getItemId(): String = id
}
