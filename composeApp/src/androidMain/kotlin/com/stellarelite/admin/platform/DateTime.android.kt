package com.stellarelite.admin.platform

import android.content.ContentValues
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AdminAppContext {
    @Volatile
    var context: android.content.Context? = null
}

actual fun currentTimeParts(): Pair<String, String> {
    val now = Date()
    val d = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(now)
    val t = SimpleDateFormat("HH:mm", Locale.US).format(now)
    return d to t
}

actual fun saveStatementPdf(fileName: String, content: String): Boolean {
    val context = AdminAppContext.context ?: return false
    val doc = PdfDocument()
    return try {
        val pageWidth = 595
        val pageHeight = 842
        val margin = 40f
        val lineHeight = 16f
        val bodySize = 11f
        val paint = Paint().apply {
            this.textSize = bodySize
            color = android.graphics.Color.BLACK
            isAntiAlias = true
        }
        val lines = content.split("\n").ifEmpty { listOf("") }
        var index = 0
        var pageNumber = 1
        while (index < lines.size) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            val page = doc.startPage(pageInfo)
            var y = margin + bodySize
            while (index < lines.size && y < pageHeight - margin) {
                page.canvas.drawText(lines[index], margin, y, paint)
                y += lineHeight
                index++
            }
            doc.finishPage(page)
            pageNumber++
        }
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= 29) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/XYZL")
            } else {
                @Suppress("DEPRECATION")
                put(
                    MediaStore.MediaColumns.DATA,
                    File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName).absolutePath,
                )
            }
        }
        val resolver = context.contentResolver
        val collection = if (Build.VERSION.SDK_INT >= 29) {
            MediaStore.Downloads.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Files.getContentUri("external")
        }
        val uri = resolver.insert(collection, values) ?: return false
        resolver.openOutputStream(uri)?.use { out ->
            doc.writeTo(out)
            out.flush()
        } ?: return false
        true
    } catch (_: Exception) {
        false
    } finally {
        doc.close()
    }
}
