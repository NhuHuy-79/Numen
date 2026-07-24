package com.nhuhuy.numen.android.logger

import android.util.Log
import com.nhuhuy.numen.core.AppLogger

class AndroidLogger(private val isDebug: Boolean = true) : AppLogger {
    override fun d(message: String) {
        if (!isDebug) return
        val (tag, location) = getCallerInfo()
        Log.d(tag, "$location $message")
    }

    override fun e(throwable: Throwable) {
        if (!isDebug) return
        val (tag, location) = getCallerInfo()
        Log.e(tag, "$location Exception occurred", throwable)
    }

    override fun e(message: String) {
        if (!isDebug) return
        val (tag, location) = getCallerInfo()
        Log.e(tag, "$location $message")
    }

    override fun tagD(tag: String, message: String) {
        if (!isDebug) return
        val (_, location) = getCallerInfo()
        Log.d(tag, "$location $message")
    }

    override fun tagE(tag: String, message: String) {
        if (!isDebug) return
        val (_, location) = getCallerInfo()
        Log.e(tag, "$location $message")
    }

    override fun tagE(tag: String, throwable: Throwable) {
        if (!isDebug) return
        val (_, location) = getCallerInfo()
        Log.e(tag, "$location Exception occurred", throwable)
    }

    private fun getCallerInfo(): Pair<String, String> {
        val stackTrace = Throwable().stackTrace

        // Lọc bỏ các class Logger nội bộ để tìm vị trí nơi gọi log thực sự
        val caller = stackTrace.firstOrNull { element ->
            val className = element.className
            !className.contains("DefaultLogger") &&
                    !className.contains("AppLogger") &&
                    !className.contains("NumenLogger") &&
                    !className.contains("java.lang.Thread")
        } ?: return Pair("NUMEN", "")

        val simpleClassName = caller.className.substringAfterLast('.')
        val fileName = caller.fileName ?: "$simpleClassName.kt"

        val location = "(${fileName}:${caller.lineNumber})#${caller.methodName} ->"

        return Pair(simpleClassName, location)
    }
}