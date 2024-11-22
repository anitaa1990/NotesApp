package com.an.notesapp.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.an.notesapp.R
import com.an.notesapp.db.Note
import com.an.notesapp.repository.NoteRepository
import com.an.notesapp.ui.model.NoteAction
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
    private val _noteUiModelState = MutableStateFlow(createNewNoteUiModel())

    // Immutable state flow that is exposed to the UI
    val noteUiModelState = _noteUiModelState.asStateFlow()

    private val _noteActionState = MutableStateFlow<NoteAction>(NoteAction.Idle)
    val noteActionState = _noteActionState.asStateFlow()

    init { getNote() }

    private fun getNote() = viewModelScope.launch {
        noteId?.let {
            repository.getNote(it.toLong()).collect { note ->
                _noteUiModelState.emit(note.toUiModel())
            }
        }
    }

    fun updateNoteTitle(title: String) {
        _noteUiModelState.value = _noteUiModelState.value.copy(title = title)
    }

    fun updateNoteDesc(desc: String) {
        _noteUiModelState.value = _noteUiModelState.value.copy(description = desc)
    }

    fun validatePassword(newNoteLockedValue: Boolean, newPassword: String) {
        val oldNoteLockedValue = noteUiModelState.value.noteLocked
        when {
            // note is locked and user is trying to unlock the note
            oldNoteLockedValue && !newNoteLockedValue -> {
                if(noteUiModelState.value.password == newPassword) {
                    // password is correct so note can be unlocked
                    updateLock(false, newPassword)
                    _noteActionState.update { NoteAction.NoteNotLocked(noteUiModelState.value.noteId) }
                } else {
                    // password is wrong so display validation error
                    _noteActionState.update { NoteAction.PasswordValidationError(R.string.error_password) }
                }
            }
            // note was unlocked and user is trying to lock the note
            !oldNoteLockedValue && newNoteLockedValue -> {
                updateLock(true, newPassword)
                _noteActionState.update { NoteAction.NoteLocked }
            }
        }
    }

    fun addOrUpdateNote() {
        if (noteId == null) {
            addNote(_noteUiModelState.value.toNote())
            triggerEvent(Event.ShowSnackbar(R.string.add_note_success))
        } else {
            updateNote(_noteUiModelState.value.toNote())
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
        _noteUiModelState.update {
            _noteUiModelState.value.copy(noteLocked = lockNote, password = password)
        }
    }

    private fun createNewNoteUiModel() = NoteUiModel(
        noteId = 0L,
        title = "",
        description = "",
        noteLocked = false,
        createdAt = OffsetDateTime.now()
    )
}