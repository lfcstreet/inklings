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

/**
 * Requirement 17A: Session and File persistence relative to a Project.
 * The internal folder structure (08 Dailies, 99 Operations) is preserved inside each project folder.
 */
class SessionManager(private val context: Context, var project: Project) {

    private var projectRootPath = "Documents/Inklings/${project.name}"
    private var relativePath = "$projectRootPath/08 Dailies/01 Inbox"
    
    // Requirement 17C: Store the specific date to ensure DA and BAS share the exact same timestamp.
    private val sessionDate = Date()
    val sessionFileName: String = generateSessionFileName(sessionDate)
    private var sessionUri: Uri? = null

    // Requirement 16: Track whether the main document has been successfully saved at least once.
    // This allows the app to distinguish between a "fresh" document and a "previously saved" one.
    var isDocumentSaved = false
        private set

    /**
     * Requirement 32 & 17C: Timestamp convention remains unchanged to maintain DA/BAS association.
     * Both files share the same timestamp derived from the session start.
     */
    private fun generateSessionFileName(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd-EEE-HH_mm_ss", Locale.US)
        val formattedDate = dateFormat.format(date).uppercase(Locale.US)
        return "DA-$formattedDate.md"
    }

    private fun generateLogFileName(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd-EEE-HH_mm_ss", Locale.US)
        val formattedDate = dateFormat.format(date).uppercase(Locale.US)
        return "BAS-$formattedDate.md"
    }

    fun saveDocument(content: String): Result<Unit> {
        return try {
            // Requirement 16 & 17A: Save document within its project directory.
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

    /**
     * Requirement 39 & 17C: Existing log behavior remains intact, now stored under the Project path.
     * The BAS file uses the same timestamp as the DA file for association.
     */
    fun saveTimeLog(minutes: Int): Result<Unit> {
        return try {
            val yearFormat = SimpleDateFormat("yyyy", Locale.US)
            val monthFormat = SimpleDateFormat("MM", Locale.US)
            
            val year = yearFormat.format(sessionDate)
            val month = monthFormat.format(sessionDate)
            
            val logRelativePath = "$projectRootPath/99 Operations/99 Log/$year/$month"
            val logFileName = generateLogFileName(sessionDate)
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

    /**
     * Requirement 17C: Move the currently open saved document and its BAS file to another project.
     * Transactional: succeeds only if both move (or DA moves and BAS doesn't exist).
     * Rejects if conflicts exist in target project.
     */
    fun moveSession(targetProject: Project): Result<Unit> {
        if (!isDocumentSaved) return Result.failure(Exception("Document must be saved before moving"))

        val targetRootPath = "Documents/Inklings/${targetProject.name}"
        val targetDaPath = "$targetRootPath/08 Dailies/01 Inbox"
        
        val yearFormat = SimpleDateFormat("yyyy", Locale.US)
        val monthFormat = SimpleDateFormat("MM", Locale.US)
        val year = yearFormat.format(sessionDate)
        val month = monthFormat.format(sessionDate)
        val targetBasPath = "$targetRootPath/99 Operations/99 Log/$year/$month"
        val logFileName = generateLogFileName(sessionDate)

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                moveWithMediaStore(targetProject, targetDaPath, targetBasPath, logFileName)
            } else {
                moveWithLegacyStorage(targetProject, targetDaPath, targetBasPath, logFileName)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun moveWithMediaStore(
        targetProject: Project,
        targetDaPath: String,
        targetBasPath: String,
        logFileName: String
    ): Result<Unit> {
        val resolver = context.contentResolver
        
        // 1. Check for conflicts
        if (findExistingUri(sessionFileName, targetDaPath) != null) {
            return Result.failure(Exception("Target file already exists in ${targetProject.name}"))
        }
        if (findExistingUri(logFileName, targetBasPath) != null) {
            return Result.failure(Exception("Target log file already exists in ${targetProject.name}"))
        }

        // 2. Identify Source Files
        val daUri = sessionUri ?: findExistingUri(sessionFileName, relativePath)
            ?: return Result.failure(Exception("Source document not found"))
        
        val basSourcePath = "$projectRootPath/99 Operations/99 Log/${SimpleDateFormat("yyyy", Locale.US).format(sessionDate)}/${SimpleDateFormat("MM", Locale.US).format(sessionDate)}"
        val basUri = findExistingUri(logFileName, basSourcePath)

        // 3. Move DA
        val daValues = ContentValues().apply {
            put(MediaStore.MediaColumns.RELATIVE_PATH, targetDaPath)
        }
        if (resolver.update(daUri, daValues, null, null) <= 0) {
            return Result.failure(Exception("Failed to move document"))
        }

        // 4. Move BAS if exists
        if (basUri != null) {
            val basValues = ContentValues().apply {
                put(MediaStore.MediaColumns.RELATIVE_PATH, targetBasPath)
            }
            if (resolver.update(basUri, basValues, null, null) <= 0) {
                // Rollback DA move (optional but good for consistency)
                daValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
                resolver.update(daUri, daValues, null, null)
                return Result.failure(Exception("Failed to move associated log file"))
            }
        }

        // 5. Update State
        updateInternalPaths(targetProject)
        sessionUri = daUri
        return Result.success(Unit)
    }

    private fun moveWithLegacyStorage(
        targetProject: Project,
        targetDaPath: String,
        targetBasPath: String,
        logFileName: String
    ): Result<Unit> {
        val rootDir = Environment.getExternalStorageDirectory()
        val sourceDaFile = File(rootDir, "$relativePath/$sessionFileName")
        val targetDaFile = File(rootDir, "$targetDaPath/$sessionFileName")
        
        val basSourcePath = "$projectRootPath/99 Operations/99 Log/${SimpleDateFormat("yyyy", Locale.US).format(sessionDate)}/${SimpleDateFormat("MM", Locale.US).format(sessionDate)}"
        val sourceBasFile = File(rootDir, "$basSourcePath/$logFileName")
        val targetBasFile = File(rootDir, "$targetBasPath/$logFileName")

        // 1. Check conflicts
        if (targetDaFile.exists()) return Result.failure(Exception("Target file already exists"))
        if (sourceBasFile.exists() && targetBasFile.exists()) return Result.failure(Exception("Target log file already exists"))

        // 2. Ensure target directories
        File(rootDir, targetDaPath).mkdirs()
        if (sourceBasFile.exists()) File(rootDir, targetBasPath).mkdirs()

        // 3. Move files
        if (!sourceDaFile.renameTo(targetDaFile)) return Result.failure(Exception("Failed to move document"))
        
        if (sourceBasFile.exists()) {
            if (!sourceBasFile.renameTo(targetBasFile)) {
                // Rollback DA
                targetDaFile.renameTo(sourceDaFile)
                return Result.failure(Exception("Failed to move log file"))
            }
        }

        updateInternalPaths(targetProject)
        return Result.success(Unit)
    }

    private fun updateInternalPaths(newProject: Project) {
        this.project = newProject
        this.projectRootPath = "Documents/Inklings/${newProject.name}"
        this.relativePath = "$projectRootPath/08 Dailies/01 Inbox"
    }
}
