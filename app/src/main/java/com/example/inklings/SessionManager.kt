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

    // Requirement 16: Track whether the main document has been successfully saved at least once.
    // This allows the app to distinguish between a "fresh" document and a "previously saved" one.
    var isDocumentSaved = false
        private set

    private fun generateSessionFileName(): String {
        val now = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd-EEE-HH_mm_ss", Locale.US)
        val formattedDate = dateFormat.format(now).uppercase(Locale.US)
        return "DA-$formattedDate.md"
    }

    fun saveDocument(content: String): Result<Unit> {
        return try {
            // Requirement 16: If a document has already been saved, we update the existing file 
            // even if the content is empty. This allows intentional clearing of a file's content
            // without deleting the file or creating a new timestamped version.
            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveWithMediaStore(content, relativePath, sessionFileName, isDocument = true)
            } else {
                saveWithLegacyStorage(content, relativePath, sessionFileName)
            }
            if (result.isSuccess) {
                isDocumentSaved = true
            }
            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun saveTimeLog(minutes: Int): Result<Unit> {
        return try {
            val now = Date()
            val yearFormat = SimpleDateFormat("yyyy", Locale.US)
            val monthFormat = SimpleDateFormat("MM", Locale.US)
            val fileTimestampFormat = SimpleDateFormat("yyyy-MM-dd - HH-mm-ss", Locale.US)
            
            val year = yearFormat.format(now)
            val month = monthFormat.format(now)
            val fileTimestamp = fileTimestampFormat.format(now)
            
            val logRelativePath = "Documents/Inklings/99 Operations/99 Log/$year/$month"
            val logFileName = "BAS-$fileTimestamp.md"
            val content = "dailying:: $minutes"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveWithMediaStore(content, logRelativePath, logFileName, isDocument = false)
            } else {
                saveWithLegacyStorage(content, logRelativePath, logFileName)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun saveWithMediaStore(
        content: String,
        path: String,
        fileName: String,
        isDocument: Boolean
    ): Result<Unit> {
        val resolver = context.contentResolver
        
        // Track sessionUri only for the main document
        val uri = if (isDocument) {
            sessionUri ?: findExistingUri(fileName, path) ?: createNewUri(fileName, path)
        } else {
            createNewUri(fileName, path)
        }
        
        return if (uri != null) {
            if (isDocument) sessionUri = uri
            resolver.openOutputStream(uri, "wt")?.use { outputStream ->
                outputStream.write(content.toByteArray(Charsets.UTF_8))
                Result.success(Unit)
            } ?: Result.failure(Exception("Failed to open output stream"))
        } else {
            Result.failure(Exception("Failed to create MediaStore entry"))
        }
    }

    private fun findExistingUri(fileName: String, path: String): Uri? {
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ? AND ${MediaStore.MediaColumns.RELATIVE_PATH} = ?"
        val selectionArgs = arrayOf(fileName, "$path/")
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

    private fun createNewUri(fileName: String, path: String): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/markdown")
            put(MediaStore.MediaColumns.RELATIVE_PATH, path)
        }
        return context.contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
    }

    private fun saveWithLegacyStorage(content: String, path: String, fileName: String): Result<Unit> {
        val rootDir = Environment.getExternalStorageDirectory()
        val targetDir = File(rootDir, path)
        if (!targetDir.exists()) {
            targetDir.mkdirs()
        }
        val file = File(targetDir, fileName)
        file.writeText(content, Charsets.UTF_8)
        return Result.success(Unit)
    }

    fun getSessionFilePath(): String {
        return "$relativePath/$sessionFileName"
    }
}
