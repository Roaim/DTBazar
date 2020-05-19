package app.roaim.dtbazar.model

import com.google.gson.annotations.SerializedName

data class ErrorBody(

	@field:SerializedName("path")
	val path: String? = null,

	@field:SerializedName("requestId")
	val requestId: String? = null,

	@field:SerializedName("error")
	val error: String? = null,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("timestamp")
	val timestamp: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
