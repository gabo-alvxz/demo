package com.gabo.finder.utils

import androidx.annotation.CallSuper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gabo.network.transport.DispatcherProviderLocator
import com.gabo.network.transport.TestDispatcherProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule

/**
 *  Base class for testing coroutines stuff.
 *  It takes care of handling
 */
@ExperimentalCoroutinesApi
abstract class CoroutinesTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    protected val testCoroutineDispatcher  = TestCoroutineDispatcher()

    @CallSuper
    @Before
    open fun setup(){
        Dispatchers.setMain(testCoroutineDispatcher)
        DispatcherProviderLocator.provider = TestDispatcherProvider(testCoroutineDispatcher)
    }

    @CallSuper
    @After
    open fun tearDown(){
        Dispatchers.resetMain()
        testCoroutineDispatcher.cleanupTestCoroutines()
    }

    protected fun launchTest(test : suspend TestCoroutineScope.() -> Unit) =
        testCoroutineDispatcher.runBlockingTest(test)

}