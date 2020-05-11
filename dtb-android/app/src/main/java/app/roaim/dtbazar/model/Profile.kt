package app.roaim.dtbazar.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["id"])
data class Profile(
    @field:SerializedName("id")
    @ColumnInfo(name = "id")
    val id: String,
    @field:SerializedName("name")
    @ColumnInfo(name = "name")
    val name: String? = null,
    @field:SerializedName("email")
    @ColumnInfo(name = "email")
    val email: String? = null,
    @field:SerializedName("gender")
    @ColumnInfo(name = "gender")
    val gender: String? = null,
    @field:SerializedName("fbLocationName")
    @ColumnInfo(name = "fbLocationName")
    val fbLocationName: String? = null,
    @field:SerializedName("fbProfilePicture")
    @ColumnInfo(name = "fbProfilePicture")
    val fbProfilePicture: String? = null,
    @field:SerializedName("createdAt")
    @ColumnInfo(name = "createdAt")
    val createdAt: String? = null
)