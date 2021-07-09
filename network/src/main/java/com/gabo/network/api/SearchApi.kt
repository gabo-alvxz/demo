package com.gabo.network.api


import com.gabo.models.models.item.Item
import com.gabo.models.models.search.SearchCategory
import com.gabo.models.models.search.SearchPage
import com.gabo.network.transport.NetworkCall
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface SearchApi{
    @GET("sites/{site}/search/")
    fun search(
        @Path("site") site : String,
        @Query("q") query : String? = null,
        @Query("category") category : String? = null,
        @Query("sort") sort : String? = null,
        @QueryMap pagination : Map<String , String> = hashMapOf(),
        @QueryMap filters : Map<String, String> = hashMapOf()
    ) : NetworkCall<SearchPage<Item>>

    @GET("sites/{site}/categories")
    fun getCategories(
        @Path("site") site : String
    ) : NetworkCall<List<SearchCategory>>

}