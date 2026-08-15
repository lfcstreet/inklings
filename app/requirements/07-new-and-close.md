# Requirement 07 — NEW and CLOSE Session Functionality

## Objective

Make the application usable as a complete writing-session application by
implementing the NEW and CLOSE buttons.

The application works with one writing session at a time.

A session begins when a new document is created and ends when the user
selects NEW or CLOSE.

A completed session cannot be reopened from within the application.

---

# 1. Writing Session

A writing session consists of:

- One editable Markdown document.
- One session-specific filename.
- A session start timestamp.
- The current editor contents.

The session filename is generated when the session starts.

The filename format remains:

    DA-YYYY-MM-DD-DAY-HH_MM_SS.md

Example:

    DA-2026-08-15-SAT-12_30_45.md

The filename does not change during the lifetime of the session.

---

# 2. NEW Button

The NEW button starts a completely new writing session.

When the user presses NEW:

1. Save the current session.
2. End the current session.
3. Clear the editor.
4. Create a new writing session.
5. Generate a new timestamp-based filename.
6. Reset the session start time.
7. Present a completely empty editor ready for writing.

The application must NOT require the user to close and reopen the application
to begin a new writing session.

---

# 3. NEW Must Not Overwrite the Previous Session

The previous session must remain saved.

For example:

    DA-2026-08-15-SAT-12_30_45.md

After pressing NEW, the new session might be:

    DA-2026-08-15-SAT-13_02_17.md

The new session must never overwrite the previous session.

---

# 4. NEW and Unsaved Changes

If the current session contains changes that have not been explicitly saved,
NEW must save them before ending the session.

The user must not lose text simply because NEW was pressed.

The save operation must use the same mechanism implemented in Requirement 06.

---

# 5. New Session State

When a new session starts:

- Editor contents are empty.
- Cursor is positioned at the beginning of the document.
- Courier Prime remains active.
- Typewriter behavior remains active.
- Distraction-free writing behavior remains active.
- The new session receives a new timestamp filename.
- The session timer is reset for the new session.

Do not carry text from the previous session into the new session.

---

# 6. CLOSE Button

The CLOSE button ends the current writing session and closes the application.

Before closing:

1. Save the current document.
2. Ensure the complete document has been written to its Markdown file.
3. End the current writing session.
4. Close the editor/application.

The user must not lose the current document by pressing CLOSE.

---

# 7. CLOSE Must Save the Complete Document

The complete underlying document must be saved.

This includes text that is currently invisible because of the
distraction-free writing behavior.

For example, if the editor visually shows only:

    Line 9
    Line 10

but the document actually contains:

    Line 1
    Line 2
    ...
    Line 10

the saved file must contain all ten lines.

---

# 8. Closed Sessions

Once CLOSE has been pressed:

- The session is considered finished.
- The application must not reopen the session automatically.
- The application must not load the previous Markdown file when started
  again.
- Starting the application again must create a new writing session.

The application does NOT need to provide an "Open previous session" feature.

There must be no file-opening functionality.

---

# 9. Application Restart

If the user:

1. Writes some text.
2. Presses CLOSE.
3. Starts the application again.

Expected behavior:

A completely new writing session is created.

The previous session remains safely stored as a Markdown file but is not
opened or displayed.

---

# 10. NEW Versus CLOSE

The intended difference is:

### NEW

    Save current session
          ↓
    End current session
          ↓
    Start new session
          ↓
    Show empty editor

### CLOSE

    Save current session
          ↓
    End current session
          ↓
    Close application

---

# 11. SAVE Button

The existing SAVE button continues to work exactly as implemented in
Requirement 06.

SAVE:

- Saves the current document.
- Keeps the session open.
- Does not create a new session.
- Does not clear the editor.
- Does not close the application.

---

# 12. Filename Handling

The session filename is generated once when the session starts.

It must not change when:

- SAVE is pressed.
- Text is edited.
- NEW is pressed.
- The device is rotated.

NEW creates a new filename.

Application restart creates a new filename.

---

# 13. Existing Storage Location

Continue using the Requirement 06 development location:

    Documents/Inklings/08 Dailies/01 Inbox/

