package com.nhuhuy.numen.compose.state

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nhuhuy.numen.core.AppResult


@Stable
sealed interface UiResult<out T> {
    data object Idle: UiResult<Nothing>
    data class Success<out T>(val data: T) : UiResult<T>
    data class Failure(val error: AppError? = null) : UiResult<Nothing>
    data object Loading : UiResult<Nothing>

    val isSuccess : Boolean get() = this is Success
    val isFailure : Boolean get() = this is Failure
    val isIdle get() : Boolean = this is Idle
    val isLoading get() : Boolean = this is Loading

    fun getDataOrNull(): T? {
        return when (this) {
            is Success -> data
            else -> null
        }
    }
}

// ====== Mapper =====
fun <T> AppResult<T>.toUiResult(
    error: AppError? = null,
): UiResult<T>{
    return when (this) {
        is AppResult.Failure -> UiResult.Failure(error)
        is AppResult.Success -> UiResult.Success(data)
    }
}

inline fun <T, R> UiResult<T>.map(
    transform: (T) -> R
): UiResult<R>{
    return when (this) {
        is UiResult.Success -> UiResult.Success(transform(data))
        is UiResult.Failure -> this
        is UiResult.Loading -> this
        is UiResult.Idle -> this
    }
}

// ====== COMPOSABLE ======
@Composable
fun <T>AppResultContainer(
    result: UiResult<T>,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    idle: @Composable () -> Unit = {},
    success: @Composable (T) -> Unit,
    loading: @Composable () -> Unit = {},
    error: @Composable (AppError?) -> Unit,
    enterAnimation: EnterTransition = fadeIn(),
    exitAnimation: ExitTransition = fadeOut(),
){
    AnimatedContent(
        modifier = modifier,
        targetState = result,
        label = "AppResultContainer",
        contentKey = { state -> state::class },
        contentAlignment = alignment,
        transitionSpec = { enterAnimation togetherWith exitAnimation }
    ){ state ->
        when (state) {
            is UiResult.Idle -> idle()
            is UiResult.Success -> success(state.data)
            is UiResult.Failure -> error(state.error)
            is UiResult.Loading -> loading()
        }
    }
}

// ====== SIDE EFFECT ======
inline fun <T> UiResult<T>.onSuccess(
    block: (T) -> Unit
): UiResult<T> {
    if (this is UiResult.Success) {
        block(data)
    }
    return  this
}

inline fun <T> UiResult<T>.onFailure(
    block: (AppError?) -> Unit
): UiResult<T> {
    if (this is UiResult.Failure) {
        block(error)
    }
    return  this
}