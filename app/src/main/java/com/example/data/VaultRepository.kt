package com.example.data

import kotlinx.coroutines.flow.Flow

class VaultRepository(
    private val vaultDao: VaultDao,
    private val noteDao: NoteDao
) {
    val allItems: Flow<List<VaultItem>> = vaultDao.getAllVaultItems()
    val allPasswords: Flow<List<PasswordEntry>> = vaultDao.getAllPasswords()
    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()

    fun searchItems(query: String): Flow<List<VaultItem>> = vaultDao.searchItems(query)

    suspend fun getItemById(id: Long): VaultItem? = vaultDao.getItemById(id)

    suspend fun insertItem(item: VaultItem): Long = vaultDao.insertItem(item)

    suspend fun updateItem(item: VaultItem) = vaultDao.updateItem(item)

    suspend fun deleteItem(item: VaultItem) = vaultDao.deleteItem(item)

    suspend fun deleteById(id: Long) = vaultDao.deleteById(id)

    suspend fun insertPassword(entry: PasswordEntry): Long = vaultDao.insertPassword(entry)

    suspend fun deletePassword(entry: PasswordEntry) = vaultDao.deletePassword(entry)

    // Room Note Entity operations
    suspend fun getNoteById(id: Long): Note? = noteDao.getNoteById(id)

    suspend fun insertNote(note: Note): Long = noteDao.insertNote(note)

    suspend fun updateNote(note: Note) = noteDao.updateNote(note)

    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)

    suspend fun populateInitialDataIfEmpty() {
        val initialItems = listOf(
            VaultItem(
                title = "Neural Network Specs",
                content = "# Neural Network Specs\nArchitectural diagrams for core transformer logic in v4.0. Ephemeral key vectors and local attention bounds.\n\n### Related Documents:\n- Internal link to [[Hardware Inventory]] for HSM execution bounds.\n- Audit findings cross-referenced in [[Q4 Security Audit]].",
                category = "Notes",
                notebook = "Research Lab",
                tags = "#ai, #neural, #architecture",
                isPinned = true,
                isEncrypted = true,
                attachmentName = "neural_spec.svg",
                attachmentType = "SVG"
            ),
            VaultItem(
                title = "Hardware Inventory",
                content = "# Hardware Inventory\n- 12 physical hardware security keys\n- Local YubiKeys\n- HSE encrypted storage modules\n\n### Connected Audits:\n- Verified against [[Q4 Security Audit]] compliance checks.",
                category = "Inventory",
                notebook = "Project Phoenix",
                tags = "#hardware, #security, #inventory",
                isPinned = true,
                isEncrypted = true
            ),
            VaultItem(
                title = "Q4 Security Audit",
                content = "# Q4 Security Audit Report\n> **Status:** Critical vulnerabilities resolved via zero-knowledge localized entropy headers.\n\n### Document Links:\n- Refer to [[Meeting Notes: Data Sovereignty]] for key exchange protocols.\n- Hardware audit verified in [[Hardware Inventory]].",
                category = "Audit",
                notebook = "Deep Storage",
                tags = "#security, #audit, #vulnerability",
                isPinned = true,
                isEncrypted = true,
                attachmentName = "Q4_Audit.pdf",
                attachmentType = "PDF"
            ),
            VaultItem(
                title = "Meeting Notes: Data Sovereignty",
                content = "# Data Sovereignty & Zero-Knowledge\n- Localized zero-knowledge proofs\n- End-to-end local encryption\n- User-held key protocols\n\n### Connected Notes:\n- Implementation details in [[Neural Network Specs]]\n- Audited under [[Q4 Security Audit]]",
                category = "Notes",
                notebook = "Research Lab",
                tags = "#legal, #privacy, #sovereignty",
                isPinned = false,
                isEncrypted = true
            ),
            VaultItem(
                title = "Theoretical Foundations of Quantum Entanglement",
                content = "# Quantum Entanglement\nQuantum entanglement is a physical phenomenon that occurs when a group of particles interact in ways such that the quantum state of each particle cannot be described independently.\n\n### Key Principles\n1. **Bell State representation:**\n`|ψ⟩ = 1/√2 (|00⟩ + |11⟩)`\n\n2. **Einstein–Podolsky–Rosen (EPR) Paradox:**\nLocal realism vs non-locality bounds under localized observation.\n\n### Cross-References:\n- Applied in [[Neural Network Specs]] for quantum vector attention.",
                category = "Research",
                notebook = "Research Lab",
                tags = "#physics, #qubit, #epr-paradox",
                isPinned = false,
                isEncrypted = true,
                attachmentName = "vault_diagram.svg",
                attachmentType = "SVG"
            )
        )

        for (item in initialItems) {
            vaultDao.insertItem(item)
            // Populate initial Room Note entity for each item
            noteDao.insertNote(
                Note(
                    title = item.title,
                    content = item.content,
                    category = item.category,
                    tags = item.tags,
                    timestamp = item.updatedAt
                )
            )
        }

        val initialPasswords = listOf(
            PasswordEntry(serviceName = "LifeVault Encryption Key", username = "vault_master", encryptedPassword = "••••••••••••••••"),
            PasswordEntry(serviceName = "ProtonMail Secure", username = "researcher@pm.me", encryptedPassword = "••••••••••••••••"),
            PasswordEntry(serviceName = "GitHub SSH Key", username = "vault-dev", encryptedPassword = "••••••••••••••••"),
            PasswordEntry(serviceName = "Crypto Wallet Seed", username = "cold_storage_1", encryptedPassword = "••••••••••••••••")
        )

        for (pass in initialPasswords) {
            vaultDao.insertPassword(pass)
        }
    }
}
