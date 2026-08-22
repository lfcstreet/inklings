# Requirement 16 — Empty Save Prevention and Intentional Clearing

## Objective

Prevent the application from creating a file when a **new document has never
been saved and contains no meaningful content**.

However, if a document **has already been saved previously**, the user must be
allowed to intentionally clear its contents and save the now-empty document.

This distinction is essential.

There are therefore two different cases:

1. **Fresh, never-saved document + empty content → do not create a file.**
2. **Previously saved document + subsequently empty content → save the empty content.**

This behavior must apply consistently to:

* Explicit Save
* Auto-save
* Save triggered by Close
* Save triggered by New

---

# 1. Definition of Empty Content

A document is considered empty if it contains no meaningful characters after
whitespace is taken into account.

The following must be treated as empty:

* Empty string
* Spaces
* Tabs
* Newlines
* Carriage returns
* Any combination of whitespace characters

Examples:

```text
""
```

```text
"   "
```

```text
"\t\t"
```

```text
"\n\n\n"
```

```text
"  \t\n  "
```

Use a proper whitespace-aware check rather than checking only for the literal
space character.

---

# 2. Meaningful Content

A document is considered non-empty if it contains at least one meaningful
non-whitespace character.

Examples:

```text
a
```

```text
Hello
```

```text
.
```

```text
-
```

```text
1
```

```text
# Heading
```

Any such content must be saved normally.

---

# 3. Fresh Document

A **fresh document** is a document that has never been saved to its final
timestamp-based file.

For a fresh document:

### Empty content

If the content contains only whitespace:

* Do not create a file.
* Do not generate a new filename solely because Save was requested.
* Do not create a log file.
* Do not modify any existing file.
* Do not show a warning.
* Do not show a confirmation.
* Simply ignore the save operation.

### Non-empty content

If the document contains meaningful content:

* Save it normally.
* Generate its normal timestamp-based filename.
* Perform the normal log-file behavior.

---

# 4. Previously Saved Document

A document becomes a **previously saved document** as soon as it has been
successfully saved to its actual file.

Once this has happened, the document is considered associated with that file.

This association must remain even if the user subsequently deletes all text.

---

# 5. Previously Saved Document — Clearing the Content

If a previously saved document is edited so that its content becomes empty,
the user must be allowed to intentionally clear the file.

For example:

### Initial state

```text
My document.md

Hello world
```

The user deletes everything.

The editor now contains:

```text
```

The user presses Save.

Expected result:

```text
My document.md

```

The existing file must be updated with the empty content.

**Do NOT skip this save.**

---

# 6. Existing File Must Be Overwritten When Intentionally Cleared

For a previously saved document whose content has become empty:

* Save the empty content to the existing file.
* Preserve the existing filename.
* Preserve the existing file location.
* Do not create a new timestamp-based filename.
* Do not silently ignore the Save operation.

This allows the user to intentionally empty an existing document.

---

# 7. Explicit Save

When the user presses **Save**:

### Fresh document

```text
Empty → Do nothing
Non-empty → Save normally
```

### Previously saved document

```text
Empty → Save empty content to existing file
Non-empty → Save normally
```

---

# 8. Auto-Save

Auto-save must follow exactly the same distinction.

### Fresh document

If the document is empty:

* Do nothing.
* Do not create a file.
* Do not create a log file.

If the document contains meaningful content:

* Save normally.

### Previously saved document

If the document becomes empty:

* Auto-save must save the empty content to the existing file.
* The existing file is intentionally cleared.

If the document contains content:

* Auto-save normally.

No notification should be displayed.

---

# 9. Close

When Close causes the current document to be saved:

### Fresh document + empty

Do not create a file.

### Fresh document + non-empty

Save normally.

### Previously saved + empty

Save the empty content to the existing file.

### Previously saved + non-empty

Save normally.

Close must not prevent the user from intentionally clearing a previously
saved document.

---

# 10. New

When New causes the current document to be saved:

### Fresh document + empty

