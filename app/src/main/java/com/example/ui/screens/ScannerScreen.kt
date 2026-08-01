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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FilterCenterFocus
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScannerScreen(
    onScanComplete: (title: String, extractedText: String) -> Unit,
    onBackClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000D1A))
    ) {
        // High-Tech Viewport Area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 140.dp, top = 60.dp)
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF071828))
                .border(2.dp, Color(0xFF00DAF3), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Viewport Grid Canvas & Animated Laser Line
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Grid lines
                val cols = 4
                val rows = 6
                for (i in 1 until cols) {
                    val x = width * i / cols
                    drawLine(
                        color = Color(0xFF00DAF3).copy(alpha = 0.15f),
                        start = Offset(x, 0f),
                        end = Offset(x, height),
                        strokeWidth = 1f
                    )
                }
                for (j in 1 until rows) {
                    val y = height * j / rows
                    drawLine(
                        color = Color(0xFF00DAF3).copy(alpha = 0.15f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1f
                    )
                }

                // Laser Scan Line
                val currentLaserY = height * laserY
                drawLine(
                    color = Color(0xFF00E5FF),
                    start = Offset(0f, currentLaserY),
                    end = Offset(width, currentLaserY),
                    strokeWidth = 4f
                )
            }

            // OCR Bounding Box Overlay Card (Simulated receipt scan target)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(300.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF051424).copy(alpha = 0.85f))
                    .border(1.5.dp, Color(0xFF00DAF3), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "[OCR SCAN TARGET]",
                            color = Color(0xFF00DAF3),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF00DAF3).copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "98.4% CONFIDENCE",
                                color = Color(0xFF00DAF3),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFF3B494C))
                    )

                    // Detected Box 1: Vendor
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF122131))
                            .border(1.dp, Color(0xFF00DAF3).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Vendor: Cafe L'Aube",
                            color = Color(0xFFD4E4FA),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "EXTRACTED",
                            color = Color(0xFF00DAF3),
                            fontSize = 10.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }

                    // Detected Box 2: Total
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF122131))
                            .border(1.dp, Color(0xFF00DAF3).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total Amount: $42.50",
                            color = Color(0xFFD4E4FA),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "EXTRACTED",
                            color = Color(0xFF00DAF3),
                            fontSize = 10.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }

                    // Detected Box 3: Date
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF122131))
                            .border(1.dp, Color(0xFF00DAF3).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Date: 2026-07-31",
                            color = Color(0xFFD4E4FA),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "EXTRACTED",
                            color = Color(0xFF00DAF3),
                            fontSize = 10.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
            }

            // Corner Target Crosshairs
            Icon(
                imageVector = Icons.Default.FilterCenterFocus,
                contentDescription = null,
                tint = Color(0xFF00DAF3),
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
            )
        }

        // Header controls overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFF051424).copy(alpha = 0.8f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFFD4E4FA)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF051424).copy(alpha = 0.85f))
                    .border(1.dp, Color(0xFF00DAF3), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF00DAF3),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LOCAL OCR ENGINE ACTIVE",
                        color = Color(0xFF00DAF3),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            }
        }

        // Bottom Controls Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .align(Alignment.BottomCenter)
                .background(Color(0xFF051424))
                .border(0.5.dp, Color(0xFF3B494C))
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Secondary action: Flash
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF122131))
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = "Flash",
                        tint = Color(0xFF00DAF3)
                    )
                }

                // Main Capture Button
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00DAF3))
                        .clickable {
                            onScanComplete(
                                "Scanned Receipt - Cafe L'Aube",
                                "Scanned Document via LifeVault Local OCR Engine:\n\nVendor: Cafe L'Aube\nTotal: $42.50\nDate: 2026-07-31\n\nItems:\n• Espresso Macchiato x2 ($12.00)\n• Organic Avocado Toast ($18.50)\n• Cold Brew Special ($12.00)\n\nVerification: Verified Local Signature 0x94FA"
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .border(3.dp, Color(0xFF00363D), CircleShape)
                    )
                }

                // Secondary action: Auto Crop / Multi Page
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF122131))
                ) {
                    Icon(
                        imageVector = Icons.Default.Crop,
                        contentDescription = "Crop",
                        tint = Color(0xFF00DAF3)
                    )
                }
            }
        }
    }
}
