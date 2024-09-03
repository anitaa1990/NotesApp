package com.an.notesapp.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.an.notesapp.R
import com.an.notesapp.db.Note
import com.an.notesapp.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository
) : BaseViewModel() {
    // StateFlow is a hot flow is created using the `stateIn` method combined with
    // `SharingStarted.WhileSubscribed`. This ensures that sharing begins when the first subscriber
    // appears and stops immediately when the last subscriber disappears,
    // based on the specified `stopTimeoutMillis` parameter. As a result, the hot flows will start
    // emitting values as soon as the first subscriber appears from the UI layer, ensuring that
    // initial data is loaded only when the UI actually needs it. This approach prevents
    // unnecessary data fetching when it’s not required and stops emitting values when the last
    // subscriber disappears.
    // viewModelScope is used to launch a coroutine within the viewModel lifecycle.
    val notes: StateFlow<List<Note>> =
        repository.getNotes().distinctUntilChanged().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    /**
     * This function is used to remove a note from the database.
     * 1. viewModelScope.launch is used to launch a coroutine within the viewModel lifecycle.
     * 2. Dispatchers.IO is used to change the dispatcher of the coroutine to IO,
     * which is optimal for IO operations, and does not block the main thread.
     * 3. repository.deleteNote(note) is used to delete the note from the database.
     */
    fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) { //this: CoroutineScope
            repository.deleteNote(note)
        }
        triggerEvent(Event.ShowSnackbar(R.string.delete_note_success))
    }
}