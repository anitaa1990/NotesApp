package com.an.notesapp.view.ui.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.an.notesapp.R
import com.an.notesapp.intent.NoteIntent
import com.an.notesapp.model.db.Note
import com.an.notesapp.model.repository.NoteRepository
import com.an.notesapp.view.ui.viewmodel.EventManager.AppEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {
    private val _notesViewState = MutableStateFlow(NotesViewState())
    val notesViewState: StateFlow<NotesViewState> = _notesViewState

    init {
        handleIntent(NoteIntent.LoadNotes)
    }

    fun handleIntent(intent: NoteIntent) {
        when(intent) {
            is NoteIntent.LoadNotes -> loadNotes()
            is NoteIntent.DeleteNote -> deleteNote(intent.note)
            is NoteIntent.OpenNoteClicked -> onNoteClicked(intent.note)
            is NoteIntent.ValidatePassword -> validatePassword(intent.password)
            else -> {  }
        }
    }

    private fun loadNotes() {
        _notesViewState.value = _notesViewState.value.copy(isLoading = true)
        viewModelScope.launch(IO) {
            repository.getNotes().collect { notes ->
                _notesViewState.value = _notesViewState.value.copy(isLoading = false, notes = notes)
            }
        }
    }

    private fun deleteNote(note: Note) {
        viewModelScope.launch(IO) {
            repository.deleteNote(note)
            EventManager.triggerEvent(AppEvent.ShowSnackbar(R.string.delete_note_success))
        }
    }

    private fun onNoteClicked(note: Note) {
        if (note.encrypt) {
            _notesViewState.value = _notesViewState.value.copy(
                selectedNote = note,
                showPasswordSheet = true
            )
        } else {
            EventManager.triggerEvent(AppEvent.NavigateToDetail(note.id))
        }
    }

    private fun validatePassword(password: String) {
        _notesViewState.value.selectedNote?.let { note ->
            if (password != note.password) {
                _notesViewState.value = _notesViewState.value.copy(
                    passwordErrorResId = R.string.error_password
                )
            } else {
                _notesViewState.value = _notesViewState.value.copy(showPasswordSheet = false)
                EventManager.triggerEvent(AppEvent.NavigateToDetail(note.id))
            }
        }
    }

    data class NotesViewState(
        val isLoading: Boolean = false,
        val notes: List<Note> = emptyList(),
        val selectedNote: Note? = null,
        val showPasswordSheet: Boolean = false,
        @StringRes val passwordErrorResId: Int? = null
    )
}