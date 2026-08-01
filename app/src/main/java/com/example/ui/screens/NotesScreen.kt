package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VaultItem
import com.example.ui.Screen
import com.example.utils.PdfExporter
import com.example.utils.toFormattedDate

enum class NoteSortOption(val label: String) {
    DATE_NEWEST("Date: Newest"),
    DATE_OLDEST("Date: Oldest"),
    TITLE_AZ("Title: A-Z"),
    TITLE_ZA("Title: Z-A")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NotesScreen(
    items: List<VaultItem>,
    selectedNotebook: String,
    selectedTag: String,
    onNotebookSelect: (String) -> Unit,
    onTagSelect: (String) -> Unit,
    onItemClick: (VaultItem) -> Unit,
    onNewItemClick: () -> Unit,
    onScanClick: () -> Unit
) {
    val context = LocalContext.current
    var selectedSortOption by remember { mutableStateOf(NoteSortOption.DATE_NEWEST) }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    val sortedItems = remember(items, selectedSortOption) {
        when (selectedSortOption) {
            NoteSortOption.DATE_NEWEST -> items.sortedByDescending { it.updatedAt }
            NoteSortOption.DATE_OLDEST -> items.sortedBy { it.updatedAt }
            NoteSortOption.TITLE_AZ -> items.sortedBy { it.title.lowercase() }
            NoteSortOption.TITLE_ZA -> items.sortedByDescending { it.title.lowercase() }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF051424))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // System Status Banner
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SYSTEM STATUS",
                            color = Color(0xFF00DAF3),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Color(0xFF3B494C))
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Secure Workspace",
                                color = Color(0xFFD4E4FA),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF00DAF3))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Encrypted & Synced • 2.4 GB used",
                                    color = Color(0xFFBAC9CC),
                                    fontSize = 12.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFC3F5FF))
                                .clickable { onScanClick() }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "SCAN VAULT",
                                color = Color(0xFF00363D),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Notebooks Horizontal Filter Bar
            item {
                Column {
                    Text(
                        text = "NOTEBOOKS",
                        color = Color(0xFF00DAF3),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    val notebooks = listOf("All", "Research Lab", "Project Phoenix", "Personal Archive", "Deep Storage")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(notebooks) { nb ->
                            val isSelected = selectedNotebook.equals(nb, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFF3E4754) else Color(0xFF122131))
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFF00DAF3) else Color(0xFF3B494C),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onNotebookSelect(nb) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (nb == "Deep Storage") Icons.Default.Lock else if (isSelected) Icons.Default.FolderOpen else Icons.Default.Folder,
                                        contentDescription = null,
                                        tint = if (isSelected) Color(0xFF00DAF3) else Color(0xFFBAC9CC),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = nb,
                                        color = if (isSelected) Color(0xFFD4E4FA) else Color(0xFFBAC9CC),
                                        fontSize = 12.sp,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Tag Cloud
            item {
                Column {
                    Text(
                        text = "TAG CLOUD",
                        color = Color(0xFFBAC9CC),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    val tags = listOf("All", "#security", "#ai", "#vulnerability", "#privacy", "#development", "#backup")
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        tags.forEach { tag ->
                            val isSelected = selectedTag.equals(tag, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSelected) Color(0xFF00DAF3).copy(alpha = 0.2f) else Color(0xFF273647))
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFF00DAF3) else Color(0xFF3B494C),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .clickable { onTagSelect(tag) }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = tag,
                                    color = if (isSelected) Color(0xFF00DAF3) else Color(0xFFBAC9CC),
                                    fontSize = 10.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            // Quick Access / Pinned Bento Section
            item {
                Text(
                    text = "QUICK ACCESS",
                    color = Color(0xFFBAC9CC),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }

            val pinnedItems = sortedItems.filter { it.isPinned }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (pinnedItems.isEmpty()) {
                        Text(
                            text = "No pinned entries in this filter.",
                            color = Color(0xFF849396),
                            fontSize = 13.sp
                        )
                    } else {
                        pinnedItems.forEach { pinned ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF122131))
                                    .border(1.dp, Color(0xFF3B494C), RoundedCornerShape(12.dp))
                                    .clickable { onItemClick(pinned) }
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = pinned.title,
                                                color = Color(0xFFD4E4FA),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = pinned.content,
                                            color = Color(0xFFBAC9CC),
                                            fontSize = 13.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color(0xFF3E4754))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = pinned.notebook.uppercase(),
                                                    color = Color(0xFF00DAF3),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                                )
                                            }
                                            Text(
                                                text = pinned.tags,
                                                color = Color(0xFF849396),
                                                fontSize = 10.sp,
                                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                            )
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.Default.PushPin,
                                        contentDescription = "Pinned",
                                        tint = Color(0xFF00DAF3),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Recent Notes Section with Dropdown Sort Menu
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT NOTES",
                        color = Color(0xFFBAC9CC),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF162738))
                                    .border(1.dp, Color(0xFF3B494C), RoundedCornerShape(6.dp))
                                    .clickable { sortMenuExpanded = true }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sort,
                                    contentDescription = "Sort Notes",
                                    tint = Color(0xFF00DAF3),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = selectedSortOption.label,
                                    color = Color(0xFF00DAF3),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = Color(0xFF00DAF3),
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = sortMenuExpanded,
                                onDismissRequest = { sortMenuExpanded = false },
                                modifier = Modifier.background(Color(0xFF122131))
                            ) {
                                NoteSortOption.values().forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = option.label,
                                                color = if (selectedSortOption == option) Color(0xFF00DAF3) else Color(0xFFD4E4FA),
                                                fontSize = 12.sp,
                                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                            )
                                        },
                                        onClick = {
                                            selectedSortOption = option
                                            sortMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Total: ${items.size}",
                            color = Color(0xFF00DAF3),
                            fontSize = 11.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
            }

            val recentItems = sortedItems.filter { !it.isPinned }
            items(recentItems) { note ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0D1C2D))
                        .border(1.dp, Color(0xFF3B494C).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .clickable { onItemClick(note) }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF273647)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (note.category) {
                                    "Development" -> Icons.Default.Code
                                    "Operations" -> Icons.Default.Shield
                                    else -> Icons.Default.Description
                                },
                                contentDescription = null,
                                tint = Color(0xFFBAC9CC),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = note.title,
                                    color = Color(0xFFD4E4FA),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                if (note.audioPath != null) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Voice Memo Attached",
                                        tint = Color(0xFF00DAF3),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${note.updatedAt.toFormattedDate()} • In: ${note.notebook} • ${note.tags}",
                                color = Color(0xFFBAC9CC),
                                fontSize = 11.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                        IconButton(
                            onClick = {
                                val pdfFile = PdfExporter.exportNoteToPdf(
                                    context = context,
                                    title = note.title,
                                    content = note.content,
                                    category = note.category,
                                    tags = note.tags,
                                    timestamp = note.updatedAt
                                )
                                if (pdfFile != null) {
                                    PdfExporter.shareNotePdf(context, pdfFile)
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = "Export PDF",
                                tint = Color(0xFF00DAF3),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Footer Shield Visual
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF010F1F))
                        .border(1.dp, Color(0xFF3B494C).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = Color(0xFF00DAF3).copy(alpha = 0.5f),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "END-TO-END ENCRYPTION ACTIVE",
                            color = Color(0xFFBAC9CC),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        // FAB
        FloatingActionButton(
            onClick = onNewItemClick,
            containerColor = Color(0xFF00E5FF),
            contentColor = Color(0xFF00626E),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Create Entry")
        }
    }
}