Do not create a file.

### Fresh document + non-empty

Save normally.

### Previously saved + empty

Save the empty content to the existing file.

### Previously saved + non-empty

Save normally.

After this save decision, New proceeds normally.

---

# 11. Save State Tracking

The application must explicitly know whether the current document has
already been saved.

Use an appropriate state such as:

```text
isSavedFile = true / false
```

or equivalent.

The exact implementation is up to the existing application architecture.

The important distinction is:

```text
Fresh document:
    no associated saved file

Previously saved document:
    associated with an existing saved file
```

Do not determine this solely from whether the current text is empty.

---

# 12. Important: Do Not Use Content as the Saved-State Indicator

The application must NOT implement logic such as:

```text
if content is empty:
    never save
```

That would incorrectly prevent intentional clearing of an existing document.

Instead:

```text
if document has never been saved:
    if content is empty:
        skip save
    else:
        save normally

if document has previously been saved:
    save normally
```

The empty-content check is therefore only a restriction on **creating the
first file**, not on updating an existing file.

---

# 13. Log File Behavior

The existing writing-time log behavior from Requirement 09 remains in place.

For a **fresh document that is never saved because it contains only
whitespace**:

* Do not create a log file.

For a document that has previously been saved:

* Continue using the existing log behavior when the document is saved,
  including when the user intentionally clears it.

Do not create a separate special "empty document" log format.

---

# 14. File Naming

Do not change the existing file naming convention.

For example:

```text
DA-2026-01-31-SAT-09_50_31.md
```

or the current filename convention implemented by the application.

A fresh empty document must not receive a filename merely because Save,
Auto-save, Close, or New was invoked.

A previously saved document must retain its existing filename even when its
content is cleared.

---

# 15. File Location

Do not change the existing file location or folder behavior.

The existing configured/default location remains unchanged.

---

# 16. No User Notification

When a fresh empty document is not saved:

* No toast.
* No dialog.
* No confirmation.
* No warning.
* No notification.

The operation should simply do nothing.

When a previously saved document is intentionally cleared:

* Do not show a special warning.
* Save normally.

The user is explicitly choosing to save the current document state.

---

# 17. Existing File Integrity

For a previously saved document:

```text
Saved content
    ↓
User deletes all content
    ↓
Save / Auto-save / Close / New
    ↓
Existing file becomes empty
```

Do not:

* Delete the file.
* Create a replacement file with a new name.
* Create a second empty file.
* Ignore the save.
* Restore the old content.

The existing file must remain at the same path and simply contain the newly
saved empty content.

---

# 18. Markdown Compatibility

The empty-content check must operate on the actual document text.

Whitespace-only Markdown is considered empty.

Meaningful Markdown is not empty.

For example:

```markdown
# Heading
```

must be considered non-empty.

A document containing only whitespace around Markdown content must still be
considered non-empty.

---

# 19. Interaction With Timer

Requirement 15 remains unchanged.

The timer must not determine whether a document is considered empty.

Timer behavior must continue independently.

---

# 20. Interaction With Typewriter Sounds

Requirement 14 remains unchanged.

This requirement must not modify:

* Typewriter sound behavior.
* Sound settings.
* Space sound.
* Backspace sound.

---

# 21. Interaction With Writing-Time Tracking

Requirement 09 remains unchanged except for the save distinction described
in this requirement.

A fresh document that is never saved because it remains empty must not create
a log file.

A previously saved document may continue to produce its normal log entry when
saved, even if the user has intentionally cleared its contents.

---

# Testing

## Test 1 — Fresh Empty Document

Open New.

Type nothing.

Press Save.

Expected:

* No file created.
* No filename generated.
* No log file created.

---

## Test 2 — Fresh Spaces Only

Type:

```text
     
```

Press Save.

Expected:

* No file created.
* No log file created.

---

## Test 3 — Fresh Blank Lines

Enter several blank lines.

Press Save.

Expected:

* No file created.
* No log file created.

---

