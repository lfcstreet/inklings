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
    // Requirement 17D: Path is relative to the Project directory.
    private var relativePath = "$projectRootPath/${project.documentSubfolder}"
    
    // Requirement 17C: Store the specific date to ensure DA and BAS share the exact same timestamp.
    private val sessionDate = Date()
    
    // Requirement 18 & 17D-FIX-01: Document title.
    // New sessions start with "TITLE" placeholder.
    private var currentTitle: String = "TITLE"
    
    // Requirement 17D & 18 & 17D-FIX-01: Filename uses project prefix, date, weekday, title and time.
    var sessionFileName: String = generateSessionFileName(sessionDate, project.documentPrefix, currentTitle)
        private set
    private var sessionUri: Uri? = null

    // Requirement 16: Track whether the main document has been successfully saved at least once.
    // This allows the app to distinguish between a "fresh" document and a "previously saved" one.
    var isDocumentSaved = false
        private set

    /**
     * Requirement 32 & 17C & 17D & 18 & 17D-FIX-01: New filename format logic.
     * format: [PREFIX-]YYYY-MM-DD, Ddd - TITLE - HH_MM_SS.md
     * Weekdays: Mon, Tue, Wed, Thu, Fri, Sat, Sun.
     */
    private fun generateSessionFileName(date: Date, prefix: String, title: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val weekdayFormat = SimpleDateFormat("EEE", Locale.US) // e.g. "Mon"
        val timeFormat = SimpleDateFormat("HH_mm_ss", Locale.US)
        
        val datePart = dateFormat.format(date)
        val weekdayPart = weekdayFormat.format(date) // Already "Mon", "Tue" etc.
        val timePart = timeFormat.format(date)
        
        val prefixPart = if (prefix.isBlank()) "" else "$prefix-"
        
        return "$prefixPart$datePart, $weekdayPart - $title - $timePart.md"
    }

    private fun generateLogFileName(date: Date, prefix: String): String {
        // Requirement 17D-FIX-01: BAS/log filename format remains unchanged.
        val dateFormat = SimpleDateFormat("yyyy-MM-dd-EEE-HH_mm_ss", Locale.US)
        val formattedDate = dateFormat.format(date).uppercase(Locale.US)
        return "$prefix-$formattedDate.md"
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
     * Requirement 39 & 17C & 17D & 18: Existing log behavior remains intact.
     * Now uses project-specific logSubfolder and logPrefix.
     * Includes document word count.
     */
    fun saveTimeLog(minutes: Int, wordCount: Int): Result<Unit> {
        return try {
            val yearFormat = SimpleDateFormat("yyyy", Locale.US)
            val monthFormat = SimpleDateFormat("MM", Locale.US)
            
            val year = yearFormat.format(sessionDate)
            val month = monthFormat.format(sessionDate)
            
            val logRelativePath = "$projectRootPath/${project.logSubfolder}/$year/$month"
            val logFileName = generateLogFileName(sessionDate, project.logPrefix)
            
            // Requirement 18 & 37: Authoritative entries for dailying and words.
            val content = "dailying:: $minutes\nwords:: $wordCount"

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
     * Requirement 17C & 17D & 18 & 17D-FIX-01: Move session to another project.
     * Adopts the target project's configured subfolders and prefixes.
     * Preserves current title.
     */
    fun moveSession(targetProject: Project): Result<Unit> {
        if (!isDocumentSaved) return Result.failure(Exception("Document must be saved before moving"))

        val targetRootPath = "Documents/Inklings/${targetProject.name}"
        val targetDaPath = "$targetRootPath/${targetProject.documentSubfolder}"
        val targetDaFileName = generateSessionFileName(sessionDate, targetProject.documentPrefix, currentTitle)
        
        val yearFormat = SimpleDateFormat("yyyy", Locale.US)
        val monthFormat = SimpleDateFormat("MM", Locale.US)
        val year = yearFormat.format(sessionDate)
        val month = monthFormat.format(sessionDate)
        val targetBasPath = "$targetRootPath/${targetProject.logSubfolder}/$year/$month"
        val targetLogFileName = generateLogFileName(sessionDate, targetProject.logPrefix)

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                moveWithMediaStore(targetProject, targetDaPath, targetDaFileName, targetBasPath, targetLogFileName)
            } else {
                moveWithLegacyStorage(targetProject, targetDaPath, targetDaFileName, targetBasPath, targetLogFileName)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun moveWithMediaStore(
        targetProject: Project,
        targetDaPath: String,
        targetDaFileName: String,
        targetBasPath: String,
        targetLogFileName: String
    ): Result<Unit> {
        val resolver = context.contentResolver
        
        // 1. Check for conflicts
        if (findExistingUri(targetDaFileName, targetDaPath) != null) {
            return Result.failure(Exception("Target file already exists in ${targetProject.name}"))
        }
        if (findExistingUri(targetLogFileName, targetBasPath) != null) {
            return Result.failure(Exception("Target log file already exists in ${targetProject.name}"))
        }

        // 2. Identify Source Files
        val daUri = sessionUri ?: findExistingUri(sessionFileName, relativePath)
            ?: return Result.failure(Exception("Source document not found"))
        
        val currentLogFileName = generateLogFileName(sessionDate, project.logPrefix)
        val basSourcePath = "$projectRootPath/${project.logSubfolder}/${SimpleDateFormat("yyyy", Locale.US).format(sessionDate)}/${SimpleDateFormat("MM", Locale.US).format(sessionDate)}"
        val basUri = findExistingUri(currentLogFileName, basSourcePath)

        // 3. Move/Rename DA
        val daValues = ContentValues().apply {
            put(MediaStore.MediaColumns.RELATIVE_PATH, targetDaPath)
            put(MediaStore.MediaColumns.DISPLAY_NAME, targetDaFileName)
        }
        if (resolver.update(daUri, daValues, null, null) <= 0) {
            return Result.failure(Exception("Failed to move document"))
        }

        // 4. Move/Rename BAS if exists
        if (basUri != null) {
            val basValues = ContentValues().apply {
                put(MediaStore.MediaColumns.RELATIVE_PATH, targetBasPath)
                put(MediaStore.MediaColumns.DISPLAY_NAME, targetLogFileName)
            }
            if (resolver.update(basUri, basValues, null, null) <= 0) {
                // Rollback DA move
                val rollbackDaValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
                    put(MediaStore.MediaColumns.DISPLAY_NAME, sessionFileName)
                }
                resolver.update(daUri, rollbackDaValues, null, null)
                return Result.failure(Exception("Failed to move associated log file"))
            }
        }

        // 5. Update State
        updateInternalPaths(targetProject, targetDaFileName)
        sessionUri = daUri
        return Result.success(Unit)
    }

    private fun moveWithLegacyStorage(
        targetProject: Project,
        targetDaPath: String,
        targetDaFileName: String,
        targetBasPath: String,
        targetLogFileName: String
    ): Result<Unit> {
        val rootDir = Environment.getExternalStorageDirectory()
        val sourceDaFile = File(rootDir, "$relativePath/$sessionFileName")
        val targetDaFile = File(rootDir, "$targetDaPath/$targetDaFileName")
        
        val currentLogFileName = generateLogFileName(sessionDate, project.logPrefix)
        val basSourcePath = "$projectRootPath/${project.logSubfolder}/${SimpleDateFormat("yyyy", Locale.US).format(sessionDate)}/${SimpleDateFormat("MM", Locale.US).format(sessionDate)}"
        val sourceBasFile = File(rootDir, "$basSourcePath/$currentLogFileName")
        val targetBasFile = File(rootDir, "$targetBasPath/$targetLogFileName")

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

        updateInternalPaths(targetProject, targetDaFileName)
        return Result.success(Unit)
    }

    /**
     * Requirement 18 & 17D-FIX-01: Rename the currently saved Markdown document.
     * Replaces the TITLE component in the new filename format.
     * Preserves prefix, date, weekday, and time.
     */
    fun renameDocument(newTitle: String): Result<Unit> {
        if (!isDocumentSaved) return Result.failure(Exception("Only saved documents can be renamed"))
        
        val trimmedTitle = newTitle.trim()
        if (trimmedTitle.isBlank()) return Result.failure(Exception("Title cannot be empty"))
        
        // Basic validation for safe filenames
        val invalidChars = charArrayOf('/', '\\', ':', '*', '?', '"', '<', '>', '|')
        if (trimmedTitle.any { it in invalidChars }) {
            return Result.failure(Exception("Title contains invalid characters"))
        }

        // Requirement 17D-FIX-01: Centralized logic to construct the new filename.
        // We reuse the sessionDate and current project's prefix.
        val nextFileName = generateSessionFileName(sessionDate, project.documentPrefix, trimmedTitle)
        if (nextFileName == sessionFileName) return Result.success(Unit) // No change

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                renameWithMediaStore(nextFileName, trimmedTitle)
            } else {
                renameWithLegacyStorage(nextFileName, trimmedTitle)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun renameWithMediaStore(nextFileName: String, nextTitle: String): Result<Unit> {
        val resolver = context.contentResolver
        
        // Check for conflicts
        if (findExistingUri(nextFileName, relativePath) != null) {
            return Result.failure(Exception("File with this title already exists in project"))
        }

        val uri = sessionUri ?: findExistingUri(sessionFileName, relativePath)
            ?: return Result.failure(Exception("Source document not found"))

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, nextFileName)
        }

        return if (resolver.update(uri, values, null, null) > 0) {
            this.sessionFileName = nextFileName
            this.currentTitle = nextTitle
            this.sessionUri = uri
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to rename file"))
        }
    }

    private fun renameWithLegacyStorage(nextFileName: String, nextTitle: String): Result<Unit> {
        val rootDir = Environment.getExternalStorageDirectory()
        val sourceFile = File(rootDir, "$relativePath/$sessionFileName")
        val targetFile = File(rootDir, "$relativePath/$nextFileName")

        if (targetFile.exists()) return Result.failure(Exception("File with this title already exists"))

        return if (sourceFile.renameTo(targetFile)) {
            this.sessionFileName = nextFileName
            this.currentTitle = nextTitle
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to rename file"))
        }
    }

    private fun updateInternalPaths(newProject: Project, newFileName: String? = null) {
        this.project = newProject
        this.projectRootPath = "Documents/Inklings/${newProject.name}"
        this.relativePath = "$projectRootPath/${newProject.documentSubfolder}"
        if (newFileName != null) {
            this.sessionFileName = newFileName
        }
    }
}
