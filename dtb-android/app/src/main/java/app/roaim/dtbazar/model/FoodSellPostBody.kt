package app.roaim.dtbazar.model

import com.google.gson.annotations.SerializedName

data class FoodSellPostBody(

    @field:SerializedName("storeFoodId")
    val storeFoodId: String,

    @field:SerializedName("qty")
    val qty: Double,

    @field:SerializedName("nid")
    val nid: String,

    @field:SerializedName("nidImage")
    val nidImage: NidImage? = null,

    @field:SerializedName("buyerName")
    val buyerName: String
)
