package com.gabo.mocks.utils

import com.gabo.network.transport.NetworkCall
import com.gabo.network.transport.NetworkException
import com.gabo.network.transport.NetworkResponse

sealed class FakeNetworkCall<out T>{
    class Success<T>(val mocked : T) : FakeNetworkCall<T>(), NetworkCall<T> {
        override suspend fun execute(): NetworkResponse<T> {
            return NetworkResponse.Success(mocked)
        }
    }
    class Error<T>(val exception : NetworkException) : FakeNetworkCall<Nothing>(), NetworkCall<T>{
        override suspend fun execute(): NetworkResponse<T> {
            return NetworkResponse.Error(exception)
        }
    }
}