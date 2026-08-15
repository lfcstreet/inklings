# Requirement 06 — Save to Development Storage Location

## Objective

Implement actual Markdown file saving to a specific, user-visible location
on the Android device.

This requirement is intended to verify that the application can create,
write, update and persist Markdown files correctly.

The storage location is FIXED for this requirement.

Configurable storage will be implemented in a later requirement.

---

# 1. Development Storage Location

For this requirement, the main folder is:

    Documents/Inklings/

The main Markdown files must be stored under:

    Documents/Inklings/08 Dailies/01 Inbox/

The resulting directory structure is:

    Documents/
    └── Inklings/
        └── 08 Dailies/
            └── 01 Inbox/
                └── DA-YYYY-MM-DD-DAY-HH_MM_SS.md

Example:

    Documents/
    └── Inklings/
        └── 08 Dailies/
            └── 01 Inbox/
                └── DA-2026-08-15-SAT-12_30_45.md

---

# 2. Important

This is a DEVELOPMENT location only.

Do not create a settings screen.

Do not allow the user to configure the location yet.

The location will be made configurable in a later requirement.

Keep the storage/path logic isolated so that the hard-coded development
location can later be replaced by a configurable main folder.

---

# 3. Android Storage

Use the appropriate modern Android storage API for writing to a
user-visible Documents location.

Do NOT use deprecated unrestricted filesystem access if a modern Android API
can accomplish the requirement.

Do NOT request broad storage permissions unnecessarily.

The implementation must work on the Nokia T20.

If Android requires the user to explicitly grant access to the Documents
folder using the Android system file/folder picker, explain this clearly
before implementing a workaround.

Do not silently substitute an application-private directory.

The purpose of this requirement is specifically to allow the developer to
verify the created Markdown files outside the application.

---

# 4. Directory Creation

The application must ensure that the following directory structure exists:

    Documents/
    └── Inklings/
        └── 08 Dailies/
            └── 01 Inbox/

If the directories do not exist, create them using the appropriate Android
storage mechanism.

Do not fail simply because the directories have not previously been created.

---

# 5. Filename

The filename format from Requirement 05 remains mandatory:

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

Use the device's local date and time.

Do not use colons in the filename.

---

# 6. File Contents

The Markdown file must contain the complete underlying document.

Only the actual text should be saved.

Do NOT save:

- Cursor position
- Scroll position
- Hidden/visible state
- Compose state
- UI information
- Formatting information that is not part of the document text

---

# 7. Hidden Text

The distraction-free editor may visually hide parts of the document.

This must have absolutely no effect on saving.

For example, if the complete document is:

    Line 1
    Line 2
    Line 3
    Line 4
    Line 5
    Line 6

and only:

    Line 5
    Line 6

is currently visible, SAVE must still write:

    Line 1
    Line 2
    Line 3
    Line 4
    Line 5
    Line 6

to the Markdown file.

---

# 8. SAVE Button

The existing SAVE button must:

1. Ensure the required directory exists.
2. Create the session file if it does not exist.
3. Write the complete document.
4. Update the existing file on subsequent saves.
5. Keep the current session open.

SAVE must NOT:

- Close the application.
- Start a new session.
- Clear the editor.
- Change the filename.

---

# 9. UTF-8

The Markdown file must be written using UTF-8 encoding.

Verify that the following are preserved correctly:

- Normal text
- Newlines
- Markdown syntax
- Unicode characters
- Emoji where supported

---

# 10. Save Feedback

After a successful SAVE, provide simple confirmation to the user.

For example:

    Saved

or:

    Saved to Documents/Inklings/08 Dailies/01 Inbox/

Do not create a complicated save dialog.

If saving fails:

- Show a clear error message.
- Keep the document in memory.
- Do not clear the editor.
- Allow SAVE to be attempted again.

---

# 11. File Verification

The saved file must be visible using a normal Android file manager or other
appropriate file-access mechanism.

