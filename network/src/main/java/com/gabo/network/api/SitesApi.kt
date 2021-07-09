package com.gabo.network.api

import com.gabo.models.models.Site
import com.gabo.network.transport.NetworkCall
import retrofit2.http.GET

interface SitesApi {
    @GET("sites/")
    fun getSites(): NetworkCall<List<Site>>
}