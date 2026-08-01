package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.utils.AppThemeStyle

@Composable
fun LifeVaultTheme(
    themeStyle: AppThemeStyle = AppThemeStyle.CYBERPUNK_CYAN,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = themeStyle.toColorScheme(),
        typography = Typography,
        content = content
    )
}

// Retain alias for compatibility if referenced
@Composable
fun MyApplicationTheme(content: @Composable () -> Unit) {
    LifeVaultTheme(content = content)
}
