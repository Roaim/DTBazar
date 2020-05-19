package app.roaim.dtbazar.model

import com.google.gson.annotations.SerializedName

data class DonationPostBody(

    @field:SerializedName("amount")
    val amount: Double,

    @field:SerializedName("storeFoodId")
    val storeFoodId: String,

    @field:SerializedName("currency")
    val currency: String
)
