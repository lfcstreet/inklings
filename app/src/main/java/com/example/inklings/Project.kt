package com.example.inklings

/**
 * Requirement 17A: Project Data Model.
 * The Project Name is derived from the directory name on the filesystem.
 * Other metadata is stored in .inklings-project.json.
 */
data class Project(
    val name: String,
    val fontColor: String,
    val isDefault: Boolean
)
