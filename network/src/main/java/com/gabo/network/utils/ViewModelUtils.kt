package com.gabo.architecture.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabo.network.transport.DispatcherProviderLocator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 *  This extension  allows us to launch coroutines from within viewModels without having to inject
 *  the coroutine dispatcher on every single ViewModel by constructor, which is a pain.
 *
 */
fun ViewModel.launch(
    dispatcher : CoroutineDispatcher = DispatcherProviderLocator.provider.main,
    block : suspend CoroutineScope.() -> Unit,
) : Job {
    return  viewModelScope.launch(context = dispatcher, block = block)
}