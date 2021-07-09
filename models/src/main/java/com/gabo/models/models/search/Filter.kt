package com.gabo.network.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Filter(
    @SerializedName("name")
    val name : String,
    @SerializedName("id")
    val id : String,
    @SerializedName("type")
    val type : Type?,
    @SerializedName("values")
    val values : List<FilterValue>
) : Parcelable {
    /***
     * We could use this in the future to render diferent rows for different filters.
     */
    enum class Type {
        @SerializedName("STRING")
        STRING,
        @SerializedName("text")
        TEXT,
        @SerializedName("range")
        RANGE,
        @SerializedName("boolean")
        BOOLEAN,
        @SerializedName("number")
        NUMBER
    }
}

@Parcelize
data class FilterValue(
    @SerializedName("id")
    val id : String,
    @SerializedName("name")
    val name : String,
    @SerializedName("results")
    val resultsQty : Int
) : Parcelable

fun Map<Filter, FilterValue>.toQueryMap()  : Map<String, String>{
    val entries = this.entries
    return mutableMapOf<String, String>().apply{
        entries.forEach{ entry ->
            this[entry.key.id] = entry.value.id
        }
    }
}