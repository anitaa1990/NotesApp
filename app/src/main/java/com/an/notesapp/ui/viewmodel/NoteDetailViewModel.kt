package com.an.notesapp.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.an.notesapp.db.Note
import com.an.notesapp.repository.NoteRepository
import com.an.notesapp.util.hashedString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    private val noteId: Long = checkNotNull(savedStateHandle["noteId"])

    // This is a mutable state flow that will be used internally in the viewmodel,
    // null is given as initial value
    private val _note = MutableStateFlow<Note?>(null)

    // Immutable state flow that is exposed to the UI
    val note = _note.asStateFlow()

    init { getNote() }

    private fun getNote() = viewModelScope.launch {
        if (noteId == 0L) {
            _note.emit(createEmptyNote())
        } else {
            repository.getNote(noteId).collect {
                _note.emit(it)
            }
        }
    }

    fun updateNoteTitle(title: String) {
        _note.value = _note.value?.copy(title = title)
    }

    fun updateNoteDesc(desc: String) {
        _note.value = _note.value?.copy(description = desc)
    }

    fun addOrUpdateNote() {
        _note.value?.let {
            if (noteId == 0L) addNote(it)
            else updateNote(it)
        }
    }

    /**
     * This function is used to update a note to the database.
     * 1. viewModelScope.launch is used to launch a coroutine within the viewModel lifecycle.
     * 2. Dispatchers.IO is used to change the dispatcher of the coroutine to IO,
     * which is optimal for IO operations, and does not block the main thread.
     * 3. note.copy() is used to update the modified date in the database before updating the note.
     * 4. repository.updateNote(note) is used to update the note in the database.
     */
    private fun updateNote(note: Note) {
        val updatedNote = note.copy(modifiedAt = OffsetDateTime.now())
        viewModelScope.launch(Dispatchers.IO) { repository.updateNote(updatedNote) }
    }

    /**
     * This function is used to add a new note to the database.
     * 1. viewModelScope.launch is used to launch a coroutine within the viewModel lifecycle.
     * 2. Dispatchers.IO is used to change the dispatcher of the coroutine to IO,
     * which is optimal for IO operations, and does not block the main thread.
     * 3. repository.insertNote(note) is used to add the note to the database.
     */
    private fun addNote(note: Note) {
        val updatedNote = note.copy(
            createdAt = OffsetDateTime.now(),
            modifiedAt = OffsetDateTime.now(),
            password = note.password?.let { it.hashedString() }
        )
        viewModelScope.launch(Dispatchers.IO) { repository.insertNote(updatedNote) }
    }

    private fun createEmptyNote() = Note(
        title = "",
        description = "",
        encrypt = false,
        password = null,
        createdAt = OffsetDateTime.now(),
        modifiedAt = OffsetDateTime.now()
    )
}