package com.example.utils

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.util.Log
import java.io.File

/**
 * Utility helper class for recording voice audio using MediaRecorder API
 * and playing back voice recordings using MediaPlayer.
 */
class AudioRecorderHelper(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var currentOutputFile: File? = null

    var isRecording: Boolean = false
    var isPlaying: Boolean = false

    /**
     * Creates a new audio recording file in local app storage.
     */
    fun createAudioFile(noteId: Long? = null): File {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            ?: context.filesDir
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        val prefix = if (noteId != null && noteId > 0) "note_${noteId}_" else "voice_"
        return File(storageDir, "AUDIO_${prefix}${System.currentTimeMillis()}.m4a")
    }

    /**
     * Starts voice recording using MediaRecorder API and outputs to specified [outputFile].
     */
    fun startRecording(outputFile: File): Boolean {
        stopRecording()
        stopPlaying()

        return try {
            currentOutputFile = outputFile
            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }
            mediaRecorder = recorder
            isRecording = true
            true
        } catch (e: Exception) {
            Log.e("AudioRecorderHelper", "Error starting MediaRecorder: ${e.message}", e)
            stopRecording()
            false
        }
    }

    /**
     * Stops current voice recording session and returns the saved audio file path.
     */
    fun stopRecording(): String? {
        if (!isRecording && mediaRecorder == null) return currentOutputFile?.absolutePath

        val savedPath = try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            currentOutputFile?.absolutePath
        } catch (e: Exception) {
            Log.e("AudioRecorderHelper", "Error stopping MediaRecorder: ${e.message}", e)
            null
        } finally {
            mediaRecorder = null
            isRecording = false
        }
        return savedPath
    }

    /**
     * Plays back recorded audio from the specified file path using MediaPlayer.
     */
    fun startPlaying(filePath: String, onCompletion: () -> Unit, onError: (String) -> Unit) {
        stopPlaying()

        val file = File(filePath)
        if (!file.exists()) {
            onError("Audio file not found on local storage.")
            return
        }

        try {
            val player = MediaPlayer().apply {
                setDataSource(filePath)
                prepare()
                start()
                setOnCompletionListener {
                    this@AudioRecorderHelper.isPlaying = false
                    onCompletion()
                }
                setOnErrorListener { _, what, extra ->
                    this@AudioRecorderHelper.isPlaying = false
                    onError("Playback error ($what, $extra)")
                    true
                }
            }
            mediaPlayer = player
            isPlaying = true
        } catch (e: Exception) {
            Log.e("AudioRecorderHelper", "Error starting MediaPlayer: ${e.message}", e)
            isPlaying = false
            onError(e.localizedMessage ?: "Playback failed")
        }
    }

    /**
     * Stops current audio playback.
     */
    fun stopPlaying() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
        } catch (e: Exception) {
            Log.e("AudioRecorderHelper", "Error stopping MediaPlayer: ${e.message}", e)
        } finally {
            mediaPlayer = null
            isPlaying = false
        }
    }

    /**
     * Clean up player and recorder resources.
     */
    fun release() {
        stopRecording()
        stopPlaying()
    }
}
