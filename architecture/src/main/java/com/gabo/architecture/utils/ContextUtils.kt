package com.gabo.architecture.utils

import android.content.Context
import android.content.ContextWrapper

/**
 *   Navigates a context through it's base contexts until one of two conditions are true:
 *   Either the base context is null or is an instance of the class we are looking for.
 *   @return an instance of T if found in the baseContext graph or @null if it's not
 */
inline fun <reified T> Context.fromContextWrapper(): T? {
    var wrapper: Context? = this
    while (wrapper != null && wrapper !is T) {
        wrapper = (wrapper as? ContextWrapper?)?.baseContext
    }
    return wrapper as? T?
}


