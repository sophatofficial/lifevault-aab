package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Database Entity representing a Note in LifeVault.
 *
 * @property id Unique primary key for the note entry.
 * @property title The title of the note.
 * @property content Raw text content supporting Markdown formatting.
 * @property category Category or topic of the note for organization (e.g., "Notes", "Research", "Audit").
 * @property tags Tag string or space/comma separated tags (e.g., "#ai, #neural").
 * @property timestamp Creation or last modification timestamp in milliseconds.
 * @property folderId Identifier of the parent folder/notebook (null if unassigned or root).
 */
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String, // Supports raw Markdown text
    val category: String = "Notes",
    val tags: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val folderId: Long? = null,
    val audioPath: String? = null
)
