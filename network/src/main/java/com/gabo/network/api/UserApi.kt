package com.gabo.network.api

import com.gabo.models.models.seller.User
import com.gabo.network.transport.NetworkCall
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {
    @GET("users/{userId}")
    fun getUser(
        @Path("userId") userId : String
    ) : NetworkCall<User>
}