For example:

    Documents/
    └── Inklings/
        └── 08 Dailies/
            └── 01 Inbox/
                ├── DA-2026-08-15-SAT-12_30_45.md
                └── DA-2026-08-15-SAT-13_02_17.md

Do not implement configurable storage yet.

---

# 14. Session Timing

For this requirement, record the session start time internally.

Do NOT yet create the separate timing Markdown file.

The session duration/timing file will be implemented in a later requirement.

However, the session start time must be reset whenever:

- The application starts a new session.
- NEW is pressed.

---

# 15. Confirmation

Do not introduce complicated confirmation dialogs.

For the initial implementation:

- NEW should perform the new-session operation directly.
- CLOSE should save and close directly.

If the application needs to show a short confirmation/error message because
saving failed, that is acceptable.

A future requirement may add confirmation behavior if needed.

---

# 16. Save Failure

If the application cannot save the current document:

### NEW

Do NOT silently discard the document.

The application must keep the current session active and show an error.

Do not start the new session until the current document has been safely
saved.

### CLOSE

Do NOT silently discard the document.

If the save fails, keep the application/session open and show an error.

The user must be able to try SAVE again.

---

# 17. Existing Functionality

Do not break existing functionality.

The application must continue to support:

- Courier Prime
- Multi-line editing
- Text selection
- Typewriter cursor
- Browsing mode
- Distraction-free writing
- Hidden text
- Complete document saving
- SAVE
- Rotation/state preservation

---

# 18. Testing

The Nokia T20 is the primary development and testing device.

## Test 1 — NEW

1. Start the application.
2. Type:

       First writing session.

3. Press NEW.

Expected:

- First document is saved.
- Editor becomes empty.
- A new session begins.
- A new timestamp filename is assigned.

---

## Test 2 — Verify Previous File

After pressing NEW, inspect:

    Documents/Inklings/08 Dailies/01 Inbox/

Expected:

The first session's Markdown file still exists.

---

## Test 3 — Write in New Session

After pressing NEW:

1. Type:

       Second writing session.

2. Press SAVE.

Expected:

A second Markdown file is created.

The first file must remain unchanged.

---

## Test 4 — CLOSE

1. Start a session.
2. Type several lines.
3. Press CLOSE.

Expected:

- The document is saved.
- The application closes.

---

## Test 5 — Restart

1. Open the application again.

Expected:

A completely new empty writing session is created.

The previous session must NOT appear in the editor.

---

## Test 6 — Hidden Text

1. Create a long document.
2. Allow text to become visually hidden.
3. Press NEW or CLOSE.
4. Inspect the saved file.

Expected:

The complete underlying document is present.

---

## Test 7 — Rotation

1. Start a session.
2. Type text.
3. Rotate the Nokia T20.
4. Press NEW or CLOSE.

Expected:

The document is saved correctly and no text is lost.

---

## Test 8 — Multiple Sessions

Create several sessions using NEW.

Expected:

Each session produces a separate Markdown file.

No previous session is overwritten.

---

# 19. Do NOT Implement

Do not implement:

- Automatic saving
- Two-minute auto-save
- Timing Markdown file
- Templates
- Template directory
- Configurable storage
- Settings screen
- File opening
- File browser
- Markdown preview
- Markdown rendering
- Session history

These belong to later requirements.

---

# Completion Criteria

Requirement 07 is complete when:

1. NEW saves the current session.
2. NEW starts a completely new empty session.
3. NEW generates a new timestamp filename.
4. The previous session remains saved.
5. CLOSE saves the current session.
6. CLOSE ends the session.
7. CLOSE closes the application.
8. Restarting the application creates a new session.
9. A previous session is never automatically reopened.
10. Hidden text is included when saving.
11. Save failures do not cause data loss.
12. Multiple sessions create separate Markdown files.
13. Existing editor functionality remains intact.
14. The application works correctly on the Nokia T20.

---

# After Implementation

Provide a short summary explaining:

1. How a writing session is represented.
2. How NEW ends and starts sessions.
3. How CLOSE ends the current session.
4. How the session filename is generated.
5. How save failures are handled.
6. How the application ensures previous sessions are not reopened.
7. Which files were changed.
8. Any Android-specific considerations.

Do not implement Requirement 08.

Stop after completing this requirement.