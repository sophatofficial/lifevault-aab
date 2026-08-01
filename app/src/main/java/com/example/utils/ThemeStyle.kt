package com.example.utils

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

enum class AppThemeStyle(
    val titleKey: String,
    val description: String,
    val primaryColor: Color,
    val backgroundColor: Color,
    val surfaceColor: Color,
    val cardBorderColor: Color
) {
    CYBERPUNK_CYAN(
        titleKey = "theme_cyberpunk",
        description = "Deep space obsidian canvas with neon cyan accents",
        primaryColor = Color(0xFF00DAF3),
        backgroundColor = Color(0xFF051424),
        surfaceColor = Color(0xFF122131),
        cardBorderColor = Color(0xFF273647)
    ),
    VELVET_MIDNIGHT(
        titleKey = "theme_midnight",
        description = "Rich midnight violet canvas with amethyst glow",
        primaryColor = Color(0xFFA78BFA),
        backgroundColor = Color(0xFF0B091A),
        surfaceColor = Color(0xFF181530),
        cardBorderColor = Color(0xFF2E2750)
    ),
    EMERALD_MATRIX(
        titleKey = "theme_emerald",
        description = "Matrix dark theme with futuristic emerald accents",
        primaryColor = Color(0xFF10B981),
        backgroundColor = Color(0xFF03140E),
        surfaceColor = Color(0xFF0B241C),
        cardBorderColor = Color(0xFF163E30)
    ),
    SUNSET_AMBER(
        titleKey = "theme_amber",
        description = "Warm dark obsidian canvas with sunset amber highlights",
        primaryColor = Color(0xFFF59E0B),
        backgroundColor = Color(0xFF170F04),
        surfaceColor = Color(0xFF291D0D),
        cardBorderColor = Color(0xFF453118)
    ),
    OLED_HIGH_CONTRAST(
        titleKey = "theme_oled",
        description = "Pure pitch black OLED canvas with high contrast UI",
        primaryColor = Color(0xFF38BDF8),
        backgroundColor = Color(0xFF000000),
        surfaceColor = Color(0xFF121212),
        cardBorderColor = Color(0xFF282828)
    );

    fun toColorScheme(): ColorScheme {
        return darkColorScheme(
            primary = primaryColor,
            onPrimary = Color(0xFF002026),
            primaryContainer = primaryColor.copy(alpha = 0.2f),
            onPrimaryContainer = primaryColor,
            secondary = primaryColor.copy(alpha = 0.8f),
            background = backgroundColor,
            onBackground = Color(0xFFD4E4FA),
            surface = surfaceColor,
            onSurface = Color(0xFFE2E8F0),
            surfaceVariant = cardBorderColor,
            onSurfaceVariant = Color(0xFF94A3B8),
            outline = primaryColor.copy(alpha = 0.5f)
        )
    }
}
