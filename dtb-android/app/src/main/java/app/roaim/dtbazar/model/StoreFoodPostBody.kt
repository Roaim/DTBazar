package app.roaim.dtbazar.model

import com.google.gson.annotations.SerializedName

data class StoreFoodPostBody(

	@field:SerializedName("unitPrice")
	val unitPrice: Double,

	@field:SerializedName("foodId")
	val foodId: String,

	@field:SerializedName("stockQty")
	val stockQty: Double
)
