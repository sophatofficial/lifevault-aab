package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.utils.AudioRecorderHelper
import kotlinx.coroutines.delay
import java.io.File
import java.util.Locale

@Composable
fun VoiceRecorderCard(
    noteId: Long?,
    audioPath: String?,
    onAudioSaved: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val audioHelper = remember { AudioRecorderHelper(context) }

    var isRecording by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var recordingTimeSeconds by remember { mutableIntStateOf(0) }
    var currentAudioPath by remember(audioPath) { mutableStateOf(audioPath) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Launcher for Audio Permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val outputFile = audioHelper.createAudioFile(noteId)
            val success = audioHelper.startRecording(outputFile)
            if (success) {
                isRecording = true
                recordingTimeSeconds = 0
                errorMessage = null
            } else {
                errorMessage = "Failed to start MediaRecorder"
            }
        } else {
            Toast.makeText(context, "Microphone permission is required for voice recording", Toast.LENGTH_SHORT).show()
        }
    }

    // Cleanup when component leaves composition
    DisposableEffect(Unit) {
        onDispose {
            audioHelper.release()
        }
    }

    // Timer effect while recording
    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingTimeSeconds = 0
            while (isRecording) {
                delay(1000)
                recordingTimeSeconds++
            }
        }
    }

    // Pulsing animation for active recording red dot
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0C1D2E))
            .border(1.dp, if (isRecording) Color(0xFFFF5252) else Color(0xFF233B54), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Recording",
                        tint = if (isRecording) Color(0xFFFF5252) else Color(0xFF00DAF3),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VOICE MEMO",
                        color = Color(0xFFD4E4FA),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }

                if (currentAudioPath != null && !isRecording) {
                    IconButton(
                        onClick = {
                            audioHelper.stopPlaying()
                            isPlaying = false
                            currentAudioPath = null
                            onAudioSaved(null)
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Voice Note",
                            tint = Color(0xFFFF6B6B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Active Recording UI
            if (isRecording) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF2A1212))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .scale(pulseScale)
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF5252))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Recording: ${formatSeconds(recordingTimeSeconds)}",
                            color = Color(0xFFFFB4B4),
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            val savedPath = audioHelper.stopRecording()
                            isRecording = false
                            if (savedPath != null) {
                                currentAudioPath = savedPath
                                onAudioSaved(savedPath)
                                Toast.makeText(context, "Voice note saved!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF5252),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop Recording",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "STOP",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            } else if (currentAudioPath != null) {
                // Audio Playback Player UI
                val file = File(currentAudioPath!!)
                val fileName = file.name

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF102638))
                        .border(1.dp, Color(0xFF00DAF3).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00DAF3).copy(alpha = 0.15f))
                                .clickable {
                                    if (isPlaying) {
                                        audioHelper.stopPlaying()
                                        isPlaying = false
                                    } else {
                                        audioHelper.startPlaying(
                                            filePath = currentAudioPath!!,
                                            onCompletion = { isPlaying = false },
                                            onError = { err ->
                                                isPlaying = false
                                                errorMessage = err
                                            }
                                        )
                                        isPlaying = true
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause Voice Recording" else "Play Voice Recording",
                                tint = Color(0xFF00DAF3),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = Color(0xFF00DAF3),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isPlaying) "PLAYING VOICE MEMO..." else "ATTACHED VOICE MEMO",
                                    color = Color(0xFF00DAF3),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = fileName,
                                color = Color(0xFFBAC9CC),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            } else {
                // Record Button UI (No Recording attached yet)
                Button(
                    onClick = {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasPermission) {
                            val outputFile = audioHelper.createAudioFile(noteId)
                            val success = audioHelper.startRecording(outputFile)
                            if (success) {
                                isRecording = true
                                recordingTimeSeconds = 0
                                errorMessage = null
                            } else {
                                errorMessage = "Failed to start MediaRecorder API"
                            }
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF102638),
                        contentColor = Color(0xFF00DAF3)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Record Audio",
                            tint = Color(0xFF00DAF3),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "RECORD VOICE NOTE (MediaRecorder)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            AnimatedVisibility(visible = errorMessage != null) {
                errorMessage?.let { err ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = err,
                        color = Color(0xFFFF6B6B),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

private fun formatSeconds(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}
