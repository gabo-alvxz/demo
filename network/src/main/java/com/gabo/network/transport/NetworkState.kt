package com.gabo.network.transport

sealed class NetworkState<out T> {
    object Loading : NetworkState<Nothing>()
    class Success<T>(val value : T) : NetworkState<T>()
    class Error(val error: NetworkException) : NetworkState<Nothing>()
}