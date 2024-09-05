package com.an.notesapp.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.an.notesapp.R
import com.an.notesapp.db.Note
import com.an.notesapp.repository.NoteRepository
import com.an.notesapp.ui.model.NoteUiModel
import com.an.notesapp.ui.model.toNote
import com.an.notesapp.ui.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: NoteRepository
) : BaseViewModel() {
    // "noteId" is nullable because noteId can be null when adding a new note
    private val noteId: String? = savedStateHandle["noteId"]

    // This is a mutable state flow that will be used internally in the viewmodel,
    private val _noteUiState = MutableStateFlow(NoteDetailUiState())

    // Immutable state flow that is exposed to the UI
    val noteUiState = _noteUiState.asStateFlow()

    init { getNote() }

    private fun getNote() = viewModelScope.launch {
        noteId?.let {
            repository.getNote(it.toLong()).collect { note ->
                _noteUiState.emit(
                    NoteDetailUiState(note = note.toUiModel())
                )
            }
        }
    }

    fun updateNoteTitle(title: String) {
        _noteUiState.value = _noteUiState.value.copy(
            note = _noteUiState.value.note.copy(title = title)
        )
    }

    fun updateNoteDesc(desc: String) {
        _noteUiState.value = _noteUiState.value.copy(
            note = _noteUiState.value.note.copy(description = desc)
        )
    }
    fun showOrHideBottomSheet(show: Boolean) {
        _noteUiState.update { _noteUiState.value.copy(showBottomSheet = show) }
    }

    fun toggleLock(
        lockNote: Boolean,
        password: String
    ) {
        val isNoteLocked = noteUiState.value.note.noteLocked
        when {
            isNoteLocked && !lockNote -> {
                val passwordMatches = noteUiState.value.note.password?.let { it == password }
                if (passwordMatches == true) {
                    updateLock(false, password)
                } else {
                    _noteUiState.update {
                        noteUiState.value.copy(passwordErrorStringId = R.string.error_password)
                    }
                }
            }
            else -> {
                updateLock(true, password)
            }
        }
    }

    fun addOrUpdateNote() {
        if (noteId == null) {
            addNote(_noteUiState.value.note.toNote())
            triggerEvent(Event.ShowSnackbar(R.string.add_note_success))
        } else {
            updateNote(_noteUiState.value.note.toNote())
            triggerEvent(Event.ShowSnackbar(R.string.update_note_success))
        }
        triggerEventWithDelay(Event.ExitScreen)
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
        viewModelScope.launch(Dispatchers.IO) { repository.insertNote(note) }
    }

    private fun updateLock(
        lockNote: Boolean,
        password: String
    ) {
        _noteUiState.update {
            noteUiState.value.copy(
                note = _noteUiState.value.note.copy(noteLocked = lockNote, password = password),
                showBottomSheet = false,
                passwordErrorStringId = null
            )
        }
    }

    data class NoteDetailUiState(
        val note: NoteUiModel = NoteUiModel(
            noteId = 0L,
            title = "",
            description = "",
            noteLocked = false,
            createdAt = OffsetDateTime.now()
        ),
        val showBottomSheet: Boolean = false,
        val passwordErrorStringId: Int? = null
    )
}