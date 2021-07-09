package com.gabo.network.api

import com.gabo.models.models.item.Item
import com.gabo.network.transport.NetworkCall
import retrofit2.http.GET
import retrofit2.http.Path

interface ItemsApi {
    @GET("items/{itemId}/")
    fun getItem(@Path("itemId") itemId : String) : NetworkCall<Item>

    @GET("items/{itemId}/description")
    fun getDescription(@Path("itemId") itemId: String) : NetworkCall<Item.Description>
}