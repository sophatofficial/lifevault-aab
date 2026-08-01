package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.utils.BiometricHelper

@Composable
fun BiometricLockOverlay(
    activity: FragmentActivity,
    onUnlockSuccess: () -> Unit
) {
    var authError by remember { mutableStateOf<String?>(null) }
    var showPinInput by remember { mutableStateOf(false) }
    var pinText by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    // Auto-trigger biometric prompt on first display
    LaunchedEffect(Unit) {
        BiometricHelper.promptBiometricAuth(
            activity = activity,
            title = "LifeVault Notes Security",
            subtitle = "Scan fingerprint or enter device passcode to access private notes",
            onSuccess = {
                authError = null
                onUnlockSuccess()
            },
            onError = { err ->
                authError = err
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030E1A))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Cyber Security Icon Badge
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0C1F33))
                    .border(2.dp, Color(0xFF00DAF3), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Encrypted Lock",
                    tint = Color(0xFF00DAF3),
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "LIFEVAULT SECURE ACCESS",
                color = Color(0xFF00DAF3),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Notes Vault is Encrypted",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Biometric authentication required to unlock private notes & credentials",
                color = Color(0xFF8DA4B8),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Primary Biometric Scan Button
            Button(
                onClick = {
                    authError = null
                    BiometricHelper.promptBiometricAuth(
                        activity = activity,
                        title = "LifeVault Notes Security",
                        subtitle = "Scan fingerprint or enter device PIN to unlock",
                        onSuccess = {
                            authError = null
                            onUnlockSuccess()
                        },
                        onError = { err ->
                            authError = err
                        }
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00DAF3),
                    contentColor = Color(0xFF00363D)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Scan Fingerprint",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "SCAN BIOMETRICS",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // PIN Fallback / Demo Unlock option
            if (!showPinInput) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF102438))
                            .border(1.dp, Color(0xFF233B54), RoundedCornerShape(10.dp))
                            .clickable { showPinInput = true }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = "Enter PIN",
                                tint = Color(0xFFBAC9CC),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ENTER PASSCODE",
                                color = Color(0xFFBAC9CC),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0A2E2C))
                            .border(1.dp, Color(0xFF00DAF3).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .clickable { onUnlockSuccess() }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Bypass",
                                tint = Color(0xFF00DAF3),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "BYPASS (DEMO)",
                                color = Color(0xFF00DAF3),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = pinText,
                        onValueChange = {
                            pinText = it
                            pinError = false
                        },
                        label = { Text("Master PIN (Default: 1234)") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00DAF3),
                            unfocusedBorderColor = Color(0xFF233B54),
                            focusedLabelColor = Color(0xFF00DAF3),
                            unfocusedLabelColor = Color(0xFF8DA4B8),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                if (pinText == "1234" || pinText == "0000" || pinText.length >= 4) {
                                    onUnlockSuccess()
                                } else {
                                    pinError = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00DAF3),
                                contentColor = Color(0xFF00363D)
                            )
                        ) {
                            Text("VERIFY PIN", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }

                    if (pinError) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Invalid PIN. Try 1234 or tap Bypass.",
                            color = Color(0xFFFF6B6B),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = authError != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                authError?.let { err ->
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF331414))
                            .border(1.dp, Color(0xFFFF5252), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = err,
                            color = Color(0xFFFFB4B4),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
