package com.gabo.models.models

import com.google.gson.annotations.SerializedName

data class Site(
    @SerializedName("id")
    val id : String,
    @SerializedName("name")
    val name : String
)