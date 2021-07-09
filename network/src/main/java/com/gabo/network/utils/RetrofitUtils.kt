package com.gabo.network.utils

import com.gabo.finder.network.NetworkCallAdapter
import com.gabo.network.BuildConfig
import com.gabo.network.transport.DispatcherProvider
import com.gabo.network.transport.ThreadSafetyCheckInterceptor
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun baseHttpClient() : OkHttpClient.Builder{
    return OkHttpClient.Builder()
        .apply {
            if(BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                addInterceptor(ThreadSafetyCheckInterceptor())
            }
        }
}

fun retrofitBuilder(gson: Gson, dispatcherProvider: DispatcherProvider) : Retrofit.Builder{
    return Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(NetworkCallAdapter.Factory(gson, dispatcherProvider))
}