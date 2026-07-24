package com.nhuhuy.numen.core


object NumenLogger {
    var logger: AppLogger? = null
}

interface AppLogger {
    fun d(message: String)
    fun e(throwable: Throwable)
    fun e(message: String)
    fun tagD(tag: String, message: String)
    fun tagE(tag: String, message: String)
    fun tagE(tag: String, throwable: Throwable)
}