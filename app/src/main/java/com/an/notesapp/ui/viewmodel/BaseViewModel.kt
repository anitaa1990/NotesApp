package com.an.notesapp.ui.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel: ViewModel() {
    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    protected fun triggerEvent(event: Event) {
        viewModelScope.launch { eventChannel.send(event) }
    }

    protected fun triggerEventWithDelay(event: Event, delay: Long = 100) {
        viewModelScope.launch {
            delay(delay)
            triggerEvent(event)
        }
    }

    sealed class Event {
        data class ShowSnackbar(@StringRes val message: Int) : Event()
        data object ExitScreen: Event()
    }
}