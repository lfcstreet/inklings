package com.example.inklings

import android.content.Context
import android.os.Environment
import android.util.Log
import org.json.JSONObject
import java.io.File

/**
 * Requirement 17A: Project Storage and Data Model.
 * Manages project discovery, migration, and metadata persistence.
 * 
 * 1. The filesystem (Documents/Inklings/) is the source of truth for Project existence.
 *    A project exists because its directory exists. There is no separate registry.
 * 2. Each Project Name is derived directly from its directory name.
 * 3. Metadata (.inklings-project.json) lives inside the project directory, so deleting 
 *    a folder externally naturally removes its metadata.
 * 4. Missing metadata is safely initialized with defaults.
 * 5. Migration from the pre-project structure is idempotent and safe.
 */
class ProjectManager(private val context: Context) {

    private val rootDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Inklings")
    private val DEFAULT_PROJECT_NAME = "default" // fallback and initial project
    private val METADATA_FILENAME = ".inklings-project.json"
    private val DEFAULT_FONT_COLOR = "#000000" // Default to black for now

    private var projectList: List<Project> = emptyList()

    /**
     * Requirement 21 & 25: Discover projects on startup and perform migration if needed.
     * This reconciliation ensures exactly one project is marked as Default.
     */
    fun initialize(): List<Project> {
        if (!rootDir.exists()) {
            rootDir.mkdirs()
        }

        migrateExistingStructure()
        ensureDefaultProjectExists()
        
        projectList = discoverProjects()
        reconcileDefaultStatus()
        
        return projectList
    }

    /**
     * Requirement 25: Migrate pre-Project structure into 'default' project.
     * This is idempotent: it only moves directories if the legacy structure is found.
     * Future startups will skip this as the legacy folders will have been renamed.
     */
    private fun migrateExistingStructure() {
        val defaultProjectDir = File(rootDir, DEFAULT_PROJECT_NAME)
        if (!defaultProjectDir.exists()) {
            defaultProjectDir.mkdirs()
        }

        val foldersToMigrate = listOf("08 Dailies", "99 Operations")
        foldersToMigrate.forEach { folderName ->
            val oldFolder = File(rootDir, folderName)
            if (oldFolder.exists() && oldFolder.isDirectory) {
                val newFolder = File(defaultProjectDir, folderName)
                if (!newFolder.exists()) {
                    // Update rather than delete/recreate to preserve file IDs/integrity
                    oldFolder.renameTo(newFolder)
                } else {
                    // Safety fallback: move to timestamped folder if collision occurs
                    oldFolder.renameTo(File(defaultProjectDir, "${folderName}_migrated_${System.currentTimeMillis()}"))
                }
            }
        }
    }

    private fun ensureDefaultProjectExists() {
        val defaultProjectDir = File(rootDir, DEFAULT_PROJECT_NAME)
        if (!defaultProjectDir.exists()) {
            // Recreate 'default' if it's missing and no other projects exist
            createProjectInternal(DEFAULT_PROJECT_NAME, DEFAULT_FONT_COLOR, true)
        } else {
            val metadataFile = File(defaultProjectDir, METADATA_FILENAME)
            if (!metadataFile.exists()) {
                saveMetadata(defaultProjectDir, Project(DEFAULT_PROJECT_NAME, DEFAULT_FONT_COLOR, true))
            }
        }
    }

    /**
     * Requirement 3 & 16: Scan Inklings/ for valid Project directories.
     * Externally deleted projects are not recreated; they simply disappear from the scan.
     */
    private fun discoverProjects(): List<Project> {
        val subDirs = rootDir.listFiles { file -> file.isDirectory } ?: emptyArray()
        return subDirs.map { dir ->
            val metadata = loadMetadata(dir) ?: initializeMissingMetadata(dir)
            Project(dir.name, metadata.fontColor, metadata.isDefault)
        }
    }

    private fun loadMetadata(projectDir: File): Project? {
        val metadataFile = File(projectDir, METADATA_FILENAME)
        if (!metadataFile.exists()) return null
        
        return try {
            val json = JSONObject(metadataFile.readText())
            Project(
                name = projectDir.name,
                fontColor = json.optString("fontColor", DEFAULT_FONT_COLOR),
                isDefault = json.optBoolean("isDefault", false)
            )
        } catch (e: Exception) {
            Log.e("ProjectManager", "Failed to load metadata for ${projectDir.name}", e)
            null
        }
    }

