package com.gabo.network.transport

import kotlinx.coroutines.CoroutineDispatcher

class TestDispatcherProvider(
    val testDispatcher : CoroutineDispatcher
) : DispatcherProvider  {
    override val io: CoroutineDispatcher
        get() = testDispatcher
    override val main: CoroutineDispatcher
        get() = testDispatcher
    override val unconfined: CoroutineDispatcher
        get() = testDispatcher
    override val default: CoroutineDispatcher
        get() = testDispatcher
}