package com.gabo.network.transport

interface NetworkCall<T>{
    suspend fun execute() : NetworkResponse<T>
}