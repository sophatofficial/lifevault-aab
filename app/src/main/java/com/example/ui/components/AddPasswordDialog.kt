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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun AddPasswordDialog(
    onDismiss: () -> Unit,
    onSave: (service: String, username: String, pass: String) -> Unit
) {
    var service by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0D1C2D))
                .border(1.dp, Color(0xFF00DAF3), RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "ADD CREDENTIAL TO VAULT",
                    color = Color(0xFF00DAF3),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )

                Column {
                    Text(
                        text = "Service / Website",
                        color = Color(0xFFBAC9CC),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF122131))
                            .border(1.dp, Color(0xFF3B494C), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        BasicTextField(
                            value = service,
                            onValueChange = { service = it },
                            textStyle = TextStyle(color = Color(0xFFD4E4FA), fontSize = 14.sp),
                            cursorBrush = SolidColor(Color(0xFF00DAF3)),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Column {
                    Text(
                        text = "Username / Email",
                        color = Color(0xFFBAC9CC),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF122131))
                            .border(1.dp, Color(0xFF3B494C), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        BasicTextField(
                            value = username,
                            onValueChange = { username = it },
                            textStyle = TextStyle(color = Color(0xFFD4E4FA), fontSize = 14.sp),
                            cursorBrush = SolidColor(Color(0xFF00DAF3)),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Column {
                    Text(
                        text = "Password",
                        color = Color(0xFFBAC9CC),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF122131))
                            .border(1.dp, Color(0xFF3B494C), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        BasicTextField(
                            value = password,
                            onValueChange = { password = it },
                            textStyle = TextStyle(color = Color(0xFFD4E4FA), fontSize = 14.sp),
                            cursorBrush = SolidColor(Color(0xFF00DAF3)),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onDismiss() }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "CANCEL",
                            color = Color(0xFFBAC9CC),
                            fontSize = 12.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF00DAF3))
                            .clickable {
                                if (service.isNotBlank()) {
                                    onSave(service, username, password)
                                    onDismiss()
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "ENCRYPT & SAVE",
                            color = Color(0xFF00363D),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
