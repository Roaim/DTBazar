package app.roaim.dtbazar.model

import com.google.gson.annotations.SerializedName

data class Token(

	@field:SerializedName("expires")
	val expires: String? = null,

	@field:SerializedName("created")
	val created: String? = null,

	@field:SerializedName("token")
	val token: String? = null
)