    private fun saveMetadata(projectDir: File, project: Project) {
        val metadataFile = File(projectDir, METADATA_FILENAME)
        val json = JSONObject().apply {
            put("fontColor", project.fontColor)
            put("isDefault", project.isDefault)
        }
        metadataFile.writeText(json.toString(2))
    }

    /**
     * Requirement 14: Initialize safe metadata if missing.
     */
    private fun initializeMissingMetadata(projectDir: File): Project {
        val project = Project(projectDir.name, DEFAULT_FONT_COLOR, false)
        saveMetadata(projectDir, project)
        return project
    }

    /**
     * Requirement 10, 19, 20: Ensure exactly one Project is Default.
     * Prefers existing default, falls back to 'default' project.
     */
    private fun reconcileDefaultStatus() {
        val defaults = projectList.filter { it.isDefault }
        
        if (defaults.size == 1) return

        var activeDefaultFound = false
        val updatedList = projectList.map { project ->
            val shouldBeDefault = if (!activeDefaultFound && (project.isDefault || project.name == DEFAULT_PROJECT_NAME)) {
                activeDefaultFound = true
                true
            } else {
                false
            }
            
            if (shouldBeDefault != project.isDefault) {
                val updated = project.copy(isDefault = shouldBeDefault)
                saveMetadata(File(rootDir, project.name), updated)
                updated
            } else {
                project
            }
        }
        
        if (!activeDefaultFound && updatedList.isNotEmpty()) {
            val first = updatedList[0].copy(isDefault = true)
            saveMetadata(File(rootDir, first.name), first)
            projectList = listOf(first) + updatedList.drop(1)
        } else {
            projectList = updatedList
        }
    }

    /**
     * Requirement 22 & 33: Data-layer Project creation.
     * UI, renaming, in-app deletion, and moving files between projects are 
     * deliberately deferred to later requirements (17B, 17C).
     */
    fun createProject(name: String, fontColor: String, isDefault: Boolean): Result<Project> {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) return Result.failure(Exception("Project name cannot be empty"))
        
        // Requirement 23: Basic directory name safety check for Android.
        val invalidChars = charArrayOf('/', '\\', ':', '*', '?', '"', '<', '>', '|')
        if (trimmedName.any { it in invalidChars }) {
            return Result.failure(Exception("Project name contains invalid characters"))
        }
        
        val projectDir = File(rootDir, trimmedName)
        if (projectDir.exists()) return Result.failure(Exception("Project directory already exists"))
        
        return try {
            val project = createProjectInternal(trimmedName, fontColor, isDefault)
            if (isDefault) {
                projectList = projectList.map { p ->
                    if (p.name != trimmedName && p.isDefault) {
                        val updated = p.copy(isDefault = false)
                        saveMetadata(File(rootDir, p.name), updated)
                        updated
                    } else p
                }
            }
            projectList = projectList + project
            Result.success(project)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createProjectInternal(name: String, fontColor: String, isDefault: Boolean): Project {
        val projectDir = File(rootDir, name)
        projectDir.mkdirs()
        File(projectDir, "08 Dailies/01 Inbox").mkdirs()
        File(projectDir, "99 Operations/99 Log").mkdirs()
        
        val project = Project(name, fontColor, isDefault)
        saveMetadata(projectDir, project)
        return project
    }

    fun getDefaultProject(): Project {
        return projectList.find { it.isDefault } 
            ?: projectList.find { it.name == DEFAULT_PROJECT_NAME }
            ?: projectList.first() 
    }

    /**
     * Requirement 31: Determine project association from its absolute path.
     */
    fun getProjectForPath(path: String): Project? {
        val parts = path.split("/")
        val inklingsIndex = parts.indexOf("Inklings")
        if (inklingsIndex != -1 && inklingsIndex + 1 < parts.size) {
            val projectName = parts[inklingsIndex + 1]
            return projectList.find { it.name == projectName }
        }
        return null
    }

    fun getAllProjects(): List<Project> = projectList
}

