package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PdfReaderScreen(
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF051424))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // PDF Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D1C2D))
                    .border(0.5.dp, Color(0xFF3B494C))
                    .padding(8.dp)
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
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = Color(0xFF00DAF3),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Q4 Security Audit.pdf",
                                color = Color(0xFFD4E4FA),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Page 1 of 12 • 2.4 MB • Encrypted Stream",
                                color = Color(0xFFBAC9CC),
                                fontSize = 11.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF00DAF3).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF00DAF3), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "OCR ACTIVE",
                            color = Color(0xFF00DAF3),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
            }

            // Document Canvas Viewer
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Page 1 Viewport Paper Surface
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0D1C2D))
                        .border(1.dp, Color(0xFF3B494C), RoundedCornerShape(8.dp))
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "STRICTLY CONFIDENTIAL",
                                color = Color(0xFFFFB4AB),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                letterSpacing = 2.sp
                            )
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFF00DAF3),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = "SYSTEM ARCHITECTURE AUDIT REPORT",
                            color = Color(0xFFC3F5FF),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Executive Summary:\nThe localized data isolation engine prevents metadata leakages across client boundaries. Key vectors remain resident within zero-knowledge hardware enclaves.",
                            color = Color(0xFFD4E4FA),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )

                        // Highlighted OCR Section
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF00DAF3).copy(alpha = 0.15f))
                                .border(1.dp, Color(0xFF00DAF3), RoundedCornerShape(6.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFF00DAF3),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ANNOTATED AUDIT FINDING #402",
                                        color = Color(0xFF00DAF3),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "\"AES-256 localized keys stored in HSE hardware element pass all zero-knowledge entropy tests without remote key transmission.\"",
                                    color = Color(0xFFD4E4FA),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Diagram Vector Simulation
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF010F1F))
                                .border(1.dp, Color(0xFF3B494C), RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawLine(
                                    color = Color(0xFF00DAF3),
                                    start = Offset(50f, 60f),
                                    end = Offset(size.width - 50f, 60f),
                                    strokeWidth = 3f
                                )
                                drawCircle(
                                    color = Color(0xFF00DAF3),
                                    radius = 12f,
                                    center = Offset(50f, 60f)
                                )
                                drawCircle(
                                    color = Color(0xFFC8D0EA),
                                    radius = 12f,
                                    center = Offset(size.width / 2, 60f)
                                )
                                drawCircle(
                                    color = Color(0xFF00DAF3),
                                    radius = 12f,
                                    center = Offset(size.width - 50f, 60f)
                                )
                            }
                            Text(
                                text = "LOCAL ZERO-KNOWLEDGE PIPELINE",
                                color = Color(0xFFBAC9CC),
                                fontSize = 10.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Annotation Bar Bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF122131))
                    .border(0.5.dp, Color(0xFF3B494C))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Default.SelectAll, contentDescription = "Select", tint = Color(0xFF00DAF3))
                    }
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Default.Highlight, contentDescription = "Highlight", tint = Color(0xFFBAC9CC))
                    }
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Default.Draw, contentDescription = "Pen", tint = Color(0xFFBAC9CC))
                    }
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Default.Comment, contentDescription = "Comment", tint = Color(0xFFBAC9CC))
                    }
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Default.TextFields, contentDescription = "Text", tint = Color(0xFFBAC9CC))
                    }
                }
            }
        }
    }
}
