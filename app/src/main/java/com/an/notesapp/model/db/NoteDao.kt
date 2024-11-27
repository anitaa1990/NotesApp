package com.an.notesapp.model.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO's are data access objects. They includes methods that offer abstract access
 * to the app's database. At compile time, Room automatically generates implementations
 * of the DAOs that we define.
 */
@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM Note")
    fun fetchAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM Note WHERE id =:noteId")
    suspend fun getNote(noteId: Long): Note
}
