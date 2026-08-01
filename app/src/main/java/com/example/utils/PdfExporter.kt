package com.example.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * Utility class to export notes as PDF files and share them externally.
 */
object PdfExporter {

    // Standard A4 dimensions in 72 DPI points
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN_LEFT = 40f
    private const val MARGIN_RIGHT = 555f
    private const val CONTENT_WIDTH = MARGIN_RIGHT - MARGIN_LEFT

    /**
     * Exports a note entity/item into a formatted PDF document.
     *
     * @param context Android Application Context
     * @param title Title of the note
     * @param content Raw Markdown content of the note
     * @param category Category tag/classification
     * @param tags Tag list string
     * @param timestamp Modification timestamp in milliseconds
     * @return The created [File] pointing to the generated PDF.
     */
    fun exportNoteToPdf(
        context: Context,
        title: String,
        content: String,
        category: String = "Notes",
        tags: String = "",
        timestamp: Long = System.currentTimeMillis()
    ): File? {
        val pdfDocument = PdfDocument()

        try {
            var pageNumber = 1
            var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            var page = pdfDocument.startPage(pageInfo)
            var canvas = page.canvas

            var yPosition = 40f

            // Background canvas tint
            canvas.drawColor(Color.parseColor("#0F1E2E"))

            // Header Banner Box
            val headerPaint = Paint().apply {
                color = Color.parseColor("#162738")
                style = Paint.Style.FILL
            }
            val headerRect = RectF(MARGIN_LEFT, yPosition, MARGIN_RIGHT, yPosition + 100f)
            canvas.drawRoundRect(headerRect, 12f, 12f, headerPaint)

            // Header Border
            val borderPaint = Paint().apply {
                color = Color.parseColor("#00DAF3")
                style = Paint.Style.STROKE
                strokeWidth = 1.5f
            }
            canvas.drawRoundRect(headerRect, 12f, 12f, borderPaint)

            // App Label
            val brandPaint = Paint().apply {
                color = Color.parseColor("#00DAF3")
                textSize = 10f
                typeface = Typeface.MONOSPACE
                isAntiAlias = true
            }
            canvas.drawText("LIFEVAULT SECURE EXPORT • PDF DOCUMENT", MARGIN_LEFT + 16f, yPosition + 24f, brandPaint)

            // Title Text in Header
            val titlePaint = Paint().apply {
                color = Color.parseColor("#FFFFFF")
                textSize = 18f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            val sanitizedTitle = if (title.isBlank()) "Untitled Note" else title
            canvas.drawText(sanitizedTitle, MARGIN_LEFT + 16f, yPosition + 52f, titlePaint)

            // Metadata Row (Category & Date)
            val metaPaint = Paint().apply {
                color = Color.parseColor("#BAC9CC")
                textSize = 10f
                typeface = Typeface.MONOSPACE
                isAntiAlias = true
            }
            val formattedDate = timestamp.toFormattedDate()
            val metaText = "Category: ${category.uppercase()}  |  Date: $formattedDate  |  Tags: ${tags.ifBlank { "None" }}"
            canvas.drawText(metaText, MARGIN_LEFT + 16f, yPosition + 78f, metaPaint)

            yPosition += 125f

            // Text paints for content rendering
            val bodyPaint = Paint().apply {
                color = Color.parseColor("#D4E4FA")
                textSize = 12f
                typeface = Typeface.DEFAULT
                isAntiAlias = true
            }

            val h1Paint = Paint().apply {
                color = Color.parseColor("#00DAF3")
                textSize = 16f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val h2Paint = Paint().apply {
                color = Color.parseColor("#90E0EF")
                textSize = 14f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val codePaint = Paint().apply {
                color = Color.parseColor("#80FFAE")
                textSize = 11f
                typeface = Typeface.MONOSPACE
                isAntiAlias = true
            }

            val codeBgPaint = Paint().apply {
                color = Color.parseColor("#081420")
                style = Paint.Style.FILL
            }

            val lines = content.split("\n")
            var inCodeBlock = false

            for (line in lines) {
                val trimmed = line.trim()

                // Check for page boundary
                if (yPosition > PAGE_HEIGHT - 60f) {
                    drawFooter(canvas, pageNumber)
                    pdfDocument.finishPage(page)

                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    canvas.drawColor(Color.parseColor("#0F1E2E"))
                    yPosition = 50f
                }

                if (trimmed.startsWith("```")) {
                    inCodeBlock = !inCodeBlock
                    yPosition += 4f
                    continue
                }

                if (inCodeBlock) {
                    val bgRect = RectF(MARGIN_LEFT, yPosition - 10f, MARGIN_RIGHT, yPosition + 6f)
                    canvas.drawRect(bgRect, codeBgPaint)
                    canvas.drawText(line, MARGIN_LEFT + 10f, yPosition, codePaint)
                    yPosition += 16f
                } else when {
                    trimmed.startsWith("# ") -> {
                        yPosition += 8f
                        canvas.drawText(trimmed.removePrefix("# "), MARGIN_LEFT, yPosition, h1Paint)
                        yPosition += 22f
                    }
                    trimmed.startsWith("## ") || trimmed.startsWith("### ") -> {
                        yPosition += 6f
                        val headingText = trimmed.replace(Regex("^#+\\s*"), "")
                        canvas.drawText(headingText, MARGIN_LEFT, yPosition, h2Paint)
                        yPosition += 18f
                    }
                    trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
                        val bulletPaint = Paint().apply {
                            color = Color.parseColor("#00DAF3")
                            style = Paint.Style.FILL
                            isAntiAlias = true
                        }
                        canvas.drawCircle(MARGIN_LEFT + 6f, yPosition - 4f, 3f, bulletPaint)
                        val bulletText = trimmed.substring(2)
                        drawWrappedText(canvas, bulletText, MARGIN_LEFT + 16f, yPosition, CONTENT_WIDTH - 16f, bodyPaint) { newY ->
                            yPosition = newY
                        }
                        yPosition += 16f
                    }
                    trimmed.startsWith("> ") -> {
                        val quoteBarPaint = Paint().apply {
                            color = Color.parseColor("#00DAF3")
                            strokeWidth = 2.5f
                        }
                        canvas.drawLine(MARGIN_LEFT, yPosition - 10f, MARGIN_LEFT, yPosition + 4f, quoteBarPaint)
                        val quoteText = trimmed.removePrefix("> ")
                        drawWrappedText(canvas, quoteText, MARGIN_LEFT + 12f, yPosition, CONTENT_WIDTH - 12f, bodyPaint) { newY ->
                            yPosition = newY
                        }
                        yPosition += 16f
                    }
                    trimmed.isBlank() -> {
                        yPosition += 10f
                    }
                    else -> {
                        drawWrappedText(canvas, line, MARGIN_LEFT, yPosition, CONTENT_WIDTH, bodyPaint) { newY ->
                            yPosition = newY
                        }
                        yPosition += 16f
                    }
                }
            }

            drawFooter(canvas, pageNumber)
            pdfDocument.finishPage(page)

            // Save PDF File to Documents directory or cache
            val fileName = "Note_${sanitizedTitle.replace(Regex("[^a-zA-Z0-9_]"), "_")}_${System.currentTimeMillis()}.pdf"
            val pdfDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "LifeVaultPDFs").apply {
                if (!exists()) mkdirs()
            }
            val pdfFile = File(pdfDir, fileName)
            FileOutputStream(pdfFile).use { out ->
                pdfDocument.writeTo(out)
            }

            return pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            pdfDocument.close()
        }
    }

    /**
     * Helper function to wrap long lines of text onto multiple lines within specified width limit.
     */
    private inline fun drawWrappedText(
        canvas: Canvas,
        text: String,
        x: Float,
        startY: Float,
        maxWidth: Float,
        paint: Paint,
        onLineDrawn: (Float) -> Unit
    ) {
        var currentY = startY
        val words = text.split(" ")
        var currentLine = StringBuilder()

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val textWidth = paint.measureText(testLine)

            if (textWidth > maxWidth) {
                canvas.drawText(currentLine.toString(), x, currentY, paint)
                currentY += paint.textSize + 4f
                currentLine = StringBuilder(word)
            } else {
                currentLine.append(if (currentLine.isEmpty()) word else " $word")
            }
        }

        if (currentLine.isNotEmpty()) {
            canvas.drawText(currentLine.toString(), x, currentY, paint)
        }
        onLineDrawn(currentY)
    }

    private fun drawFooter(canvas: Canvas, pageNumber: Int) {
        val footerPaint = Paint().apply {
            color = Color.parseColor("#3B494C")
            textSize = 9f
            typeface = Typeface.MONOSPACE
            isAntiAlias = true
        }
        canvas.drawText("Generated via LifeVault • Page $pageNumber", MARGIN_LEFT, PAGE_HEIGHT - 25f, footerPaint)
    }

    /**
     * Launches standard Android Share Intent to open or share the PDF note file.
     */
    fun shareNotePdf(context: Context, pdfFile: File) {
        try {
            val authority = "${context.packageName}.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, pdfFile)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Exported Note: ${pdfFile.nameWithoutExtension}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Share Note PDF")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Exported PDF saved: ${pdfFile.name}", Toast.LENGTH_LONG).show()
        }
    }
}
