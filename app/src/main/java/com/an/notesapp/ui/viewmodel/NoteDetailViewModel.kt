package com.an.notesapp.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.an.notesapp.db.Note
import com.an.notesapp.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: NoteRepository
) : ViewModel() {
    private val noteId: Long? = savedStateHandle["noteId"]

    // This is a mutable state flow that will be used internally in the viewmodel,
    // null is given as initial value
    private val _note = MutableStateFlow<Note?>(null)

    // Immutable state flow that is exposed to the UI
    val note = _note.asStateFlow()

    init { getNote() }

    private fun getNote() = viewModelScope.launch {
        noteId?.let { id ->
            repository.getNote(id).collect {
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

    fun encrypt(encrypt: Boolean) {
        _note.value = _note.value?.copy(encrypt = encrypt)
    }

    fun updatePassword(password: String) {
        _note.value = _note.value?.copy(password = password)
    }
}