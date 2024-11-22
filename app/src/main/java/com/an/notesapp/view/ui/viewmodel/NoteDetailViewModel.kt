package com.an.notesapp.view.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.an.notesapp.model.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: NoteRepository
) : BaseViewModel() {
}