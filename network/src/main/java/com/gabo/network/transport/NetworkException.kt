package com.gabo.network.transport

import java.lang.Exception

class NetworkException(
    private val code: Int,
    private val errorCode : Error,
    cause : Throwable?
) : Exception(cause){
    enum class Error {
        NETWORK_ISSUE,
        NOT_FOUND,
        SERVER_ERROR,
        BAD_REQUEST,
        REDIRECT;
        companion object {
            fun fromCode(code : Int) : Error {
                return when(code) {
                    in 300 until 399 -> REDIRECT
                    in 400 until 403 -> BAD_REQUEST
                    404 -> NOT_FOUND
                    in 405 until 499 -> BAD_REQUEST
                    in 500 until 599 -> SERVER_ERROR
                    else -> NETWORK_ISSUE
                }
            }
        }
    }
    constructor() : this(
        code = 0,
        errorCode = Error.NETWORK_ISSUE,
        cause = null
    )
    constructor(throwable: Throwable)  : this(
        code = 0,
        errorCode = Error.NETWORK_ISSUE,
        cause = throwable
    )
    constructor(code : Int) : this(
        code = code,
        errorCode = Error.fromCode(code),
        cause = null
    )
}