package com.gabo.network.di

import com.gabo.network.api.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class ServicesModule {

    @Provides
    fun provideSearchApi(
        @MeliRetrofitClient retrofitClient: Retrofit
    ) : SearchApi {
        return retrofitClient.create(SearchApi::class.java)
    }

    @Provides
    fun provideCategoriesApi(
        @MeliRetrofitClient retrofitClient: Retrofit
    ) : CategoriesApi {
        return retrofitClient.create(CategoriesApi::class.java)
    }

    @Provides
    fun provideSitesApi(
        @MeliRetrofitClient retrofitClient: Retrofit
    ) : SitesApi {
        return retrofitClient.create(SitesApi::class.java)
    }

    @Provides
    fun providesItemsApi(
        @MeliRetrofitClient retrofitClient: Retrofit
    ) : ItemsApi {
        return retrofitClient.create(ItemsApi::class.java)
    }

    @Provides
    fun providesUserApi(
        @MeliRetrofitClient retrofitClient: Retrofit
    ) : UserApi {
        return retrofitClient.create(UserApi::class.java)
    }
}