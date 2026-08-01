package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import com.example.data.Note
import com.example.data.PasswordEntry
import com.example.data.VaultItem
import com.example.utils.AppLanguage
import com.example.utils.AppThemeStyle
import com.example.utils.DatabaseBackupExporter
import com.example.utils.LanguageManager

@Composable
fun SettingsScreen(
    vaultItems: List<VaultItem> = emptyList(),
    passwords: List<PasswordEntry> = emptyList(),
    notes: List<Note> = emptyList(),
    currentTheme: AppThemeStyle = AppThemeStyle.CYBERPUNK_CYAN,
    onThemeSelect: (AppThemeStyle) -> Unit = {},
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    onLanguageSelect: (AppLanguage) -> Unit = {},
    onUpgradeClick: () -> Unit
) {
    val context = LocalContext.current
    var lastBackupStatus by remember { mutableStateOf<String?>(null) }
    var isBiometricEnabled by remember { mutableStateOf(true) }
    var isAutoLockEnabled by remember { mutableStateOf(true) }
    var isOfflineStrict by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF051424))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Title Header
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = currentTheme.primaryColor,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = LanguageManager.getString("settings_title", currentLanguage),
                            color = Color(0xFFD4E4FA),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "LifeVault v4.2.0 • Zero-Knowledge Architecture",
                            color = Color(0xFFBAC9CC),
                            fontSize = 12.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
            }

            // Group: Visual Theme Style Selection
            item {
                Text(
                    text = LanguageManager.getString("theme_style_title", currentLanguage).uppercase(),
                    color = Color(0xFFBAC9CC),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(currentTheme.surfaceColor)
                        .border(1.dp, currentTheme.cardBorderColor, RoundedCornerShape(12.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppThemeStyle.values().forEach { theme ->
                        val isSelected = theme == currentTheme
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) theme.primaryColor.copy(alpha = 0.15f) else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (isSelected) theme.primaryColor else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onThemeSelect(theme) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Theme Color Palette Indicator
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(theme.backgroundColor)
                                    .border(2.dp, theme.primaryColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(theme.primaryColor)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = LanguageManager.getString(theme.titleKey, currentLanguage),
                                    color = if (isSelected) theme.primaryColor else Color(0xFFD4E4FA),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = theme.description,
                                    color = Color(0xFFBAC9CC),
                                    fontSize = 11.sp
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Active Theme",
                                    tint = theme.primaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Group: Language Selection
            item {
                Text(
                    text = LanguageManager.getString("language_title", currentLanguage).uppercase(),
                    color = Color(0xFFBAC9CC),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(currentTheme.surfaceColor)
                        .border(1.dp, currentTheme.cardBorderColor, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppLanguage.values().forEach { lang ->
                        val isSelected = lang == currentLanguage
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) currentTheme.primaryColor.copy(alpha = 0.2f) else currentTheme.backgroundColor)
                                .border(
                                    1.dp,
                                    if (isSelected) currentTheme.primaryColor else Color(0xFF233B54),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onLanguageSelect(lang) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = lang.flag,
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = lang.displayName,
                                    color = if (isSelected) currentTheme.primaryColor else Color(0xFFBAC9CC),
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            // Pro Upgrade Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0D1C2D))
                        .border(1.dp, Color(0xFF00DAF3), RoundedCornerShape(16.dp))
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFF00DAF3),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "LifeVault Pro",
                                    color = Color(0xFFC3F5FF),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Remove ads, unlock unlimited OCR document scans & encrypted multi-device local sync.",
                                color = Color(0xFFBAC9CC),
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF00DAF3))
                                .clickable { onUpgradeClick() }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "UPGRADE",
                                color = Color(0xFF00363D),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Group 1: Encryption & Auth
            item {
                Text(
                    text = "SECURITY & BIOMETRICS",
                    color = Color(0xFFBAC9CC),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF122131))
                        .border(1.dp, Color(0xFF3B494C), RoundedCornerShape(12.dp))
                ) {
                    // Item 1: Biometric Unlock
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = null,
                                tint = Color(0xFF00DAF3),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Biometric / PIN Unlock",
                                    color = Color(0xFFD4E4FA),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Require fingerprint or PIN upon launch",
                                    color = Color(0xFFBAC9CC),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Switch(
                            checked = isBiometricEnabled,
                            onCheckedChange = { isBiometricEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF00363D),
                                checkedTrackColor = Color(0xFF00DAF3)
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFF3B494C))
                    )

                    // Item 2: Auto Lock Timer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFF00DAF3),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Instant Auto-Lock",
                                    color = Color(0xFFD4E4FA),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Lock vault immediately when app is backgrounded",
                                    color = Color(0xFFBAC9CC),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Switch(
                            checked = isAutoLockEnabled,
                            onCheckedChange = { isAutoLockEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF00363D),
                                checkedTrackColor = Color(0xFF00DAF3)
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFF3B494C))
                    )

                    // Item 3: Strict Offline Mode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PrivacyTip,
                                contentDescription = null,
                                tint = Color(0xFF00DAF3),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Strict Offline Isolation",
                                    color = Color(0xFFD4E4FA),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Disable all network access for maximum privacy",
                                    color = Color(0xFFBAC9CC),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Switch(
                            checked = isOfflineStrict,
                            onCheckedChange = { isOfflineStrict = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF00363D),
                                checkedTrackColor = Color(0xFF00DAF3)
                            )
                        )
                    }
                }
            }

            // Group 2: Backup & Data Export
            item {
                Text(
                    text = "BACKUP & DATA MANAGEMENT",
                    color = Color(0xFFBAC9CC),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF122131))
                        .border(1.dp, Color(0xFF3B494C), RoundedCornerShape(12.dp))
                ) {
                    // Create Local Encrypted Snapshot (JSON Database Backup)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val file = DatabaseBackupExporter.exportDatabaseToJson(
                                    context = context,
                                    vaultItems = vaultItems,
                                    passwords = passwords,
                                    notes = notes
                                )
                                if (file != null) {
                                    lastBackupStatus = "Backup saved to local storage: ${file.name}"
                                    DatabaseBackupExporter.shareBackupFile(context, file)
                                } else {
                                    lastBackupStatus = "Error generating database backup JSON file"
                                }
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Backup,
                            contentDescription = null,
                            tint = Color(0xFF00DAF3),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Export Database as JSON Backup",
                                color = Color(0xFFD4E4FA),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Export full Room DB (${vaultItems.size} items, ${notes.size} notes) to local JSON",
                                color = Color(0xFFBAC9CC),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFF3B494C))
                    )

                    // Restore Snapshot
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restore,
                            contentDescription = null,
                            tint = Color(0xFFBAC9CC),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Restore from Local File",
                                color = Color(0xFFD4E4FA),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Import and decrypt previously saved .vault snapshot",
                                color = Color(0xFFBAC9CC),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            if (lastBackupStatus != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF092834))
                            .border(1.dp, Color(0xFF00DAF3), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = lastBackupStatus!!,
                            color = Color(0xFF00DAF3),
                            fontSize = 11.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
            }

            // Info Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF010F1F))
                        .border(1.dp, Color(0xFF3B494C).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LifeVault Personal Knowledge Hub • Built for Privacy\nAll data is stored locally in SQLite with AES-256 encryption.",
                        color = Color(0xFF849396),
                        fontSize = 11.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
