package com.an.notesapp.module

import android.content.Context
import androidx.room.Room
import com.an.notesapp.db.NoteDao
import com.an.notesapp.db.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NoteDbModule {
    @Provides
    @Singleton
    fun provideNoteDatabase(@ApplicationContext appContext: Context): NoteDatabase {
        return Room.databaseBuilder(
            appContext,
            NoteDatabase::class.java,
            "note_database"
        ).build()
    }

    @Provides
    fun provideChannelDao(database: NoteDatabase): NoteDao {
        return database.noteDao()
    }
}
