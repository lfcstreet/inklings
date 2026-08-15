package com.example.inklings

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SessionManager(private val context: Context) {

    private val relativePath = "Documents/Inklings/08 Dailies/01 Inbox"
    
    val sessionFileName: String = generateSessionFileName()
    private var sessionUri: Uri? = null

    private fun generateSessionFileName(): String {
        val now = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd-EEE-HH_mm_ss", Locale.US)
        val formattedDate = dateFormat.format(now).uppercase(Locale.US)
        return "DA-$formattedDate.md"
    }

    fun saveDocument(content: String): Result<Unit> {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveWithMediaStore(content)
            } else {
                saveWithLegacyStorage(content)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun saveWithMediaStore(content: String): Result<Unit> {
        val resolver = context.contentResolver
        
        // Check if we already have a URI for this session
        val uri = sessionUri ?: findExistingUri(sessionFileName) ?: createNewUri(sessionFileName)
        
        return if (uri != null) {
            sessionUri = uri
            resolver.openOutputStream(uri, "wt")?.use { outputStream ->
                outputStream.write(content.toByteArray(Charsets.UTF_8))
                Result.success(Unit)
            } ?: Result.failure(Exception("Failed to open output stream"))
        } else {
            Result.failure(Exception("Failed to create MediaStore entry"))
        }
    }

    private fun findExistingUri(fileName: String): Uri? {
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ? AND ${MediaStore.MediaColumns.RELATIVE_PATH} = ?"
        val selectionArgs = arrayOf(fileName, "$relativePath/")
        val queryUri = MediaStore.Files.getContentUri("external")
        
        context.contentResolver.query(
            queryUri,
            arrayOf(MediaStore.MediaColumns._ID),
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                return Uri.withAppendedPath(queryUri, id.toString())
            }
        }
        return null
    }

    private fun createNewUri(fileName: String): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/markdown")
            put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
        }
        return context.contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
    }

    private fun saveWithLegacyStorage(content: String): Result<Unit> {
        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val inklingsDir = File(documentsDir, "Inklings/08 Dailies/01 Inbox")
        if (!inklingsDir.exists()) {
            inklingsDir.mkdirs()
        }
        val file = File(inklingsDir, sessionFileName)
        file.writeText(content, Charsets.UTF_8)
        return Result.success(Unit)
    }

    fun getSessionFilePath(): String {
        return "$relativePath/$sessionFileName"
    }
}
