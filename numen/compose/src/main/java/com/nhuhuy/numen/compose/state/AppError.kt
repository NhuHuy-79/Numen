package com.nhuhuy.numen.compose.state

interface AppError

sealed interface NumenError : AppError {
    data class Unknown(val throwable: Throwable? = null) : NumenError
    data class HttpError(val code: Int, val message: String) : NumenError
    data object LostConnection: NumenError
    data object TimeOut: NumenError
    data object IOError: NumenError
}