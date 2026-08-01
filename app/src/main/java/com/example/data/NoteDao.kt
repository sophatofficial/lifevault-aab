package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object for [Note] operations.
 */
@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): Note?

    @Query("SELECT * FROM notes WHERE folderId = :folderId ORDER BY timestamp DESC")
    fun getNotesByFolder(folderId: Long): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE category = :category ORDER BY timestamp DESC")
    fun getNotesByCategory(category: String): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE tags LIKE '%' || :tag || '%' ORDER BY timestamp DESC")
    fun searchNotesByTag(tag: String): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)
}
