package com.gabo.finder.common.utils

import com.gabo.architecture.utils.Resource
import com.gabo.network.transport.NetworkState
import com.gabo.network.transport.NetworkException
import com.gabo.network.transport.NetworkResponse


fun <T> Resource<T, NetworkException>.toNetworkState() : NetworkState<T> {
    return when(this){
        is Resource.Success -> NetworkState.Success(this.value)
        is Resource.Error -> NetworkState.Error(this.error)
    }
}
fun <T> NetworkResponse<T>.toResource() : Resource<T, NetworkException> {
    return when(this){
        is NetworkResponse.Success -> Resource.Success(this.value)
        is NetworkResponse.Error -> Resource.Error(this.error)
    }
}