package com.an.notesapp.view.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.an.notesapp.intent.NoteIntent
import com.an.notesapp.model.db.Note
import com.an.notesapp.model.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.an.notesapp.R
import java.time.OffsetDateTime

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository
) : BaseViewModel() {
    private val _notesViewState = MutableStateFlow(NotesViewState())
    val notesViewState: StateFlow<NotesViewState> = _notesViewState

    init {
        handleIntent(NoteIntent.LoadNotes)
    }

    fun handleIntent(intent: NoteIntent) {
        when(intent) {
            is NoteIntent.LoadNotes -> loadNotes()
            is NoteIntent.DeleteNote -> deleteNote(intent.note)
            is NoteIntent.AddNote -> addNote(intent.title, intent.description)
            is NoteIntent.UpdateNote -> updateNote(intent.note)
        }
    }

    private fun loadNotes() {
        _notesViewState.value = _notesViewState.value.copy(isLoading = true)
        viewModelScope.launch(IO) {
            val notes = repository.getNotes()
            _notesViewState.value = _notesViewState.value.copy(isLoading = false, notes = notes)
        }
    }

    private fun deleteNote(note: Note) {
        viewModelScope.launch(IO) { //this: CoroutineScope
            repository.deleteNote(note)
            triggerEvent(AppEvent.ShowSnackbar(R.string.delete_note_success))
        }
    }

    private fun addNote(title: String, description: String) {
        viewModelScope.launch(IO) {
            val note = Note(
                title = title,
                description = description,
                modifiedAt = OffsetDateTime.now()
            )
            repository.insertNote(note)
            triggerEvent(AppEvent.ShowSnackbar(R.string.add_note_success))
        }
    }

    private fun updateNote(note: Note) {
        viewModelScope.launch(IO) {
            val updatedNote = note.copy(modifiedAt = OffsetDateTime.now())
            repository.updateNote(updatedNote)
            triggerEvent(AppEvent.ShowSnackbar(R.string.update_note_success))
        }
    }

    data class NotesViewState(
        val isLoading: Boolean = false,
        val notes: List<Note> = emptyList(),
        val errorMessage: String = ""
    )
}