## Test 4 — Fresh Non-Empty Document

Type:

```text
Hello
```

Press Save.

Expected:

* Normal file created.
* Normal filename used.
* Normal log behavior occurs.

---

## Test 5 — Previously Saved, Then Cleared

Create a document containing:

```text
Hello
```

Save it.

Then delete all content.

Press Save.

Expected:

* Existing file is updated.
* Existing filename is preserved.
* Existing file now contains empty content.
* No new file is created.

---

## Test 6 — Previously Saved, Then Auto-Saved Empty

Create and save:

```text
Hello
```

Delete all content.

Wait for auto-save.

Expected:

* Existing file is updated with empty content.
* No new filename is generated.
* No second file is created.

---

## Test 7 — Previously Saved, Then Close

Create and save:

```text
Hello
```

Delete all content.

Close the document.

Expected:

* Existing file is saved as empty.
* Existing filename is preserved.
* Close proceeds normally.

---

## Test 8 — Previously Saved, Then New

Create and save:

```text
Hello
```

Delete all content.

Press New.

Expected:

* Existing file is updated to empty.
* Existing filename is preserved.
* New document opens normally.

---

## Test 9 — Existing File Must Not Be Deleted

Create and save a document.

Delete all text.

Save.

Verify that:

* The file still exists.
* The filename is unchanged.
* The path is unchanged.
* The file contains the newly saved empty content.

---

## Test 10 — Reopen Cleared File

Save a document containing:

```text
Hello
```

Clear it and save.

Close the application.

Reopen the cleared file.

Expected:

* The file opens successfully.
* The editor contains empty content.
* No old "Hello" content is restored.

---

## Test 11 — Fresh Whitespace With Auto-Save

Create a fresh document.

Enter only spaces.

Wait for auto-save.

Expected:

* No file is created.
* No log file is created.

---

## Test 12 — Existing File With Whitespace

Save:

```text
Hello
```

Then replace the content with spaces only.

Save.

Expected:

* Existing file is overwritten with the whitespace content.
* It is NOT treated as a request to skip saving.

---

# Completion Criteria

Requirement 16 is complete when:

1. Fresh empty documents are not saved.
2. Fresh whitespace-only documents are not saved.
3. Fresh empty documents do not receive a filename.
4. Fresh empty documents do not create a log file.
5. Fresh non-empty documents save normally.
6. A previously saved document may be intentionally cleared.
7. Saving a previously saved empty document updates the existing file.
8. Auto-save can intentionally clear a previously saved file.
9. Close can intentionally clear a previously saved file.
10. New can intentionally clear the previously saved file before creating the
    new document.
11. The existing filename is preserved when a saved document is cleared.
12. The existing file is not deleted and recreated under a different name.
13. The application tracks whether the document has an associated saved file.
14. Empty-content status is NOT used as the saved-state indicator.
15. All save pathways use the same saved-state/content validation logic.
16. No user notification is shown when a fresh empty save is skipped.
17. No unrelated functionality is changed.

---

# Required Source-Code Comments

Add concise comments explaining:

1. Why a fresh whitespace-only document must not create a file.
2. Why a previously saved document must still be allowed to become empty.
3. Why the application tracks whether a real file already exists for the
   current document.
4. Why the empty-content check must not be used as the saved-state indicator.
5. Why all save pathways share the same decision logic.
6. Why an existing file is updated rather than deleted/recreated when the user
   intentionally clears it.
7. Why fresh empty documents do not create log files.

---

# After Implementation

Provide a short summary explaining:

1. Which files were modified.
2. How the application distinguishes fresh and previously saved documents.
3. How explicit Save behaves in both cases.
4. How Auto-save behaves in both cases.
5. How Close behaves in both cases.
6. How New behaves in both cases.
7. How log-file creation behaves.
8. How an intentionally cleared existing file is preserved and updated.
9. How whitespace-only content is detected.
10. Confirmation that no unrelated functionality was changed.

Stop after implementing this requirement.