The developer must be able to navigate to:

    Documents/
        Inklings/
            08 Dailies/
                01 Inbox/

and see the generated `.md` file.

The file should be independently openable/readable outside the application.

The application itself must NOT implement file opening.

---

# 12. Existing Functionality

Do not change existing editor behavior.

The application must continue to support:

- Courier Prime
- Multi-line editing
- Typewriter cursor
- Distraction-free writing
- Browsing mode
- Hidden text
- Normal text editing
- Rotation/state preservation

---

# 13. Automatic Saving

Do NOT implement automatic saving yet.

Automatic saving every two minutes belongs to a later requirement.

---

# 14. Templates

Do NOT implement templates yet.

Templates will be added later:

    template/
    ├── maintemplate.md
    └── timetemplate.md

---

# 15. NEW

Do NOT implement final NEW behavior yet.

---

# 16. CLOSE

Do NOT implement final CLOSE behavior yet.

---

# 17. Configurable Storage

Do NOT implement configurable storage yet.

The current location:

    Documents/Inklings/

is intentionally fixed for development.

A later requirement will allow the user to select/configure the main folder.

The eventual structure will remain:

    <main folder>/
    └── 08 Dailies/
        └── 01 Inbox/

---

# 18. Testing

The Nokia T20 is the primary development and testing device.

## Test 1 — Create File

1. Start the application.
2. Type:

       Hello world

3. Press SAVE.

Expected:

A `.md` file appears under:

    Documents/Inklings/08 Dailies/01 Inbox/

---

## Test 2 — Verify Filename

Verify that the filename follows:

    DA-YYYY-MM-DD-DAY-HH_MM_SS.md

---

## Test 3 — Verify Contents

Open the file using a file manager or other external method.

Expected contents:

    Hello world

---

## Test 4 — Update File

1. Type additional text.
2. Press SAVE again.

Expected:

The same session file is updated.

A second file must NOT be created.

---

## Test 5 — Hidden Text

1. Create a document containing many lines.
2. Allow text to become visually hidden.
3. Press SAVE.
4. Inspect the Markdown file externally.

Expected:

The complete document is present, including visually hidden text.

---

## Test 6 — Unicode

Enter something such as:

    Hello — world
    नमस्ते
    😊

Press SAVE.

Verify that the file preserves the characters correctly.

---

## Test 7 — Rotation

1. Type text.
2. Rotate the Nokia T20.
3. Press SAVE.
4. Inspect the resulting file.

Expected:

The complete document is saved correctly.

---

# 19. Do NOT Implement

Do not implement:

- Automatic save
- Two-minute auto-save
- Timing file
- Templates
- Template directory
- Configurable main folder
- Settings
- File opening
- File browser
- Markdown preview
- Markdown rendering
- NEW session behavior
- Final CLOSE behavior

---

# Completion Criteria

Requirement 06 is complete when:

1. The application can create a Markdown file.
2. The file is stored under:

       Documents/Inklings/08 Dailies/01 Inbox/

3. The directory structure is created if necessary.
4. The filename follows the required timestamp format.
5. The file is visible outside the application.
6. The file contains the complete underlying document.
7. Hidden text is included.
8. SAVE updates the same session file.
9. UTF-8 text is preserved.
10. Save failures do not lose the document.
11. The application does not unnecessarily request broad storage access.
12. Existing editor functionality remains intact.
13. The implementation works on the Nokia T20.

---

# After Implementation

Provide a short summary explaining:

1. Which Android storage API was used.
2. Where the files are physically stored.
3. How the directory structure is created.
4. How Android permissions/access are handled.
5. How the timestamp filename is generated.
6. How the complete document is obtained for saving.
7. Which files were changed.
8. Whether any permissions were added.
9. Any Android-version limitations.
10. How this implementation can later support a configurable main folder.

Do not implement Requirement 07.

Stop after completing this requirement.