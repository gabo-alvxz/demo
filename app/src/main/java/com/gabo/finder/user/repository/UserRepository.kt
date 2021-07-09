package com.gabo.finder.user.repository

import com.gabo.architecture.utils.Resource
import com.gabo.finder.common.utils.toResource
import com.gabo.network.api.UserApi
import com.gabo.models.models.seller.User
import com.gabo.network.transport.NetworkException
import javax.inject.Inject

/***
 *  Even when we dont have a users module in our app with it's own screens and ViewModels yet,
 *  we need this repository to display the seller information.
 *
 *  I've created this repo in it's own user module because it's logically belongs to user , not
 *  to the item object. If the app grows, for example, if we added a Seller screen, we wont need to move
 *  this from it's package.
 */
class UserRepository @Inject constructor(
    private val api : UserApi
){
    suspend fun getUser(userId : String) : Resource<User, NetworkException > {
        return api.getUser(userId).execute().toResource()
    }
}