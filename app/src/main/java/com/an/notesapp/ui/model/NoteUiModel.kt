package com.an.notesapp.ui.model

import com.an.notesapp.db.Note
import java.time.OffsetDateTime

data class NoteUiModel(
    val noteId: Long,
    val title: String,
    val description: String,
    val noteLocked: Boolean,
    val password: String? = null,
    val createdAt: OffsetDateTime
)

fun NoteUiModel.toNote(): Note {
    return Note(
        id = this.noteId,
        title = this.title,
        description = this.description,
        encrypt = this.noteLocked,
        password = this.password,
        createdAt = this.createdAt,
        modifiedAt = OffsetDateTime.now()
    )
}

fun Note.toUiModel(): NoteUiModel {
    return NoteUiModel(
        noteId = this.id,
        title = this.title,
        description = this.description,
        noteLocked = this.encrypt,
        password = this.password,
        createdAt = this.createdAt
    )
}