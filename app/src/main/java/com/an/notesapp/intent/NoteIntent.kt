package com.an.notesapp.intent

import com.an.notesapp.model.db.Note

sealed class NoteIntent {
    object LoadNotes : NoteIntent()
    data class AddNote(val title: String, val description: String) : NoteIntent()
    data class UpdateNote(val note: Note) : NoteIntent()
    data class DeleteNote(val note: Note) : NoteIntent()
}