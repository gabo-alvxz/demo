package com.gabo.finder.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.gabo.network.transport.NetworkState
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.reflect.KClass

fun <T> LiveData<T>.getOrAwaitUntil(
    timeout: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    check: (T) -> Boolean
): T {
    val latch = CountDownLatch(1)
    val observer: Observer<T> = Observer {
        if (check(it)) {
            latch.countDown()
        }
    }
    this.observeForever(observer)
    val current = this.value
    return try {
        when {
            current != null && check(current) -> current
            !latch.await(timeout, timeUnit) -> throw TimeoutException(
                "Expected value never arrived. Latest value : $value"
            )
            else -> value!!
        }
    } finally {
        this.removeObserver(observer)
    }
}

fun <T> LiveData<NetworkState<T>>.getOrAwaitUntilState(state: KClass<out NetworkState<*>>): NetworkState<T> =
    getOrAwaitUntil { state.isInstance(it) }