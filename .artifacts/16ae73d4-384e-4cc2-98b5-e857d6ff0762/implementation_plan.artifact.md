# Implementation Plan - Move Current Document Between Projects (Requirement 17C)

This plan adds the ability to move a saved document and its associated BAS log between projects, reusing the existing Project management UI.

## User Review Required

> [!IMPORTANT]
> Moving a document is only available for **saved** documents. Unsaved sessions will have the "Move here" action disabled.

> [!NOTE]
> The movement is transactional: if the matching BAS file exists, both must move successfully for the operation to be considered a success. If a conflict exists in the target project, the move will be aborted.

## Proposed Changes

### Storage & Persistence Layer

#### [MODIFY] [SessionManager.kt](file:///E:/git/Inklings/app/src/main/java/com/example/inklings/SessionManager.kt)
- Add `moveSession(targetProject: Project): Result<Unit>`:
    - Logic to calculate target paths for DA and BAS files.
    - Conflict detection: check if files already exist at target locations.
    - Transactional move:
        1. Find BAS file (if any) using the timestamp derived from `sessionFileName`.
        2. Move DA file using `MediaStore` (update `RELATIVE_PATH`) or legacy `File.renameTo()`.
        3. Move BAS file (if any) preserving its `YYYY/MM` sub-structure.
        4. If any step fails, attempt to rollback or report error.
    - Update `projectRootPath` and `relativePath` internal states upon success.
- Add helper `getTimestampFromFileName(fileName: String): String`.

### ViewModel Layer

#### [MODIFY] [WritingViewModel.kt](file:///E:/git/Inklings/app/src/main/java/com/example/inklings/WritingViewModel.kt)
- Add `isDocumentSaved` property (delegate to `sessionManager.isDocumentSaved`).
- Add `moveCurrentDocument(targetProject: Project)`:
    - Call `sessionManager.moveSession(targetProject)`.
    - On success:
        - Update `currentProject` state.
        - Emit a toast confirming the move.
    - On failure:
        - Emit an error toast.

---

### UI Components

#### [MODIFY] [WritingScreen.kt](file:///E:/git/Inklings/app/src/main/java/com/example/inklings/WritingScreen.kt)
- **Modify `ProjectItem`**:
    - Add an optional `onMoveClick: () -> Unit` parameter.
    - Display a "Move here" button if `onMoveClick` is provided and it's not the current project.
    - Ensure styling matches the existing UI (compact, theme-aware).
- **Modify `ProjectManagementDialog`**:
    - Pass `isDocumentSaved` to the dialog.
    - For each project in the list:
        - If it's the current project, show "Current".
        - If it's NOT the current project AND `isDocumentSaved` is true, show "Move here" button.
        - If `isDocumentSaved` is false, hide/disable the move action.
    - Handle `onMoveClick` by calling `viewModel.moveCurrentDocument(project)`.

---

## Verification Plan

### Automated Tests
- Unit tests for `SessionManager.getTimestampFromFileName`.
- Integration tests for `moveSession` (mocking filesystem/MediaStore if possible).

### Manual Verification
1. **Saved Move**: Create a project "Writing", save a document. Create project "Research". Open dialog, click "Move here" on Research. Verify editor color changes, and file is moved on disk.
2. **BAS Move**: Write enough to generate a BAS log. Move the document. Verify both `.md` and `BAS-` files moved to the new project.
3. **Unsaved Check**: Start a new session, do not save. Open dialog. Verify "Move here" is not available.
4. **Conflict Check**: Manually create a file with the same name in a target project. Try to move there. Verify error message and no files moved.
5. **External Deletion**: Delete target project externally. Try to move. Verify safe failure.
6. **Save after Move**: Move a document, then Ctrl+S. Verify new content is in the new project location.
