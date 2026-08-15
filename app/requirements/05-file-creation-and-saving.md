# Requirement 05 — Markdown File Creation and Saving

## Objective

Introduce the first real file-handling functionality.

The application must be able to create and save the current writing session
as a Markdown (.md) file.

This requirement introduces basic file persistence only.

Do not implement configurable storage locations, templates, automatic saving,
session timing, NEW or final CLOSE behavior yet.

---

# 1. Writing Session

When the application starts, it creates a new writing session.

Each writing session has a unique filename based on the date and time at which
the session was created.

The filename format MUST be:

    DA-YYYY-MM-DD-DAY-HH_MM_SS.md

Example:

    DA-2026-01-31-SAT-09_50_31.md

Where:

- YYYY = four-digit year
- MM = two-digit month
- DD = two-digit day
- DAY = three-letter uppercase day of week
- HH = two-digit hour
- MM = two-digit minute
- SS = two-digit second

Use the local date and time of the device.

The filename must be safe to use as an Android filesystem filename.

The colon character must NOT be used in the filename.

---

# 2. Main Markdown File Location

The main Markdown file must be stored under:

    <main folder>/08 Dailies/01 Inbox/

For example:

    <main folder>/
    └── 08 Dailies/
        └── 01 Inbox/
            └── DA-2026-01-31-SAT-09_50_31.md

The concept of `<main folder>` will eventually be configurable by the user.

However, configurable storage is NOT part of this requirement.

For Requirement 05, use a simple fixed application-controlled location
suitable for development and testing.

Do not build a settings screen or directory-selection UI yet.

The storage architecture should nevertheless be designed so that the
`<main folder>` can be made configurable in a later requirement without
rewriting the file-saving logic.

---

# 3. Directory Creation

If the required directory does not exist, the application should create the
required directory structure:

    08 Dailies/
    └── 01 Inbox/

The application must not fail merely because the directories do not already
exist.

---

# 4. File Format

The main writing file must be a plain Markdown file:

    .md

The content saved to the file must be the actual underlying document text.

Do NOT save:

- UI formatting
- Hidden-line state
- Cursor position
- Scrolling state
- Compose-specific information

Only the document's actual text should be written to the Markdown file.

---

# 5. Hidden Text

Any text that is visually hidden by the distraction-free editor must still
be saved.

For example, if the underlying document contains:

    Line 1
    Line 2
    Line 3
    Line 4
    Line 5

but the editor currently displays only:

    Line 4
    Line 5

the saved Markdown file MUST contain:

    Line 1
    Line 2
    Line 3
    Line 4
    Line 5

Visual hiding must never affect the saved document.

---

# 6. Initial Save

When the user presses SAVE:

1. Create the required directory if necessary.
2. Create the session Markdown file if it does not already exist.
3. Write the complete current document to the file.
4. If the file already exists, overwrite it with the current document.
5. Keep the current writing session open.

The user must be able to continue writing after SAVE.

---

# 7. SAVE Button

The existing SAVE button must now perform the save operation.

SAVE must:

- Persist the current document.
- Keep the current session open.
- Not close the application.
- Not start a new session.

---

# 8. Timestamp and Filename Uniqueness

The filename is based on the session creation timestamp.

The filename must contain seconds.

If two sessions somehow receive the same timestamp, the implementation must
avoid overwriting an existing session file.

Do not add random identifiers to the normal filename format.

The expected filename format remains:

    DA-YYYY-MM-DD-DAY-HH_MM_SS.md

Only use a collision-handling mechanism if genuinely necessary.

---

# 9. Encoding

Save the Markdown file using UTF-8 encoding.

The application must correctly preserve:

- ASCII text
- Unicode characters
- Newlines
- Markdown punctuation
- Emoji and other Unicode characters where supported by the device

---

# 10. Save Errors

If saving fails:

- Do not silently pretend the save succeeded.
- Do not delete the user's document.
- Show a simple user-visible error message.
- Keep the document in memory.
- Allow the user to attempt SAVE again.

Do not implement an elaborate error-handling UI.

---

# 11. Storage Permissions

Use the modern Android storage mechanisms appropriate for the selected
storage location.

Do not request broad filesystem/storage permissions unnecessarily.

If the chosen storage mechanism cannot provide the required
`<main folder>/08 Dailies/01 Inbox/` structure without user interaction,
explain the limitation rather than silently implementing a different behavior.

The final configurable storage solution will be addressed in a later
requirement.

---

# 12. Automatic Saving

