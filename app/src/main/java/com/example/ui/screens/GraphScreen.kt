package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VaultItem
import com.example.utils.GraphConnectionNode
import com.example.utils.NoteLinkParser

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GraphScreen(
    items: List<VaultItem>,
    onNodeItemClick: (VaultItem) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    
    // Parse bi-directional graph nodes from Room VaultItem note links
    val allGraphNodes = remember(items) {
        NoteLinkParser.buildKnowledgeGraph(items)
    }

    var selectedNodeId by remember(allGraphNodes) {
        mutableStateOf(allGraphNodes.firstOrNull()?.id ?: 0L)
    }

    val filteredNodes = remember(allGraphNodes, selectedCategory) {
        if (selectedCategory == "All") {
            allGraphNodes
        } else {
            allGraphNodes.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }
    }

    val activeNode = filteredNodes.find { it.id == selectedNodeId }
        ?: allGraphNodes.find { it.id == selectedNodeId }
        ?: filteredNodes.firstOrNull()
        ?: allGraphNodes.firstOrNull()

    val totalConnections = remember(allGraphNodes) {
        allGraphNodes.sumOf { it.connectedTitles.size } / 2
    }

    val categories = remember(items) {
        listOf("All") + items.map { it.category }.distinct().sorted()
    }

    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF051424))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header controls
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D1C2D))
                    .border(0.5.dp, Color(0xFF3B494C))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Hub,
                            contentDescription = null,
                            tint = Color(0xFF00DAF3),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "BI-DIRECTIONAL KNOWLEDGE GRAPH",
                                color = Color(0xFFC3F5FF),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "${allGraphNodes.size} Notes • $totalConnections Bi-Directional Links [[...]]",
                                color = Color(0xFFBAC9CC),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF00DAF3).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFF00DAF3).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ROOM DB INDEXED",
                            color = Color(0xFF00DAF3),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Filter Row
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) Color(0xFF3E4754) else Color(0xFF122131))
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0xFF00DAF3) else Color(0xFF3B494C),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) Color(0xFF00DAF3) else Color(0xFFBAC9CC),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Interactive Canvas Area
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF010F1F))
                    .border(1.dp, Color(0xFF3B494C), RoundedCornerShape(16.dp))
            ) {
                val containerWidthDp = maxWidth
                val containerHeightDp = maxHeight

                val nodeMapByTitle = remember(allGraphNodes) {
                    allGraphNodes.associateBy { it.title.lowercase() }
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    val posMap = filteredNodes.associate { node ->
                        node.id to Offset(node.xRatio * width, node.yRatio * height)
                    }

                    // Draw connection lines for bi-directional links
                    filteredNodes.forEach { node ->
                        val startPos = posMap[node.id] ?: return@forEach
                        node.connectedTitles.forEach { connectedTitle ->
                            val targetNode = nodeMapByTitle[connectedTitle.lowercase()] ?: return@forEach
                            val endPos = posMap[targetNode.id] ?: return@forEach

                            val isHighlighted = (node.id == activeNode?.id || targetNode.id == activeNode?.id)
                            drawLine(
                                color = if (isHighlighted)
                                    Color(0xFF00DAF3).copy(alpha = pulseAlpha)
                                else
                                    Color(0xFF3B494C).copy(alpha = 0.4f),
                                start = startPos,
                                end = endPos,
                                strokeWidth = if (isHighlighted) 3.5f else 1.5f
                            )
                        }
                    }

                    // Draw node circles
                    filteredNodes.forEach { node ->
                        val pos = posMap[node.id] ?: return@forEach
                        val isSelected = activeNode?.id == node.id
                        val isConnectedToSelected = activeNode != null &&
                                activeNode.connectedTitles.any { it.equals(node.title, ignoreCase = true) }

                        val nodeColor = when {
                            isSelected -> Color(0xFF00DAF3)
                            isConnectedToSelected -> Color(0xFF00E676)
                            node.category.equals("Audit", ignoreCase = true) -> Color(0xFFFFB74D)
                            node.category.equals("Inventory", ignoreCase = true) -> Color(0xFFFF4081)
                            else -> Color(0xFF7C4DFF)
                        }

                        if (isSelected) {
                            drawCircle(
                                color = nodeColor.copy(alpha = 0.25f),
                                radius = 26.dp.toPx(),
                                center = pos
                            )
                        }

                        drawCircle(
                            color = nodeColor,
                            radius = if (isSelected) 12.dp.toPx() else 8.dp.toPx(),
                            center = pos
                        )
                    }
                }

                // Overlay Clickable Buttons for Nodes dynamically aligned with Canvas
                filteredNodes.forEach { node ->
                    val isSelected = activeNode?.id == node.id
                    val isConnected = activeNode != null &&
                            activeNode.connectedTitles.any { it.equals(node.title, ignoreCase = true) }

                    val xOffset = containerWidthDp * node.xRatio
                    val yOffset = containerHeightDp * node.yRatio

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(
                                start = (xOffset - 24.dp).coerceAtLeast(4.dp),
                                top = (yOffset + 12.dp).coerceAtLeast(4.dp)
                            )
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when {
                                    isSelected -> Color(0xFF00DAF3)
                                    isConnected -> Color(0xFF00E676)
                                    else -> Color(0xFF122131)
                                }
                            )
                            .clickable { selectedNodeId = node.id }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = node.title,
                            color = if (isSelected || isConnected) Color(0xFF002227) else Color(0xFFD4E4FA),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                    }
                }
            }

            // Bottom Selected Node Details Panel
            if (activeNode != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0D1C2D))
                        .border(0.5.dp, Color(0xFF3B494C))
                        .padding(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF00DAF3))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = activeNode.title,
                                    color = Color(0xFF00DAF3),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            activeNode.vaultItem?.let { item ->
                                Button(
                                    onClick = { onNodeItemClick(item) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00DAF3),
                                        contentColor = Color(0xFF002227)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.OpenInNew,
                                        contentDescription = "Open Note",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "OPEN NOTE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Category: ${activeNode.category}",
                                color = Color(0xFFBAC9CC),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Backlinks: ${activeNode.backlinks.size}",
                                color = Color(0xFF00E676),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Outgoing: ${activeNode.outgoingLinks.size}",
                                color = Color(0xFFC3F5FF),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Backlinks Section (Other notes linking TO this note)
                        if (activeNode.backlinks.isNotEmpty()) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = null,
                                        tint = Color(0xFF00E676),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "BACKLINKS (Referenced By):",
                                        color = Color(0xFF00E676),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    activeNode.backlinks.forEach { linkTitle ->
                                        val targetNode = allGraphNodes.find { it.title.equals(linkTitle, ignoreCase = true) }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFF0A2E20))
                                                .border(1.dp, Color(0xFF00E676).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                                .clickable {
                                                    targetNode?.let { selectedNodeId = it.id }
                                                }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "[[ $linkTitle ]]",
                                                color = Color(0xFF00E676),
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Outgoing Links Section (Internal links inside this note)
                        if (activeNode.outgoingLinks.isNotEmpty()) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = Color(0xFF00DAF3),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "OUTGOING INTERNAL LINKS:",
                                        color = Color(0xFF00DAF3),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    activeNode.outgoingLinks.forEach { linkTitle ->
                                        val targetNode = allGraphNodes.find { it.title.equals(linkTitle, ignoreCase = true) }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFF102638))
                                                .border(1.dp, Color(0xFF00DAF3).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                                .clickable {
                                                    targetNode?.let { selectedNodeId = it.id }
                                                }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "[[ $linkTitle ]]",
                                                color = Color(0xFF00DAF3),
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

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

