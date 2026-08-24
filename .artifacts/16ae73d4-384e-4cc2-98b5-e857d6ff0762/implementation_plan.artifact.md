# Implementation Plan - Requirement 18 (Timer Sounds, Rename, Word Count)

This plan implements timer audio alerts, optional document renaming, and word count logging in the BAS files.

## User Review Required

> [!IMPORTANT]
> - `pop.mp3` will be played 3 times on fresh timer start and on completion. This is independent of the "Typewriter Sounds" setting.
> - Document Rename is explicit and optional. The title is inserted into the filename: `<prefix>-<date>-<day>-<title>-<time>.md`.
> - BAS logs will now store word counts as `words:: <count>`.

## Proposed Changes

### Audio & Timer (Part A)

#### [MODIFY] [TypewriterSoundManager.kt](file:///E:/git/Inklings/app/src/main/java/com/example/inklings/TypewriterSoundManager.kt)
- Load `pop.mp3` from assets.
- Add `playPopThreeTimes()` method using a coroutine or sequential `SoundPool` calls with a small delay to ensure 3 distinct sounds.

#### [MODIFY] [WritingViewModel.kt](file:///E:/git/Inklings/app/src/main/java/com/example/inklings/WritingViewModel.kt)
- Trigger `soundManager.playPopThreeTimes()` in `startTimer()` if it's a fresh start (stopped -> running).
- Trigger `soundManager.playPopThreeTimes()` when `remainingTimeMillis` reaches 0.

---

### Document Rename (Part B)

#### [MODIFY] [SessionManager.kt](file:///E:/git/Inklings/app/src/main/java/com/example/inklings/SessionManager.kt)
- Add `currentTitle` property to track the optional title.
- Update `generateSessionFileName` to include the title if present.
- Add `renameDocument(newTitle: String): Result<Unit>`:
    - Validate title (no path traversal, etc.).
    - Check for naming conflicts.
    - Perform `MediaStore` or `File` rename.
    - Update internal state (`sessionFileName`, `currentTitle`).

#### [MODIFY] [WritingViewModel.kt](file:///E:/git/Inklings/app/src/main/java/com/example/inklings/WritingViewModel.kt)
- Add `renameCurrentDocument(title: String)` method.

#### [MODIFY] [WritingScreen.kt](file:///E:/git/Inklings/app/src/main/java/com/example/inklings/WritingScreen.kt)
- Add a "Rename" action button (e.g., using `Icons.Outlined.Edit`).
- Implement `RenameDialog` for user input.

---

### Word Count Logging (Part C)

#### [MODIFY] [SessionManager.kt](file:///E:/git/Inklings/app/src/main/java/com/example/inklings/SessionManager.kt)
- Update `saveTimeLog(minutes: Int)` to `saveTimeLog(minutes: Int, wordCount: Int)`.
- Implement logic to overwrite/update the `words::` and `dailying::` lines in the BAS file instead of just appending (per Requirement 37).

#### [MODIFY] [WritingViewModel.kt](file:///E:/git/Inklings/app/src/main/java/com/example/inklings/WritingViewModel.kt)
- Add `calculateWordCount(text: String): Int` helper.
- Pass word count to `sessionManager.saveTimeLog`.

---

## Verification Plan

### Automated Tests
- Unit tests for word counting logic.
- Unit tests for filename generation with/without titles.

### Manual Verification
1. **Timer**: Start timer from 30:00, hear 3 pops. Let it run down (or set to 5s for testing), hear 3 pops and see red text.
2. **Rename**: Save a file. Click "Rename", enter "Chapter1". Verify filename on disk. Restart app, verify it still works.
3. **Word Count**: Write "One two three". Save/Close. Inspect BAS file for `words:: 3`.
4. **Move + Rename**: Move a titled file to another project. Verify title is preserved and prefix changes.
