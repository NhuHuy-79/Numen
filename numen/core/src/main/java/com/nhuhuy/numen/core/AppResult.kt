package com.nhuhuy.numen.core

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout


sealed interface AppResult<out T> {
    data class Success<out T>(val data: T) : AppResult<T>
    data class Failure(val throwable: Throwable) : AppResult<Nothing>
}

val <T> AppResult<T>.isSuccess get() = this is AppResult.Success
val <T> AppResult<T>.isFailure get() = this is AppResult.Failure

// ====== CREATE ======
suspend inline fun <T> safeCall(
    dispatcher: DispatcherProvider = AppDispatcherProvider,
    crossinline finally: () -> Unit = {},
    crossinline block: suspend () -> T,
): AppResult<T> {
    return withContext(dispatcher.io) {
        try {
            val data = block()
            AppResult.Success(data)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            NumenLogger.logger?.e(e)
            AppResult.Failure(e)
        } finally {
            finally()
        }
    }
}

suspend inline fun <T> safeCallWithTimeout(
    timeout: Long = 1_000L,
    dispatcher: DispatcherProvider = AppDispatcherProvider,
    crossinline finally: () -> Unit,
    crossinline block: suspend () -> T,

    ): AppResult<T> {
    return withTimeout(timeout) {
        withContext(dispatcher.io) {
            try {
                val data = block()
                AppResult.Success(data)
            } catch (e: TimeoutCancellationException) {
                throw e
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                NumenLogger.logger?.e(e)
                AppResult.Failure(e)
            } finally {
                finally()
            }
        }
    }
}

// ====== SIDE EFFECT =======
inline fun <T> AppResult<T>.onSuccess(block: (T) -> Unit): AppResult<T> {
    when (this) {
        is AppResult.Success -> block(data)
        else -> Unit
    }

    return this
}


suspend inline fun <T> AppResult<T>.onSuccessSuspend(block: suspend (T) -> Unit): AppResult<T> {
    if (this is AppResult.Success) {
        block(data)
    }

    return this
}

inline fun <T> AppResult<T>.onFailure(block: (Throwable) -> Unit): AppResult<T> {
    if (this is AppResult.Failure) {
        block(throwable)
    }

    return this
}

suspend inline fun <T> AppResult<T>.onFailureSuspend(
    block: suspend (Throwable) -> Unit): AppResult<T> {
    if (this is AppResult.Failure) {
        block(throwable)
    }
    return this
}


// ====== GET DATA ======
fun <T> AppResult<T>.getDataOrNull(): T? {
    return when (this) {
        is AppResult.Success -> data
        else -> null
    }
}

fun <T> AppResult<T>.getDataOrFallback(fallback: T): T {
    return when (this) {
        is AppResult.Success -> data
        else -> fallback
    }
}

// ====== TRANSFORM ======
inline fun <T, R> AppResult<T>.fold(
    onSuccess: (T) -> R,
    onFailure: (Throwable) -> R
): R {
    return when (this) {
        is AppResult.Success -> onSuccess(data)
        is AppResult.Failure -> onFailure(throwable)
    }
}

inline fun <T, R> AppResult<T>.map(
    transform: (T) -> R
): AppResult<R> {
    return when (this) {
        is AppResult.Success -> AppResult.Success(transform(data))
        is AppResult.Failure -> this
    }
}



