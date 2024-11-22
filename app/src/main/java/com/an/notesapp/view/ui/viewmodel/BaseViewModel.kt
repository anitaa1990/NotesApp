package com.an.notesapp.view.ui.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel: ViewModel() {
    private val eventChannel = Channel<AppEvent>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    protected fun triggerEvent(event: AppEvent) {
        viewModelScope.launch { eventChannel.send(event) }
    }

    protected fun triggerEventWithDelay(event: AppEvent, delay: Long = 100) {
        viewModelScope.launch {
            delay(delay)
            triggerEvent(event)
        }
    }

    sealed class AppEvent {
        data class ShowSnackbar(@StringRes val message: Int) : AppEvent()
        data object ExitScreen: AppEvent()
        data object ShowDiscardDialog: AppEvent()
    }
}