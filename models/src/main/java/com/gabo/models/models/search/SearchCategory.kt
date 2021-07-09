package com.gabo.models.models.search

import android.os.Parcelable
import com.gabo.network.models.Filter
import com.gabo.network.models.FilterValue
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchCategory(
    @SerializedName("id")
    val id : String,
    @SerializedName("name")
    val name : String
) : Parcelable