package com.an.notesapp.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

/**
 * You define each Room entity as a class annotated with @Entity.
 * A Room entity includes fields for each column in the corresponding table in the database,
 * including one or more columns that make up the primary key.
 */
@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val encrypt: Boolean,
    val password: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: OffsetDateTime,
    @ColumnInfo(name = "modified_at")
    val modifiedAt: OffsetDateTime
)
