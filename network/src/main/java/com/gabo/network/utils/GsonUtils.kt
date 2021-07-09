package com.gabo.network.utils

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder

fun gsonBuilder() : GsonBuilder {
    return GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .serializeNulls()
}