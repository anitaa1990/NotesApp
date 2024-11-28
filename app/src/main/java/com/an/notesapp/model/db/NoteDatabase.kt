package com.an.notesapp.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * [NoteDatabase] defines the database configuration and serves as the app's main access point
 * to the persisted data. The database class must satisfy the following conditions:
 *  1. The class must be annotated with a @Database annotation that includes an entities
 *     array that lists all of the data entities associated with the database.
 *  2. The class must be an abstract class that extends RoomDatabase.
 *  3. For each DAO class that is associated with the database, the database class must
 *     define an abstract method that has zero arguments and returns an instance of the DAO class.
 */
@Database(entities = [Note::class], version = 1, exportSchema = false)
@TypeConverters(TimestampConverter::class, FormatSetTypeConverter::class)
abstract class NoteDatabase: RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
