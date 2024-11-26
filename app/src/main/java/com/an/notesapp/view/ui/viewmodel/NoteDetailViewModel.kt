package com.an.notesapp.view.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.an.notesapp.model.db.Note
import com.an.notesapp.model.repository.NoteRepository
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
) : BaseViewModel() {
    // "noteId" is nullable because noteId can be null when adding a new note
    private val noteId: String? = savedStateHandle["noteId"]

    // This is a mutable state flow that will be used internally in the viewmodel,
    private val _noteDetailViewState = MutableStateFlow(NoteDetailViewState())

    // Immutable state flow that is exposed to the UI
    val noteDetailViewState = _noteDetailViewState.asStateFlow()

    init { getNote() }

    private fun getNote() = viewModelScope.launch {
        noteId?.let {
            viewModelScope.launch(IO) {
                _noteDetailViewState.value = _noteDetailViewState.value.copy(
                    note = repository.getNote(it.toLong())
                )
            }
        }
    }

    data class NoteDetailViewState(
        val note: Note = Note(title = "", description = "", modifiedAt = OffsetDateTime.now())
    )
}