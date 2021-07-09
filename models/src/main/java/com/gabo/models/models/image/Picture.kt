package com.gabo.models.models.image

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Picture(
    @SerializedName("id")
    val id  : String,
    @SerializedName("url")
    val plainUrl : String,
    @SerializedName("secure_url")
    val secureUrl : String?,
    @SerializedName("size")
    val sizeString : String
) : Parcelable {
    val url
        get() = secureUrl ?: plainUrl

    val size :  Pair<Int, Int>
        get(){
            val components = sizeString.split("x")
            return Pair(components[0].toInt(), components[1].toInt())
        }
    val ratio : Double
        get() {
            val (width, height) = size
            return width.toDouble() / height.toDouble()
        }
}