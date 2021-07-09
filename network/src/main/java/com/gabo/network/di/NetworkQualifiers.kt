package com.gabo.network.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ExternalHttpClient


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MeliRetrofitClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultGson

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TestDispatcher
