package com.gabo.finder.item.repository

import com.gabo.architecture.utils.Resource
import com.gabo.finder.common.utils.toResource
import com.gabo.network.api.ItemsApi
import com.gabo.models.models.item.Item
import com.gabo.network.transport.NetworkException
import javax.inject.Inject

class ItemRepository @Inject constructor(
    private val api : ItemsApi
) {
    suspend fun getItem(itemId : String ) : Resource<Item, NetworkException>{
        return api.getItem(itemId).execute().toResource()
    }
    suspend fun getItemDescription(itemId : String) : Resource<String, NetworkException> {
        return api.getDescription(itemId).execute().toResource().map {
            it.text
        }
    }
}