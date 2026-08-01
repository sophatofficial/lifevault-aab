package com.example.utils

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.Note
import com.example.data.PasswordEntry
import com.example.data.VaultItem
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

/**
 * Utility object to export the full Room database contents (VaultItems, PasswordEntries, Notes)
 * into a structured JSON backup file on the device's local storage.
 */
object DatabaseBackupExporter {

    /**
     * Serializes all Room database tables into a formatted JSON backup file.
     *
     * @param context Application context
     * @param vaultItems All VaultItem records from Room
     * @param passwords All PasswordEntry records from Room
     * @param notes All Note records from Room
     * @return Created [File] object on local storage, or null if export failed.
     */
    fun exportDatabaseToJson(
        context: Context,
        vaultItems: List<VaultItem>,
        passwords: List<PasswordEntry>,
        notes: List<Note>
    ): File? {
        try {
            val rootJson = JSONObject()
            rootJson.put("app", "LifeVault")
            rootJson.put("version", "4.2.0")
            rootJson.put("exportedAt", System.currentTimeMillis())
            rootJson.put("exportedDate", System.currentTimeMillis().toFormattedDate())

            // 1. Vault Items Table
            val vaultArray = JSONArray()
            for (item in vaultItems) {
                val itemObj = JSONObject().apply {
                    put("id", item.id)
                    put("title", item.title)
                    put("content", item.content)
                    put("category", item.category)
                    put("notebook", item.notebook)
                    put("tags", item.tags)
                    put("isPinned", item.isPinned)
                    put("isEncrypted", item.isEncrypted)
                    put("updatedAt", item.updatedAt)
                    put("attachmentName", item.attachmentName ?: "")
                    put("attachmentType", item.attachmentType ?: "")
                    put("audioPath", item.audioPath ?: "")
                }
                vaultArray.put(itemObj)
            }
            rootJson.put("vaultItems", vaultArray)

            // 2. Passwords Table
            val passArray = JSONArray()
            for (pass in passwords) {
                val passObj = JSONObject().apply {
                    put("id", pass.id)
                    put("serviceName", pass.serviceName)
                    put("username", pass.username)
                    put("encryptedPassword", pass.encryptedPassword)
                    put("category", pass.category)
                    put("updatedAt", pass.updatedAt)
                }
                passArray.put(passObj)
            }
            rootJson.put("passwords", passArray)

            // 3. Notes Table
            val notesArray = JSONArray()
            for (note in notes) {
                val noteObj = JSONObject().apply {
                    put("id", note.id)
                    put("title", note.title)
                    put("content", note.content)
                    put("category", note.category)
                    put("tags", note.tags)
                    put("timestamp", note.timestamp)
                    put("folderId", note.folderId ?: -1)
                    put("audioPath", note.audioPath ?: "")
                }
                notesArray.put(noteObj)
            }
            rootJson.put("notes", notesArray)

            // Save JSON string to device's local storage
            val fileName = "LifeVault_Backup_${System.currentTimeMillis()}.json"
            val backupDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "LifeVaultBackups").apply {
                if (!exists()) mkdirs()
            }
            val backupFile = File(backupDir, fileName)

            FileOutputStream(backupFile).use { out ->
                out.write(rootJson.toString(2).toByteArray(Charsets.UTF_8))
            }

            return backupFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Shares the generated JSON backup file via standard Android share chooser.
     */
    fun shareBackupFile(context: Context, backupFile: File) {
        try {
            val authority = "${context.packageName}.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, backupFile)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "LifeVault Database Backup (${backupFile.name})")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Share JSON Database Backup")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Backup file created: ${backupFile.name}", Toast.LENGTH_LONG).show()
        }
    }
}
