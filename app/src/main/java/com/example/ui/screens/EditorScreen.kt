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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VaultItem
import com.example.ui.components.VoiceRecorderCard
import com.example.utils.PdfExporter

import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import com.example.utils.NoteLinkParser

enum class EditorMode {
    EDIT,
    PREVIEW,
    SPLIT
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditorScreen(
    item: VaultItem?,
    allItems: List<VaultItem> = emptyList(),
    onSaveItem: (VaultItem) -> Unit,
    onOpenNote: ((VaultItem) -> Unit)? = null,
    onBackClick: () -> Unit
) {
    var title by remember(item) { mutableStateOf(item?.title ?: "Theoretical Foundations of Quantum Entanglement") }
    var content by remember(item) {
        mutableStateOf(
            item?.content
                ?: "# Quantum Entanglement\nQuantum entanglement is a physical phenomenon that occurs when a group of particles interact in ways such that the quantum state of each particle cannot be described independently.\n\n### Key Principles\n> **Note:** Bell state calculation verified locally.\n\n- [x] Formulate Hamiltonian operator\n- [ ] Measure spin observables\n\n```kotlin\nval state = \"|ψ⟩ = 1/√2 (|00⟩ + |11⟩)\"\nprintln(state)\n```"
        )
    }
    var notebook by remember(item) { mutableStateOf(item?.notebook ?: "Research Lab") }
    var category by remember(item) { mutableStateOf(item?.category ?: "Notes") }
    var tags by remember(item) { mutableStateOf(item?.tags ?: "#RESEARCH, #SECURITY, #MARKDOWN") }
    var audioPath by remember(item) { mutableStateOf(item?.audioPath) }
    var editorMode by remember { mutableStateOf(EditorMode.EDIT) }
    var showSavedToast by remember { mutableStateOf(false) }
    var showLinkDropdown by remember { mutableStateOf(false) }
    var exportedPdfFileName by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Compute bi-directional links dynamically
    val outgoingTitles = remember(content) {
        NoteLinkParser.extractInternalLinks(content)
    }
    val backlinks = remember(allItems, title) {
        if (title.isBlank()) emptyList()
        else allItems.filter { other ->
            other.id != item?.id && NoteLinkParser.extractInternalLinks(other.content).any { it.equals(title, ignoreCase = true) }
        }
    }

    // Real-time word and character counts
    val wordCount = remember(content) {
        if (content.isBlank()) 0
        else content.trim().split(Regex("""\s+""")).filter { it.isNotBlank() }.size
    }
    val charCount = remember(content) {
        content.length
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF051424))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D1C2D))
                    .border(0.5.dp, Color(0xFF3B494C))
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFFD4E4FA)
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF273647))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = Color(0xFF00DAF3),
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "ROOM DB ENTITY",
                                            color = Color(0xFF00DAF3),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = Color(0xFFBAC9CC),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Saved",
                                    color = Color(0xFFBAC9CC),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    // Mode Toggle & Save Button
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // View Mode Toggle Segmented Control
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF162738))
                                .border(1.dp, Color(0xFF3B494C), RoundedCornerShape(8.dp))
                                .padding(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (editorMode == EditorMode.EDIT) Color(0xFF273647) else Color.Transparent)
                                    .clickable { editorMode = EditorMode.EDIT }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Mode",
                                        tint = if (editorMode == EditorMode.EDIT) Color(0xFF00DAF3) else Color(0xFFBAC9CC),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "EDIT",
                                        color = if (editorMode == EditorMode.EDIT) Color(0xFF00DAF3) else Color(0xFFBAC9CC),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (editorMode == EditorMode.PREVIEW) Color(0xFF273647) else Color.Transparent)
                                    .clickable { editorMode = EditorMode.PREVIEW }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = "Preview Mode",
                                        tint = if (editorMode == EditorMode.PREVIEW) Color(0xFF00DAF3) else Color(0xFFBAC9CC),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "PREVIEW",
                                        color = if (editorMode == EditorMode.PREVIEW) Color(0xFF00DAF3) else Color(0xFFBAC9CC),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        // Save to Room Entity Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF00DAF3))
                                .clickable {
                                    val current = item ?: VaultItem(
                                        title = title,
                                        content = content,
                                        category = category,
                                        notebook = notebook,
                                        tags = tags,
                                        audioPath = audioPath
                                    )
                                    onSaveItem(
                                        current.copy(
                                            title = title,
                                            content = content,
                                            category = category,
                                            notebook = notebook,
                                            tags = tags,
                                            audioPath = audioPath,
                                            updatedAt = System.currentTimeMillis()
                                        )
                                    )
                                    showSavedToast = true
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = null,
                                    tint = Color(0xFF00363D),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "SAVE",
                                    color = Color(0xFF00363D),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Export PDF Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E3A52))
                                .border(1.dp, Color(0xFF00DAF3), RoundedCornerShape(8.dp))
                                .clickable {
                                    val pdfFile = PdfExporter.exportNoteToPdf(
                                        context = context,
                                        title = title,
                                        content = content,
                                        category = category,
                                        tags = tags
                                    )
                                    if (pdfFile != null) {
                                        exportedPdfFileName = pdfFile.name
                                        PdfExporter.shareNotePdf(context, pdfFile)
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = "Export PDF",
                                    tint = Color(0xFF00DAF3),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "EXPORT PDF",
                                    color = Color(0xFF00DAF3),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            // Saved confirmation banner
            if (showSavedToast) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F3B40))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF00DAF3),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Successfully saved Note entity to Room SQLite database",
                                color = Color(0xFFD4E4FA),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = "DISMISS",
                            color = Color(0xFF00DAF3),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { showSavedToast = false }
                        )
                    }
                }
            }

            // Document Content Surface
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Voice Recording Component
                VoiceRecorderCard(
                    noteId = item?.id,
                    audioPath = audioPath,
                    onAudioSaved = { path ->
                        audioPath = path
                    }
                )

                // Bi-Directional Knowledge Graph Connection Panel
                if (outgoingTitles.isNotEmpty() || backlinks.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0B1929))
                            .border(1.dp, Color(0xFF00DAF3).copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Hub,
                                    contentDescription = null,
                                    tint = Color(0xFF00DAF3),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "BI-DIRECTIONAL GRAPH CONNECTIONS",
                                    color = Color(0xFFC3F5FF),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            if (outgoingTitles.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = null,
                                            tint = Color(0xFF00DAF3),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Outgoing Note Links (${outgoingTitles.size}):",
                                            color = Color(0xFF00DAF3),
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        outgoingTitles.forEach { targetTitle ->
                                            val targetItem = allItems.find { it.title.equals(targetTitle, ignoreCase = true) }
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0xFF102638))
                                                    .border(1.dp, Color(0xFF00DAF3).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                                    .clickable {
                                                        if (targetItem != null && onOpenNote != null) {
                                                            onOpenNote(targetItem)
                                                        }
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = "[[ $targetTitle ]]",
                                                    color = Color(0xFF00DAF3),
                                                    fontSize = 11.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            if (backlinks.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowBack,
                                            contentDescription = null,
                                            tint = Color(0xFF00E676),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Backlinks referencing this note (${backlinks.size}):",
                                            color = Color(0xFF00E676),
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        backlinks.forEach { backlinkItem ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0xFF0A2E20))
                                                    .border(1.dp, Color(0xFF00E676).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                                    .clickable {
                                                        if (onOpenNote != null) {
                                                            onOpenNote(backlinkItem)
                                                        }
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = "[[ ${backlinkItem.title} ]]",
                                                    color = Color(0xFF00E676),
                                                    fontSize = 11.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Category & Notebook Badges & Tags
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF00DAF3).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF00DAF3), RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "CATEGORY: ${category.uppercase()}",
                            color = Color(0xFF00DAF3),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF3E4754))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = notebook.uppercase(),
                            color = Color(0xFFD4E4FA),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    tags.split(",").filter { it.isNotBlank() }.forEach { tagStr ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF273647))
                                .border(1.dp, Color(0xFF3B494C), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = tagStr.trim(),
                                color = Color(0xFFBAC9CC),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Category & Tags Inline Editors
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CATEGORY",
                            color = Color(0xFFBAC9CC),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        BasicTextField(
                            value = category,
                            onValueChange = { category = it },
                            textStyle = TextStyle(
                                color = Color(0xFF00DAF3),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            cursorBrush = SolidColor(Color(0xFF00DAF3)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF162738), RoundedCornerShape(4.dp))
                                .border(1.dp, Color(0xFF273647), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(2f)) {
                        Text(
                            text = "TAGS (COMMA SEPARATED)",
                            color = Color(0xFFBAC9CC),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        BasicTextField(
                            value = tags,
                            onValueChange = { tags = it },
                            textStyle = TextStyle(
                                color = Color(0xFFD4E4FA),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            cursorBrush = SolidColor(Color(0xFF00DAF3)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF162738), RoundedCornerShape(4.dp))
                                .border(1.dp, Color(0xFF273647), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }

                // Title Input
                BasicTextField(
                    value = title,
                    onValueChange = { title = it },
                    textStyle = TextStyle(
                        color = Color(0xFFD4E4FA),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    cursorBrush = SolidColor(Color(0xFF00DAF3)),
                    modifier = Modifier.fillMaxWidth()
                )

                // Attachments Chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF122131))
                            .border(1.dp, Color(0xFF3B494C), RoundedCornerShape(16.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = Color(0xFF00DAF3),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Room Entity 'notes'",
                                color = Color(0xFFBAC9CC),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF122131))
                            .border(1.dp, Color(0xFF3B494C), RoundedCornerShape(16.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = null,
                                tint = Color(0xFF00DAF3),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "vault_diagram.svg",
                                color = Color(0xFFBAC9CC),
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF3B494C))
                )

                // Editor Content according to Mode
                when (editorMode) {
                    EditorMode.EDIT -> {
                        BasicTextField(
                            value = content,
                            onValueChange = { content = it },
                            textStyle = TextStyle(
                                color = Color(0xFFD4E4FA),
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            cursorBrush = SolidColor(Color(0xFF00DAF3)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(380.dp)
                        )
                    }

                    EditorMode.PREVIEW -> {
                        MarkdownPreview(
                            markdownContent = content,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    EditorMode.SPLIT -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "RAW MARKDOWN INPUT",
                                color = Color(0xFF00DAF3),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            BasicTextField(
                                value = content,
                                onValueChange = { content = it },
                                textStyle = TextStyle(
                                    color = Color(0xFFD4E4FA),
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                cursorBrush = SolidColor(Color(0xFF00DAF3)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .background(Color(0xFF0D1C2D), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFF273647), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            )
                            Text(
                                text = "RENDERED MARKDOWN PREVIEW",
                                color = Color(0xFF00DAF3),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            MarkdownPreview(
                                markdownContent = content,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }

            // Floating Markdown Formatting Toolbar & Status Indicator
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF122131))
                    .border(0.5.dp, Color(0xFF3B494C))
            ) {
                // Real-Time Word & Character Count Status Indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF091624))
                        .padding(horizontal = 14.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00DAF3))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "STATS",
                            color = Color(0xFFBAC9CC),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$wordCount ${if (wordCount == 1) "word" else "words"}",
                            color = Color(0xFF00DAF3),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "•",
                            color = Color(0xFF3B494C),
                            fontSize = 11.sp
                        )
                        Text(
                            text = "$charCount ${if (charCount == 1) "char" else "chars"}",
                            color = Color(0xFFC3F5FF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { content += " **Bold**" }) {
                        Icon(imageVector = Icons.Default.FormatBold, contentDescription = "Bold", tint = Color(0xFFBAC9CC))
                    }
                    IconButton(onClick = { content += " *Italic*" }) {
                        Icon(imageVector = Icons.Default.FormatItalic, contentDescription = "Italic", tint = Color(0xFFBAC9CC))
                    }
                    // Insert Note Link [[...]] button with auto-complete dropdown
                    Box {
                        IconButton(onClick = { showLinkDropdown = true }) {
                            Icon(imageVector = Icons.Default.Link, contentDescription = "Insert Note Link", tint = Color(0xFF00DAF3))
                        }
                        DropdownMenu(
                            expanded = showLinkDropdown,
                            onDismissRequest = { showLinkDropdown = false },
                            modifier = Modifier.background(Color(0xFF0D1C2D))
                        ) {
                            Text(
                                text = "INSERT INTERNAL LINK:",
                                color = Color(0xFF00DAF3),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                            val availableNotes = allItems.filter { it.id != item?.id }
                            if (availableNotes.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("[[ Note Title ]]", color = Color(0xFFBAC9CC)) },
                                    onClick = {
                                        content += " [[Note Title]]"
                                        showLinkDropdown = false
                                    }
                                )
                            } else {
                                availableNotes.forEach { noteItem ->
                                    DropdownMenuItem(
                                        text = { Text("[[ ${noteItem.title} ]]", color = Color(0xFFD4E4FA)) },
                                        onClick = {
                                            content += " [[${noteItem.title}]]"
                                            showLinkDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    IconButton(onClick = { content += "\n# Title" }) {
                        Icon(imageVector = Icons.Default.Title, contentDescription = "Heading", tint = Color(0xFFBAC9CC))
                    }
                    IconButton(onClick = { content += "\n- Bullet item" }) {
                        Icon(imageVector = Icons.Default.FormatListBulleted, contentDescription = "List", tint = Color(0xFFBAC9CC))
                    }
                    IconButton(onClick = { content += "\n- [ ] Task item" }) {
                        Icon(imageVector = Icons.Default.CheckBox, contentDescription = "Task Checkbox", tint = Color(0xFFBAC9CC))
                    }
                    IconButton(onClick = { content += "\n> Quote text" }) {
                        Icon(imageVector = Icons.Default.FormatQuote, contentDescription = "Quote", tint = Color(0xFFBAC9CC))
                    }
                    IconButton(onClick = { content += "\n```kotlin\n// Code snippet\n```" }) {
                        Icon(imageVector = Icons.Default.Code, contentDescription = "Code", tint = Color(0xFFBAC9CC))
                    }
                    IconButton(onClick = { content += "\n\n*AI Summary: Local zero-knowledge state verified.*" }) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI Assistant", tint = Color(0xFF00DAF3))
                    }
                }
            }
        }
    }
}

/**
 * Render Markdown content into Jetpack Compose components visually.
 */
@Composable
fun MarkdownPreview(
    markdownContent: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color(0xFF0A1828), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFF273647), RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val lines = markdownContent.split("\n")
        var inCodeBlock = false
        var codeBlockContent = StringBuilder()

        lines.forEach { line ->
            val trimmed = line.trim()

            if (trimmed.startsWith("```")) {
                if (inCodeBlock) {
                    // End code block
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF050E17), RoundedCornerShape(6.dp))
                            .border(1.dp, Color(0xFF1E3245), RoundedCornerShape(6.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = codeBlockContent.toString().trimEnd(),
                            color = Color(0xFF80FFAE),
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 18.sp
                        )
                    }
                    codeBlockContent = StringBuilder()
                    inCodeBlock = false
                } else {
                    inCodeBlock = true
                }
            } else if (inCodeBlock) {
                codeBlockContent.append(line).append("\n")
            } else when {
                trimmed.startsWith("# ") -> {
                    Text(
                        text = trimmed.removePrefix("# "),
                        color = Color(0xFF00DAF3),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                trimmed.startsWith("## ") -> {
                    Text(
                        text = trimmed.removePrefix("## "),
                        color = Color(0xFF90E0EF),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                    )
                }

                trimmed.startsWith("### ") -> {
                    Text(
                        text = trimmed.removePrefix("### "),
                        color = Color(0xFFBAC9CC),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                trimmed.startsWith("> ") -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF122334), RoundedCornerShape(4.dp))
                            .border(1.dp, Color(0xFF00DAF3), RoundedCornerShape(4.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(24.dp)
                                .background(Color(0xFF00DAF3))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = parseFormattedInline(trimmed.removePrefix("> ")),
                            color = Color(0xFFD4E4FA),
                            fontSize = 14.sp,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }

                trimmed.startsWith("- [x]") || trimmed.startsWith("- [X]") -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckBox,
                            contentDescription = "Checked",
                            tint = Color(0xFF00DAF3),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = parseFormattedInline(trimmed.substring(5).trim()),
                            color = Color(0xFF8B9DA3),
                            fontSize = 14.sp
                        )
                    }
                }

                trimmed.startsWith("- [ ]") -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckBoxOutlineBlank,
                            contentDescription = "Unchecked",
                            tint = Color(0xFFBAC9CC),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = parseFormattedInline(trimmed.substring(5).trim()),
                            color = Color(0xFFD4E4FA),
                            fontSize = 14.sp
                        )
                    }
                }

                trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00DAF3))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = parseFormattedInline(trimmed.substring(2).trim()),
                            color = Color(0xFFD4E4FA),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }

                trimmed.isBlank() -> {
                    Spacer(modifier = Modifier.height(4.dp))
                }

                else -> {
                    Text(
                        text = parseFormattedInline(line),
                        color = Color(0xFFD4E4FA),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

/**
 * Parses simple inline Markdown tokens like **bold**, *italic*, and `code`.
 */
private fun parseFormattedInline(text: String) = buildAnnotatedString {
    var index = 0
    val length = text.length

    while (index < length) {
        when {
            // Internal Link [[Note Title]]
            text.startsWith("[[", index) -> {
                val endIndex = text.indexOf("]]", index + 2)
                if (endIndex != -1) {
                    val linkTitle = text.substring(index + 2, endIndex).trim()
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00DAF3),
                            background = Color(0xFF0F2C3B),
                            fontFamily = FontFamily.Monospace
                        )
                    ) {
                        append(" 🔗 [[ $linkTitle ]] ")
                    }
                    index = endIndex + 2
                } else {
                    append(text[index])
                    index++
                }
            }

            // Bold **text**
            text.startsWith("**", index) -> {
                val endIndex = text.indexOf("**", index + 2)
                if (endIndex != -1) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFFFFFFF))) {
                        append(text.substring(index + 2, endIndex))
                    }
                    index = endIndex + 2
                } else {
                    append(text[index])
                    index++
                }
            }

            // Inline Code `code`
            text.startsWith("`", index) -> {
                val endIndex = text.indexOf("`", index + 1)
                if (endIndex != -1) {
                    withStyle(
                        SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            background = Color(0xFF1E2E3E),
                            color = Color(0xFF80FFAE)
                        )
                    ) {
                        append(" ${text.substring(index + 1, endIndex)} ")
                    }
                    index = endIndex + 1
                } else {
                    append(text[index])
                    index++
                }
            }

            // Italic *text*
            text.startsWith("*", index) && !text.startsWith("**", index) -> {
                val endIndex = text.indexOf("*", index + 1)
                if (endIndex != -1 && !text.startsWith("**", endIndex)) {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = Color(0xFFE2F1FF))) {
                        append(text.substring(index + 1, endIndex))
                    }
                    index = endIndex + 1
                } else {
                    append(text[index])
                    index++
                }
            }

            else -> {
                append(text[index])
                index++
            }
        }
    }
}

