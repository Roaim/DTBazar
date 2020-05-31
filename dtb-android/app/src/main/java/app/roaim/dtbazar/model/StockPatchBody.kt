package app.roaim.dtbazar.model

import com.google.gson.annotations.SerializedName

data class StockPatchBody(

    @field:SerializedName("unitPrice")
    val unitPrice: Double,

    @field:SerializedName("quantity")
    val quantity: Double
)