Do NOT implement automatic saving yet.

Automatic saving every two minutes will be implemented in a later requirement.

---

# 13. NEW

Do NOT implement final NEW behavior yet.

The NEW button should remain unchanged for this requirement.

---

# 14. CLOSE

Do NOT implement final CLOSE behavior yet.

The CLOSE button should remain unchanged for this requirement.

---

# 15. Templates

Do NOT implement templates yet.

The application will later use:

    template/
    ├── maintemplate.md
    └── timetemplate.md

Template handling belongs to a later requirement.

---

# 16. Configurable Main Folder

Do NOT implement configuration yet.

The eventual application will allow the user to specify the main folder.

The resulting directory structure will be:

    <configured main folder>/
    └── 08 Dailies/
        └── 01 Inbox/
            └── DA-YYYY-MM-DD-DAY-HH_MM_SS.md

The current implementation should keep the location logic isolated enough
that this can be changed later.

---

# 17. Existing Functionality

Do not break any existing editor functionality.

The application must continue to support:

- Courier Prime
- Multi-line editing
- Text selection
- Typewriter cursor
- Distraction-free writing behavior
- Browsing through the document
- Hidden text remaining in the underlying document
- State preservation across rotation

---

# 18. Testing

The Nokia T20 is the primary development and testing device.

## Test 1 — Basic Save

1. Start the application.
2. Type several lines.
3. Press SAVE.

Expected:

A Markdown file is created under:

    <main folder>/08 Dailies/01 Inbox/

---

## Test 2 — Filename

Verify that the filename follows exactly this pattern:

    DA-YYYY-MM-DD-DAY-HH_MM_SS.md

Example:

    DA-2026-01-31-SAT-09_50_31.md

Verify:

- `DA-` prefix exists.
- Date is correct.
- Day of week is correct.
- Day of week is uppercase.
- Time contains hours, minutes and seconds.
- Time uses underscores rather than colons.
- `.md` extension exists.

---

## Test 3 — Directory Structure

Verify that the file exists inside:

    08 Dailies/01 Inbox/

and not directly inside the main folder.

---

## Test 4 — File Contents

Open the resulting Markdown file using an appropriate file viewer or file
manager.

Expected:

The file contains exactly the document text.

---

## Test 5 — Hidden Text

1. Write a long document.
2. Allow some text to become visually hidden.
3. Press SAVE.

Expected:

The saved Markdown file contains the complete document, including hidden
text.

---

## Test 6 — Continue Editing

1. Type text.
2. Press SAVE.
3. Continue typing.
4. Press SAVE again.

Expected:

The same session file is updated with the latest document contents.

---

## Test 7 — Unicode

Enter text containing Unicode characters.

Expected:

The saved Markdown file preserves the characters correctly.

---

## Test 8 — Rotation

1. Type text.
2. Rotate the Nokia T20.
3. Press SAVE.

Expected:

The complete document is saved correctly.

---

# 19. Do NOT Implement

Do not implement:

- Automatic saving
- Timing file
- NEW session behavior
- CLOSE behavior
- Templates
- Configurable main folder
- Configurable directories
- Settings screen
- File opening
- File browser
- Markdown preview
- Markdown rendering

These belong to later requirements.

---

# Completion Criteria

Requirement 05 is complete when:

1. SAVE creates a `.md` file.
2. The filename follows `DA-YYYY-MM-DD-DAY-HH_MM_SS.md`.
3. The filename uses the device's local date and time.
4. The file is stored under `08 Dailies/01 Inbox/`.
5. The required directory structure is created if necessary.
6. The file uses UTF-8.
7. The complete underlying document is saved.
8. Hidden text is included in the saved file.
9. SAVE updates the existing session file.
10. The session remains open after SAVE.
11. Save failures are handled visibly without losing the document.
12. Broad storage permissions are not unnecessarily requested.
13. Existing editor behavior remains intact.
14. The application works on the Nokia T20.

---

# After Implementation

Explain:

1. Which Android storage API was used.
2. Where the Markdown file is stored.
3. How `<main folder>` is currently represented.
4. How the timestamp filename is generated.
5. How the day-of-week abbreviation is generated.
6. How UTF-8 encoding is handled.
7. How hidden text is preserved during saving.
8. How the directory structure is created.
9. Which files were changed.
10. Whether any permissions were added.
11. Any Android-version or storage limitations.
12. How the implementation can later support a configurable main folder.

Do not implement Requirement 06.

Stop after completing this requirement.