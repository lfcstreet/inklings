# Implementation Plan - Requirement 17D-FIX-01 (Document Filename Format & Rename Integration)

This plan implements the new Markdown document filename format, validates empty document prefixes, and integrates these changes with the existing rename and move functionality.

## User Review Required

> [!IMPORTANT]
> The new Markdown filename format is `[PREFIX-]YYYY-MM-DD, Ddd - TITLE - HH_MM_SS.md`.
> - If `documentPrefix` is empty, the filename starts with the date.
> - Newly created documents will have `TITLE` as a placeholder.
> - The existing Rename feature will now replace the `TITLE` component in this specific format.

## Proposed Changes

### Project Management

#### [MODIFY] [ProjectManager.kt](file:///E:/git/Inklings/app/src/main/java/com/example/inklings/ProjectManager.kt)
- Update `loadMetadata` to allow `documentPrefix` to be an empty string (remove `.ifEmpty { "DA" }`).

### Session & Filename Handling

#### [MODIFY] [SessionManager.kt](file:///E:/git/Inklings/app/src/main/java/com/example/inklings/SessionManager.kt)
- Update `currentTitle` to default to `"TITLE"` for new sessions.
- **New helper method `buildNewFormatFileName(prefix: String, date: Date, title: String): String`**:
    - Constructs the filename using the new format: `[PREFIX-]YYYY-MM-DD, Ddd - TITLE - HH_MM_SS.md`.
    - Handles blank prefix logic (no leading hyphen).
    - Uses exact weekday format (`Mon`, `Tue`, etc.).
- **Update `generateSessionFileName`**: Use `buildNewFormatFileName`.
- **Update `renameDocument`**:
    - Identify the components of the current filename (prefix, date, time).
    - Construct the new filename using the new title while preserving other components.
    - Support both titled and untitled (placeholder `TITLE`) new-format filenames.
- **Update `moveSession`**:
    - Use the target project's `documentPrefix` and the current `currentTitle` to generate the destination filename in the new format.

## Verification Plan

### Automated Tests
- Unit tests for filename construction with various prefixes and titles.
- Unit tests for parsing/extracting components from the new filename format during rename.

### Manual Verification
1. **New Document**: Create a document in a project with prefix `DA`. Verify filename is `DA-2026-08-27, Thu - TITLE - 21_43_10.md`.
2. **Blank Prefix**: Create a document in a project with prefix `""`. Verify filename starts with date.
3. **Rename**: Rename the `TITLE` to `My Chapter`. Verify other components are preserved.
4. **Project Move**: Move the titled document to another project with prefix `RN`. Verify filename becomes `RN-2026-08-27, Thu - My Chapter - 21_43_10.md`.
5. **Old File Compatibility**: Open an old-format file and save it. Verify its filename remains unchanged.
6. **BAS Association**: Verify BAS logs are still correctly associated with the session (same timestamp).
