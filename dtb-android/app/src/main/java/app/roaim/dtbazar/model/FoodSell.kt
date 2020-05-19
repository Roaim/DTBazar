package app.roaim.dtbazar.model

import com.google.gson.annotations.SerializedName

data class FoodSell(

	@field:SerializedName("unitPrice")
	val unitPrice: Int? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("foodName")
	val foodName: String? = null,

	@field:SerializedName("storeFoodId")
	val storeFoodId: String? = null,

	@field:SerializedName("foodId")
	val foodId: String? = null,

	@field:SerializedName("qty")
	val qty: Int? = null,

	@field:SerializedName("nid")
	val nid: String? = null,

	@field:SerializedName("nidImage")
	val nidImage: NidImage? = null,

	@field:SerializedName("storeName")
	val storeName: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("buyerName")
	val buyerName: String? = null,

	@field:SerializedName("storeId")
	val storeId: String? = null
)

data class NidImage(

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("source")
	val source: String? = null,

	@field:SerializedName("url")
	val url: String? = null
)
