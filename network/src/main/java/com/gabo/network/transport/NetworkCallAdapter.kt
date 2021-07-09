package com.gabo.finder.network

import com.gabo.network.transport.DispatcherProvider
import com.gabo.network.transport.NetworkCall
import com.gabo.network.transport.NetworkException
import com.gabo.network.transport.NetworkResponse
import com.google.gson.Gson
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.TimeoutException

class NetworkCallAdapter<T>(
    val innerType: Type,
    val gson: Gson,
    val dispatcherProvider: DispatcherProvider
) :
    CallAdapter<ResponseBody, NetworkCall<T>> {
    override fun adapt(call: Call<ResponseBody>): NetworkCall<T> {
        return NetworkCallImpl(innerType, call, gson, dispatcherProvider)
    }

    override fun responseType(): Type {
        return ResponseBody::class.java
    }

    class Factory(
        val gson: Gson,
        val dispatcherProvider: DispatcherProvider
    ) : CallAdapter.Factory() {
        override fun get(
            returnType: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
        ): CallAdapter<*, *>? {
            if (getRawType(returnType) != NetworkCall::class.java) {
                return null
            }
            val innerType = getParameterUpperBound(0, returnType as ParameterizedType)
            return NetworkCallAdapter<Any>(innerType, gson, dispatcherProvider)
        }
    }
}

class NetworkCallImpl<T>(
    val type: Type,
    val call: Call<ResponseBody>,
    val gson: Gson,
    val dispatcherProvider: DispatcherProvider
) :
    NetworkCall<T> {
    override suspend fun execute(): NetworkResponse<T> {
        return withContext(dispatcherProvider.io) {
            try {
                val response = call.execute()
                handleError(response)
                NetworkResponse.Success(parseBody(response.body()))
            } catch (e: NetworkException) {
                NetworkResponse.Error(e)
            } catch (e: IOException) {
                NetworkResponse.Error(NetworkException(e))
            } catch (e: TimeoutException) {
                NetworkResponse.Error(NetworkException(e))
            }
        }
    }

    private fun parseBody(body: ResponseBody?): T {
        body ?: throw NetworkException()
        return try {
            gson.fromJson(body.charStream(), type)
        } catch (e: Exception) {
            throw NetworkException(e)
        }
    }

    private fun handleError(response: Response<ResponseBody>) {
        if (!response.isSuccessful) {
            throw NetworkException(response.code())
        }
    }

}