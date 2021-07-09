package com.gabo.network.transport

sealed class NetworkResponse<out T> {
    class Success<T>(val value : T) : NetworkResponse<T>()
    class Error(val error : NetworkException) : NetworkResponse<Nothing>()
}