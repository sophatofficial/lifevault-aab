package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.ads.MobileAds
import com.example.admob.AdMobBanner
import com.example.admob.AdMobConfig
import com.example.data.VaultItem
import com.example.ui.Screen
import com.example.ui.VaultViewModel
import com.example.ui.components.AddPasswordDialog
import com.example.ui.components.LifeVaultBottomNav
import com.example.ui.components.LifeVaultTopBar
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.GraphScreen
import com.example.ui.screens.NotesScreen
import com.example.ui.screens.PdfReaderScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VaultScreen
import com.example.ui.theme.LifeVaultTheme

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.compose.runtime.mutableStateOf
import com.example.ui.components.BiometricLockOverlay
import com.example.widget.QuickNoteWidgetProvider

class MainActivity : FragmentActivity() {
    private val widgetActionState = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        widgetActionState.value = intent?.action

        // Initialize Google Mobile Ads SDK & Preload Interstitial Ad
        MobileAds.initialize(this) {}
        AdMobConfig.initialize(applicationContext)
        AdMobConfig.loadInterstitialAd(this)

        setContent {
            val viewModel: VaultViewModel = viewModel()
            val appTheme by viewModel.appTheme.collectAsState()
            val activeWidgetAction by widgetActionState

            LaunchedEffect(activeWidgetAction) {
                activeWidgetAction?.let { action ->
                    handleWidgetAction(action, viewModel)
                    widgetActionState.value = null
                }
            }

            LifeVaultTheme(themeStyle = appTheme) {
                LifeVaultApp(activity = this, viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.action?.let { action ->
            widgetActionState.value = action
        }
    }

    private fun handleWidgetAction(action: String, viewModel: VaultViewModel) {
        when (action) {
            QuickNoteWidgetProvider.ACTION_ADD_NOTE -> {
                viewModel.createNewItem(
                    title = "Quick Launcher Note",
                    content = "# Quick Launcher Note\n\nCreated directly from home screen widget."
                )
                viewModel.navigateTo(Screen.EDITOR)
            }
            QuickNoteWidgetProvider.ACTION_SCAN_DOC -> {
                viewModel.navigateTo(Screen.SCANNER)
            }
            QuickNoteWidgetProvider.ACTION_OPEN_VAULT -> {
                viewModel.navigateTo(Screen.VAULT)
            }
        }
    }
}

@Composable
fun LifeVaultApp(
    activity: FragmentActivity,
    viewModel: VaultViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val items by viewModel.items.collectAsState()
    val passwords by viewModel.passwords.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val selectedItem by viewModel.selectedItem.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedNotebook by viewModel.selectedNotebook.collectAsState()
    val selectedTag by viewModel.selectedTag.collectAsState()
    val aiResponse by viewModel.aiResponse.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val isUnlocked by viewModel.isVaultUnlocked.collectAsState()
    val appTheme by viewModel.appTheme.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()

    var showAddPasswordDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = appTheme.backgroundColor,
        topBar = {
            if (currentScreen != Screen.SCANNER && currentScreen != Screen.EDITOR && currentScreen != Screen.PDF_READER) {
                LifeVaultTopBar(
                    currentScreen = currentScreen,
                    isUnlocked = isUnlocked,
                    onLockToggle = {
                        viewModel.toggleVaultLock()
                        AdMobConfig.showInterstitialAd(activity)
                    },
                    onSearchClick = { viewModel.navigateTo(Screen.SEARCH) }
                )
            }
        },
        bottomBar = {
            Column {
                // AdMob Banner Ad placement above navigation bar
                if (currentScreen != Screen.SCANNER) {
                    AdMobBanner(
                        onUpgradeClick = { viewModel.navigateTo(Screen.SETTINGS) }
                    )
                }

                if (currentScreen != Screen.SCANNER && currentScreen != Screen.EDITOR && currentScreen != Screen.PDF_READER) {
                    LifeVaultBottomNav(
                        currentScreen = currentScreen,
                        currentLanguage = appLanguage,
                        currentTheme = appTheme,
                        onScreenSelected = { screen -> viewModel.navigateTo(screen) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(appTheme.backgroundColor)
        ) {
            if (!isUnlocked) {
                BiometricLockOverlay(
                    activity = activity,
                    onUnlockSuccess = { viewModel.toggleVaultLock() }
                )
            } else {
                when (currentScreen) {
                Screen.NOTES -> NotesScreen(
                    items = items,
                    selectedNotebook = selectedNotebook,
                    selectedTag = selectedTag,
                    onNotebookSelect = { viewModel.selectNotebook(it) },
                    onTagSelect = { viewModel.selectTag(it) },
                    onItemClick = { note ->
                        viewModel.navigateTo(Screen.EDITOR, note)
                    },
                    onNewItemClick = {
                        viewModel.createNewItem(
                            title = "Untitled Vault Note",
                            content = "Start typing your encrypted notes here..."
                        )
                        viewModel.navigateTo(Screen.EDITOR)
                    },
                    onScanClick = {
                        viewModel.navigateTo(Screen.SCANNER)
                    }
                )

                Screen.VAULT -> VaultScreen(
                    passwords = passwords,
                    onAddPasswordClick = { showAddPasswordDialog = true },
                    onOpenScannerClick = { viewModel.navigateTo(Screen.SCANNER) },
                    onOpenPdfClick = { viewModel.navigateTo(Screen.PDF_READER) }
                )

                Screen.SEARCH -> SearchScreen(
                    query = searchQuery,
                    items = items,
                    aiResponse = aiResponse,
                    isAiLoading = isAiLoading,
                    onQueryChange = { viewModel.setSearchQuery(it) },
                    onAiQuerySubmit = { prompt -> viewModel.runLocalAiSummary(prompt) },
                    onItemClick = { note -> viewModel.navigateTo(Screen.EDITOR, note) }
                )

                Screen.GRAPH -> GraphScreen(
                    items = items,
                    onNodeItemClick = { note -> viewModel.navigateTo(Screen.EDITOR, note) }
                )

                Screen.EDITOR -> EditorScreen(
                    item = selectedItem,
                    allItems = items,
                    onSaveItem = { note ->
                        viewModel.saveItem(note)
                        AdMobConfig.showInterstitialAd(activity)
                        viewModel.navigateTo(Screen.NOTES)
                    },
                    onOpenNote = { targetNote ->
                        viewModel.navigateTo(Screen.EDITOR, targetNote)
                    },
                    onBackClick = { viewModel.navigateTo(Screen.NOTES) }
                )

                Screen.SCANNER -> ScannerScreen(
                    onScanComplete = { title, text ->
                        viewModel.createNewItem(
                            title = title,
                            content = text,
                            category = "Scanned Document",
                            notebook = "Research Lab",
                            tags = "#receipt, #ocr, #scanned"
                        )
                        AdMobConfig.showInterstitialAd(activity)
                        viewModel.navigateTo(Screen.NOTES)
                    },
                    onBackClick = { viewModel.navigateTo(Screen.NOTES) }
                )

                Screen.PDF_READER -> PdfReaderScreen(
                    onBackClick = { viewModel.navigateTo(Screen.VAULT) }
                )

                Screen.SETTINGS -> SettingsScreen(
                    vaultItems = items,
                    passwords = passwords,
                    notes = notes,
                    currentTheme = appTheme,
                    onThemeSelect = { viewModel.setAppTheme(it) },
                    currentLanguage = appLanguage,
                    onLanguageSelect = { viewModel.setAppLanguage(it) },
                    onUpgradeClick = {
                        AdMobConfig.showInterstitialAd(activity)
                    }
                )
            }
        }
        }
    }

    if (showAddPasswordDialog) {
        AddPasswordDialog(
            onDismiss = { showAddPasswordDialog = false },
            onSave = { service, user, pass ->
                viewModel.addPassword(service, user, pass)
            }
        )
    }
}
