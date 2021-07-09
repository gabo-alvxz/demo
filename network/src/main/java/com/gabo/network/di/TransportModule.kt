package com.gabo.network.di

import com.gabo.network.transport.BaseUrls
import com.gabo.network.transport.DefaultDispatcherProvider
import com.gabo.network.transport.DispatcherProvider
import com.gabo.network.utils.baseHttpClient
import com.gabo.network.utils.gsonBuilder
import com.gabo.network.utils.retrofitBuilder
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class TransportModule {

    @Provides
    @DefaultGson
    fun provideDefaultGson() : Gson {
        return gsonBuilder().create()
    }

    @Provides
    @ExternalHttpClient
    fun provideExternalHttpClient() : OkHttpClient {
        return baseHttpClient()
            .followRedirects(false)
            .build()
    }

    @Provides
    @MeliRetrofitClient
    fun provideQuotesRetrofitClient(
        @ExternalHttpClient httpClient: OkHttpClient,
        @DefaultGson gson: Gson,
        dispatcherProvider: DispatcherProvider
    ) : Retrofit {
        return retrofitBuilder(gson, dispatcherProvider)
            .baseUrl(BaseUrls.MELI_API_PROD.url)
            .client(httpClient)
            .build()
    }

    @Provides
    fun provideDispatcherProvider() : DispatcherProvider{
        return DefaultDispatcherProvider()
    }

}