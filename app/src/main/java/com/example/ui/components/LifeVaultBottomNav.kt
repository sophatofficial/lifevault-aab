package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EnhancedEncryption
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.Screen

import com.example.utils.AppLanguage
import com.example.utils.AppThemeStyle
import com.example.utils.LanguageManager

@Composable
fun LifeVaultBottomNav(
    currentScreen: Screen,
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    currentTheme: AppThemeStyle = AppThemeStyle.CYBERPUNK_CYAN,
    onScreenSelected: (Screen) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(currentTheme.surfaceColor)
            .border(width = (0.5).dp, color = currentTheme.cardBorderColor)
            .navigationBarsPadding()
            .padding(vertical = 6.dp, horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                title = LanguageManager.getString("nav_notes", currentLanguage),
                icon = Icons.Default.Description,
                isSelected = currentScreen == Screen.NOTES || currentScreen == Screen.EDITOR,
                activeColor = currentTheme.primaryColor,
                onClick = { onScreenSelected(Screen.NOTES) }
            )
            NavItem(
                title = LanguageManager.getString("nav_vault", currentLanguage),
                icon = Icons.Default.EnhancedEncryption,
                isSelected = currentScreen == Screen.VAULT,
                activeColor = currentTheme.primaryColor,
                onClick = { onScreenSelected(Screen.VAULT) }
            )
            NavItem(
                title = LanguageManager.getString("nav_search", currentLanguage),
                icon = Icons.Default.Search,
                isSelected = currentScreen == Screen.SEARCH,
                activeColor = currentTheme.primaryColor,
                onClick = { onScreenSelected(Screen.SEARCH) }
            )
            NavItem(
                title = LanguageManager.getString("nav_graph", currentLanguage),
                icon = Icons.Default.Hub,
                isSelected = currentScreen == Screen.GRAPH,
                activeColor = currentTheme.primaryColor,
                onClick = { onScreenSelected(Screen.GRAPH) }
            )
            NavItem(
                title = LanguageManager.getString("nav_settings", currentLanguage),
                icon = Icons.Default.Settings,
                isSelected = currentScreen == Screen.SETTINGS,
                activeColor = currentTheme.primaryColor,
                onClick = { onScreenSelected(Screen.SETTINGS) }
            )
        }
    }
}

@Composable
private fun NavItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    activeColor: Color = Color(0xFF00DAF3),
    onClick: () -> Unit
) {
    val activeBg = if (isSelected) Color(0xFF3E4754) else Color.Transparent
    val contentColor = if (isSelected) activeColor else Color(0xFFBAC9CC)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(activeBg)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                color = contentColor,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
        }
    }
}
