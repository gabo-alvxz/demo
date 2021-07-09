package com.gabo.network.api

import com.gabo.models.models.search.SearchCategory
import com.gabo.network.transport.NetworkCall
import retrofit2.http.GET
import retrofit2.http.Path

interface CategoriesApi {
    @GET("sites/{site}/categories")
    fun getCategories(
        @Path("site") site : String
    ) : NetworkCall<List<SearchCategory>>
}