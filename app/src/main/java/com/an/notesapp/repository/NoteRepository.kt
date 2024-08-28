package com.an.notesapp.repository

import com.an.notesapp.db.Note
import com.an.notesapp.db.NoteDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao
) {
    suspend fun insertNote(note: Note) = noteDao.insertNote(note)
    suspend fun updateNote(note: Note) = noteDao.updateNote(note)
    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)
    fun getNotes(): Flow<List<Note>> = noteDao.fetchAllNotes()
    fun getNote(noteId: Long) = noteDao.getNote(noteId)
}