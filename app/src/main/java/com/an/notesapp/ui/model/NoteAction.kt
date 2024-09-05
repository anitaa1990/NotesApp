package com.an.notesapp.ui.model

sealed class NoteAction {
    data object Idle: NoteAction()
    data object NoteLocked : NoteAction()
    data class NoteNotLocked(val noteId: Long) : NoteAction()
    data class PasswordValidationSuccess(val noteId: Long) : NoteAction()
    data class PasswordValidationError(val errorResId: Int?) : NoteAction()
}


