package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vault_items")
data class VaultItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "Notes",
    val notebook: String = "Research Lab",
    val tags: String = "#research, #security",
    val isPinned: Boolean = false,
    val isEncrypted: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis(),
    val attachmentName: String? = null,
    val attachmentType: String? = null,
    val audioPath: String? = null
)

@Entity(tableName = "password_entries")
data class PasswordEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val serviceName: String,
    val username: String,
    val encryptedPassword: String,
    val category: String = "General",
    val updatedAt: Long = System.currentTimeMillis()
)
