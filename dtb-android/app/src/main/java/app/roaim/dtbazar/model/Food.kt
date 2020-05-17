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
    val id: String
) : ListItem {
    @Ignore
    override fun getItemId(): String = id
}

data class FoodPostBody(

    @field:SerializedName("unit")
    val unit: String? = "KG",

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("currency")
    val currency: String = "BDT"
)
