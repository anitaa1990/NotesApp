package com.an.notesapp.view.ui.viewmodel

import androidx.annotation.StringRes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

object EventManager {
    private val eventChannel = Channel<AppEvent>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun triggerEvent(event: AppEvent) {
        CoroutineScope(Dispatchers.Default).launch { eventChannel.send(event) }
    }

    fun triggerEventWithDelay(event: AppEvent, delay: Long = 100) {
        CoroutineScope(Dispatchers.Default).launch {
            delay(delay)
            triggerEvent(event)
        }
    }

    sealed class AppEvent {
        data class ShowSnackbar(@StringRes val message: Int) : AppEvent()
        data object NavigateToDetail: AppEvent()
        data object ExitScreen: AppEvent()
        data object ShowDiscardDialog: AppEvent()
    }
}