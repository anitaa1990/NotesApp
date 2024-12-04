package com.an.notesapp.intent

import androidx.compose.ui.text.AnnotatedString
import com.an.notesapp.model.db.Note

sealed class NoteIntent {
    object LoadNotes : NoteIntent()
    object LoadNote : NoteIntent()
    data object AddOrSaveNote : NoteIntent()
    data class UpdateNoteTitle(val title: String) : NoteIntent()
    data class UpdateNoteDescription(val description: AnnotatedString) : NoteIntent()
    data class LockNote(val password: String): NoteIntent()
    data object UnLockNote: NoteIntent()
    data class DeleteNote(val note: Note) : NoteIntent()
    data class OpenNoteClicked(val note: Note): NoteIntent()
    data class ValidatePassword(val password: String): NoteIntent()
    data object DismissPasswordSheet : NoteIntent()
}