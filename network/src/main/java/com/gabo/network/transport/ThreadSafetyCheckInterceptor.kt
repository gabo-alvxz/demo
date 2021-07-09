package com.gabo.network.transport

import android.os.Looper
import com.gabo.network.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.lang.IllegalStateException

/**
 *  This interceptor will make our app to crash if we are calling the net  in the ui thread so we don't do it.
 *  It must only be injected for debuggable variants so it only explodes in development , not in prod.
 */
class ThreadSafetyCheckInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if(BuildConfig.DEBUG && checkIfMainThread()){
            val url =  chain.request().url
            throw IllegalStateException("Network call on the main thread for url : $url")
        }
        return chain.proceed(chain.request())
    }

    private fun checkIfMainThread() : Boolean {
        return Looper.getMainLooper().thread == Thread.currentThread()
    }
}