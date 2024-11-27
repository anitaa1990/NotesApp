package com.an.notesapp.view.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: NoteRepository
) : ViewModel() {
    // "noteId" is nullable because noteId can be null when adding a new note
    private val noteId: String? = savedStateHandle["noteId"]

    // This is a mutable state flow that will be used internally in the viewmodel,
    private val _noteDetailViewState = MutableStateFlow(
        NoteDetailViewState(showDeleteIcon = noteId != null)
    )

    // Immutable state flow that is exposed to the UI
    val noteDetailViewState = _noteDetailViewState.asStateFlow()

    init {
        handleIntent(NoteIntent.LoadNote)
    }

    fun handleIntent(intent: NoteIntent) {
        when(intent) {
            is NoteIntent.LoadNote -> loadNote()
            is NoteIntent.UpdateNoteTitle -> updateNoteTitle(intent.title)
            is NoteIntent.UpdateNoteDescription -> updateNoteDesc(intent.description)
            is NoteIntent.AddOrSaveNote -> noteId?.let { saveNote() } ?: addNote()
            is NoteIntent.DeleteNote -> deleteNote()
            else -> {  }
        }
    }

    private fun loadNote() = viewModelScope.launch {
        noteId?.let {
            viewModelScope.launch(IO) {
                _noteDetailViewState.value = _noteDetailViewState.value.copy(
                    note = repository.getNote(it.toLong())
                )
            }
        }
    }

    private fun updateNoteTitle(title: String) {
        val note = _noteDetailViewState.value.note.copy(title = title)
        _noteDetailViewState.value = _noteDetailViewState.value.copy(
            showSaveIcon = true, note = note
        )
    }

    private fun updateNoteDesc(desc: String) {
        val note = _noteDetailViewState.value.note.copy(description = desc)
        _noteDetailViewState.value = _noteDetailViewState.value.copy(
            showSaveIcon = true, note = note
        )
    }

    fun addNote() {
        viewModelScope.launch(IO) {
            val note = _noteDetailViewState.value.note.copy(
                createdAt = OffsetDateTime.now(),
                modifiedAt = OffsetDateTime.now()
            )
            repository.insertNote(note)
        }
        EventManager.triggerEvent(AppEvent.ShowSnackbar(R.string.add_note_success))
        EventManager.triggerEventWithDelay(AppEvent.ExitScreen)
    }

    fun saveNote() {
        viewModelScope.launch(IO) {
            val updatedNote = _noteDetailViewState.value.note.copy(
                modifiedAt = OffsetDateTime.now()
            )
            repository.updateNote(updatedNote)
        }
        EventManager.triggerEvent(AppEvent.ShowSnackbar(R.string.update_note_success))
        EventManager.triggerEventWithDelay(AppEvent.ExitScreen)
    }

    private fun deleteNote() {
        viewModelScope.launch(IO) {
            val note = _noteDetailViewState.value.note
            repository.deleteNote(note)
        }
        EventManager.triggerEvent(AppEvent.ShowSnackbar(R.string.delete_note_success))
        EventManager.triggerEventWithDelay(AppEvent.ExitScreen)
    }

    data class NoteDetailViewState(
        val note: Note = Note(title = "", description = "", modifiedAt = OffsetDateTime.now()),
        val showDeleteIcon: Boolean,
        val showSaveIcon: Boolean = false
    )
}