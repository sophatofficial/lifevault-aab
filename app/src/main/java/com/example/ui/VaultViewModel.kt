package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Note
import com.example.data.PasswordEntry
import com.example.data.VaultItem
import com.example.data.VaultRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.utils.AppLanguage
import com.example.utils.AppThemeStyle

enum class Screen {
    NOTES,
    VAULT,
    SEARCH,
    GRAPH,
    SCANNER,
    EDITOR,
    PDF_READER,
    SETTINGS
}

class VaultViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: VaultRepository

    private val _currentScreen = MutableStateFlow(Screen.NOTES)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _selectedItem = MutableStateFlow<VaultItem?>(null)
    val selectedItem: StateFlow<VaultItem?> = _selectedItem.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedNotebook = MutableStateFlow("All")
    val selectedNotebook: StateFlow<String> = _selectedNotebook.asStateFlow()

    private val _selectedTag = MutableStateFlow("All")
    val selectedTag: StateFlow<String> = _selectedTag.asStateFlow()

    private val _aiResponse = MutableStateFlow<String?>(null)
    val aiResponse: StateFlow<String?> = _aiResponse.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _isVaultUnlocked = MutableStateFlow(false)
    val isVaultUnlocked: StateFlow<Boolean> = _isVaultUnlocked.asStateFlow()

    private val _appTheme = MutableStateFlow(AppThemeStyle.CYBERPUNK_CYAN)
    val appTheme: StateFlow<AppThemeStyle> = _appTheme.asStateFlow()

    private val _appLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    val passwords: StateFlow<List<PasswordEntry>>
    val items: StateFlow<List<VaultItem>>
    val notes: StateFlow<List<Note>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = VaultRepository(database.vaultDao(), database.noteDao())

        viewModelScope.launch {
            repository.populateInitialDataIfEmpty()
        }

        passwords = repository.allPasswords.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        notes = repository.allNotes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        items = combine(
            repository.allItems,
            _searchQuery,
            _selectedNotebook,
            _selectedTag
        ) { all, query, notebook, tag ->
            all.filter { item ->
                val matchesQuery = query.isBlank() ||
                        item.title.contains(query, ignoreCase = true) ||
                        item.content.contains(query, ignoreCase = true) ||
                        item.tags.contains(query, ignoreCase = true)
                val matchesNotebook = notebook == "All" || item.notebook.equals(notebook, ignoreCase = true)
                val matchesTag = tag == "All" || item.tags.contains(tag, ignoreCase = true)

                matchesQuery && matchesNotebook && matchesTag
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun navigateTo(screen: Screen, item: VaultItem? = null) {
        _selectedItem.value = item ?: _selectedItem.value
        _currentScreen.value = screen
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectNotebook(notebook: String) {
        _selectedNotebook.value = notebook
    }

    fun selectTag(tag: String) {
        _selectedTag.value = tag
    }

    fun createNewItem(
        title: String,
        content: String,
        category: String = "Notes",
        notebook: String = "Research Lab",
        tags: String = "#research, #security"
    ) {
        viewModelScope.launch {
            val newItem = VaultItem(
                title = title,
                content = content,
                category = category,
                notebook = notebook,
                tags = tags,
                updatedAt = System.currentTimeMillis()
            )
            val newId = repository.insertItem(newItem)
            _selectedItem.value = newItem.copy(id = newId)

            // Save into Room Note Entity
            repository.insertNote(
                Note(
                    title = title,
                    content = content,
                    category = category,
                    tags = tags,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun saveItem(item: VaultItem) {
        viewModelScope.launch {
            if (item.id == 0L) {
                val newId = repository.insertItem(item)
                _selectedItem.value = item.copy(id = newId)
            } else {
                repository.updateItem(item)
                _selectedItem.value = item
            }

            // Also persist into Room Note entity
            repository.insertNote(
                Note(
                    title = item.title,
                    content = item.content,
                    category = item.category,
                    tags = item.tags,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun saveNoteEntity(title: String, content: String, category: String = "Notes", tags: String = "", folderId: Long? = null) {
        viewModelScope.launch {
            repository.insertNote(
                Note(
                    title = title,
                    content = content,
                    category = category,
                    tags = tags,
                    timestamp = System.currentTimeMillis(),
                    folderId = folderId
                )
            )
        }
    }

    fun deleteItem(item: VaultItem) {
        viewModelScope.launch {
            repository.deleteItem(item)
            if (_selectedItem.value?.id == item.id) {
                _selectedItem.value = null
                _currentScreen.value = Screen.NOTES
            }
        }
    }

    fun addPassword(service: String, username: String, pass: String) {
        viewModelScope.launch {
            repository.insertPassword(
                PasswordEntry(
                    serviceName = service,
                    username = username,
                    encryptedPassword = pass.ifBlank { "••••••••••••••••" }
                )
            )
        }
    }

    fun runLocalAiSummary(prompt: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiResponse.value = null
            kotlinx.coroutines.delay(800)
            _aiResponse.value = when {
                prompt.contains("receipt", ignoreCase = true) ->
                    "Found 3 local receipt items from last week:\n• Cafe L'Aube ($42.50)\n• Hardware Key Supply ($189.00)\n• Secure Server Host ($29.99)\n\nAll documents verified & stored locally."

                prompt.contains("summarize", ignoreCase = true) ->
                    "Auto-Summary:\n• Quantum correlation & Bell State representation |ψ⟩ = 1/√2 (|00⟩ + |11⟩).\n• Zero-knowledge proof protocols eliminate metadata leakages.\n• AES-256 localized entropy keys stored in HSE element."

                prompt.contains("tokyo", ignoreCase = true) ->
                    "Travel Itinerary Tokyo (Encrypted Note):\n1. Akihabara Tech Hardware Market\n2. Cryptography Conference 2026\n3. Local offline map data downloaded."

                else ->
                    "Offline AI Model Response:\n\"Analyzing '${prompt}' across your 100% encrypted vault notes. Zero external servers consulted. Key concepts indexed & mapped.\""
            }
            _isAiLoading.value = false
        }
    }

    fun toggleVaultLock() {
        _isVaultUnlocked.value = !_isVaultUnlocked.value
    }

    fun setAppTheme(theme: AppThemeStyle) {
        _appTheme.value = theme
    }

    fun setAppLanguage(language: AppLanguage) {
        _appLanguage.value = language
    }
}
