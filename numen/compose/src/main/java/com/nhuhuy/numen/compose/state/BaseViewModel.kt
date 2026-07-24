package com.nhuhuy.numen.compose.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface UiState

interface UiAction

interface UiEvent

abstract class BaseViewModel<STATE: UiState, ACTION : UiAction, EVENT : UiEvent> : ViewModel(){
    abstract val state: StateFlow<STATE>
    protected val currentState get() = state.value

    abstract fun onAction(action: ACTION)

    private val _event = Channel<EVENT>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    protected fun emitEvent(event: EVENT) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}


inline fun <T> MutableStateFlow<T>.reduce(
    reducer: T.() -> T
) {
    update {  state ->
        state.reducer()
    }
}