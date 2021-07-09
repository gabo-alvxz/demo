package com.gabo.network.transport

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherProvider {
    val io : CoroutineDispatcher
    val main : CoroutineDispatcher
    val unconfined : CoroutineDispatcher
    val default : CoroutineDispatcher
}

object DispatcherProviderLocator {
    var provider  : DispatcherProvider  = DefaultDispatcherProvider()
}