package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDao {
    @Query("SELECT * FROM vault_items ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllVaultItems(): Flow<List<VaultItem>>

    @Query("SELECT * FROM vault_items WHERE id = :id")
    suspend fun getItemById(id: Long): VaultItem?

    @Query("SELECT * FROM vault_items WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    fun searchItems(query: String): Flow<List<VaultItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: VaultItem): Long

    @Update
    suspend fun updateItem(item: VaultItem)

    @Delete
    suspend fun deleteItem(item: VaultItem)

    @Query("DELETE FROM vault_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM password_entries ORDER BY serviceName ASC")
    fun getAllPasswords(): Flow<List<PasswordEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPassword(entry: PasswordEntry): Long

    @Delete
    suspend fun deletePassword(entry: PasswordEntry)
}
