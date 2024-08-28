package com.an.notesapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.an.notesapp.db.Note
import com.an.notesapp.repository.NoteRepository
import com.an.notesapp.util.hashedString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {
    private val _notesUiState = MutableStateFlow(NoteUiState())
    val notesUiState = _notesUiState.asStateFlow()

    init { getNotes() }
    private fun getNotes() {
        viewModelScope.launch {
            repository.getNotes().distinctUntilChanged().collect {
                _notesUiState.value = NoteUiState(isEmpty = it.isEmpty(), notes = it)
            }
        }
    }

    /**
     * This function is used to add a new note to the database.
     * 1. viewModelScope.launch is used to launch a coroutine within the viewModel lifecycle.
     * 2. Dispatchers.IO is used to change the dispatcher of the coroutine to IO,
     * which is optimal for IO operations, and does not block the main thread.
     * 3. repository.insertNote(note) is used to add the note to the database.
     */
    fun addNote(
        title: String,
        description: String,
        encrypted: Boolean = false,
        password: String? = null
    ) {
        val note = Note(
            title = title,
            description = description,
            encrypt = encrypted,
            password = password?.hashedString(),
            createdAt = OffsetDateTime.now(),
            modifiedAt = OffsetDateTime.now()
        )

        viewModelScope.launch(Dispatchers.IO) { repository.insertNote(note) }
    }

    /**
     * This function is used to update a note to the database.
     * 1. viewModelScope.launch is used to launch a coroutine within the viewModel lifecycle.
     * 2. Dispatchers.IO is used to change the dispatcher of the coroutine to IO,
     * which is optimal for IO operations, and does not block the main thread.
     * 3. note.copy() is used to update the modified date in the database before updating the note.
     * 4. repository.updateNote(note) is used to update the note in the database.
     */
    fun updateNote(note: Note) {
        val updatedNote = note.copy(modifiedAt = OffsetDateTime.now())
        viewModelScope.launch(Dispatchers.IO) { repository.updateNote(updatedNote) }
    }

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
    }

    data class NoteUiState(
        val isEmpty: Boolean = true,
        val notes: List<Note> = emptyList()
    )
}