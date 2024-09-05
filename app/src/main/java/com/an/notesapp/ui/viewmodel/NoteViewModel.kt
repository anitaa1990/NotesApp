package com.an.notesapp.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.an.notesapp.R
import com.an.notesapp.db.Note
import com.an.notesapp.repository.NoteRepository
import com.an.notesapp.ui.model.NoteAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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

    private val _noteActionState = MutableStateFlow<NoteAction>(NoteAction.Idle)
    val noteActionState = _noteActionState.asStateFlow()

    private var selectedNote: Note? = null
    fun onNoteItemClicked(note: Note) {
        this.selectedNote = note
        if (note.encrypt) {
            // open bottom sheet to allow users to enter password
            _noteActionState.update { NoteAction.NoteLocked }
        } else {
            // redirect to note detail screen since the note is not locked
            _noteActionState.update { NoteAction.NoteNotLocked(note.id) }
        }
    }

    fun validatePassword(password: String) {
        if (selectedNote?.password == password) {
            // password is correct so redirect to note detail screen
            selectedNote?.let { note ->
                _noteActionState.update { NoteAction.PasswordValidationSuccess(note.id) }
            }
        } else {
            // password is wrong so display validation error
            _noteActionState.update { NoteAction.PasswordValidationError(R.string.error_password) }
        }
    }

    fun reset() {
        _noteActionState.update { NoteAction.Idle }
    }
}