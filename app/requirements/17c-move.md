# Requirement 17C — Move Current Document Between Projects

## Objective

Add the ability to move the **currently open saved document** from its current Project to another existing Project.

The existing Project-management dialog from Requirement 17B should be reused.

Do not create a new Project-management screen.

For each Project other than the current Project, add a:

```text
Move here
```

action.

Moving a document must also move its matching BAS/log file when one exists.

The document must remain open after the move.

Future Save, Auto-save, Close-triggered save, New-triggered save, and log updates must use the new Project location.

---

# 1. Reuse Existing Project Dialog

Requirement 17B already provides the Project-management dialog.

Reuse this existing dialog.

Do NOT create:

* A separate Move screen
* A new dropdown
* A second Project selector
* A new full-screen Activity

Add the move functionality directly to the existing Project list.

---

# 2. Project List Move Action

For every Project other than the Project containing the current document,
show:

```text
Move here
```

Example:

```text
Projects

default             [Move here]

Writing             [Current]

Research            [Move here]

Work                [Move here]
```

The exact styling should match the existing Project dialog.

---

# 3. Current Project

The Project containing the current document must be clearly identified.

For example:

```text
Writing    Current
```

The current Project must NOT display:

```text
Move here
```

because moving a document to the Project it is already in serves no purpose.

---

# 4. Saved Documents Only

The Move feature applies only to a document that has already been saved and
therefore has a real Markdown file on disk.

If the current writing session has never been saved:

* Do not create a file merely to enable Move.
* Do not silently perform a first Save.
* Do not invent a filename.
* Do not move an unsaved in-memory session.

For a fresh unsaved session, hide or disable all:

```text
Move here
```

actions.

If disabled, the UI may indicate briefly that the document must be saved
before it can be moved.

Do not add an intrusive confirmation dialog.

---

# 5. Move Markdown File

When the user taps:

```text
Move here
```

for another Project, move the current Markdown file from its existing
Project to the corresponding location inside the target Project.

Example:

Current file:

```text
Inklings/Writing/08 Dailies/01 Inbox/
    DA-2026-08-22-SAT-10_15_30.md
```

User selects:

```text
Research    Move here
```

Result:

```text
Inklings/Research/08 Dailies/01 Inbox/
    DA-2026-08-22-SAT-10_15_30.md
```

The original file must no longer remain in Writing after a successful move.

---

# 6. Preserve Filename

Do NOT rename the Markdown file.

The filename must remain exactly the same.

Example:

Before:

```text
DA-2026-08-22-SAT-10_15_30.md
```

After:

```text
DA-2026-08-22-SAT-10_15_30.md
```

Do not generate a new timestamp.

Do not create a new DA filename.

---

# 7. Associated BAS / Log File

The writing session may have a corresponding BAS/log file.

The BAS file must move together with the Markdown file if it exists.

Example Markdown:

```text
DA-2026-08-22-SAT-10_15_30.md
```

Associated BAS:

```text
BAS-2026-08-22-SAT-10_15_30.md
```

The timestamp portion associates the files.

---

# 8. Timestamp Association

The existing DA/BAS filename convention must remain unchanged.

Conceptually:

```text
DA-<timestamp>.md

BAS-<same timestamp>.md
```

Use the existing filename/timestamp logic already implemented by the app.

Do not introduce a new ID scheme solely for 17C.

---

# 9. BAS Location

The BAS/log file may exist in the Project's existing log hierarchy, for
example:

```text
Inklings/Writing/
└── 99 Operations/
    └── 99 Log/
        └── YYYY/
            └── MM/
                └── BAS-2026-08-22-SAT-10_15_30.md
```

When moving to Research, preserve the corresponding log hierarchy:

```text
Inklings/Research/
└── 99 Operations/
    └── 99 Log/
        └── YYYY/
            └── MM/
                └── BAS-2026-08-22-SAT-10_15_30.md
```

Do not flatten the log structure.

---

# 10. No BAS File

A Markdown document may not yet have an associated BAS file.

If no matching BAS file exists:

* Move the Markdown file normally.
* Do not create a BAS file just because the document is being moved.
* Do not treat the absence of BAS as an error.

---

# 11. BAS Exists

If a matching BAS file does exist:

* Move it together with the Markdown file.
* Preserve its filename.
* Preserve its contents.
* Preserve the timestamp.
* Preserve its year/month folder structure inside the target Project.

The Markdown and BAS must end up in the same Project.

---

# 12. Existing Content Must Not Change

Moving a document must not modify the document content.

Do not alter:

* Markdown text
* BAS contents
* Filename
* Timestamp
* Session duration values

This is a filesystem-location change only.

---

# 13. Keep Editor Open

After a successful move:

* Keep the document open.
* Preserve the current text.
* Preserve cursor position.
* Preserve scroll position where practical.
* Do not force the user to reopen the document.

The move should feel like changing the document's Project location, not
closing and reopening the editor.

---

# 14. Update Active Project

After a successful move, the current document/session must immediately belong
to the target Project.

Example:

Before:

```text
Current Project = Writing
```

Move to:

```text
Research
```

After:

```text
Current Project = Research
```

The Project-management dialog should reflect this if reopened.

---

# 15. Apply Target Project Font Color

After moving the document, immediately apply the target Project's configured
Font Color.

Example:

```text
Writing → blue
Research → green
```

Move document:

```text
Writing → Research
```

Expected:

```text
Editor font color → green
```

Do not modify Markdown content to apply this color.

---

# 16. Future Save Path

After a successful move, explicit Save must write to the new Project path.

Example:

Moved to:

```text
Inklings/Research/
```

Then:

```text
Ctrl+S
```

or the Save button must update:

```text
Inklings/Research/08 Dailies/01 Inbox/<same filename>
```

It must NOT recreate or update the old Writing copy.

---

# 17. Future Auto-Save Path

Auto-save must use the new Project path.

After a successful move:

```text
Auto-save
    ↓
Target Project
```

Do not continue writing to the old Project.

---

# 18. Close After Move

If the document is moved and later closed:

* Close-triggered save uses the new Project path.
* BAS/log behavior uses the new Project.
* The old Project must not receive a recreated Markdown file.

---

# 19. New After Move

If the document is moved and then the user presses New:

* The current document's final save/log behavior uses the Project it was
  moved to.
* The newly created writing session still follows the Default Project rule
  from Requirement 17B.

Moving the current document must NOT change the Default Project.

---

# 20. Default Project Must Not Change

Moving a document to another Project must never automatically change the
Default Project.

Example:

```text
Default Project = Writing
```

Current document is moved:

```text
Writing → Research
```

After the move:

```text
Default Project = Writing
Current document Project = Research
```

Future new sessions still use Writing until the user explicitly changes the
Default Project.

---

# 21. Project Metadata Must Not Change

Moving a document must not modify:

* Project Name
* Project Font Color
* Default Project status

except that the editor starts using the target Project's existing Font Color
because the document now belongs to that Project.

---

# 22. Move Action Visibility

For a saved document:

```text
Current Project → no Move here button
Other Projects → Move here
```

For an unsaved document:

```text
All Move here actions → hidden or disabled
```

Do not display an active move option that cannot safely operate.

---

# 23. No Confirmation for Normal Move

Do not show a confirmation dialog for a normal, conflict-free Move operation.

The user has explicitly selected:

```text
Move here
```

That is sufficient confirmation.

After success:

* Update active paths.
* Update Project association.
* Apply target Font Color.
* Close the Project dialog if appropriate.

---

# 24. Target File Conflict

Before moving, check whether the target Markdown path already contains a file
with the exact same filename.

Example:

```text
Inklings/Research/08 Dailies/01 Inbox/
    DA-2026-08-22-SAT-10_15_30.md
```

already exists.

In this case:

* Do NOT overwrite the existing target file.
* Do NOT generate a new filename automatically.
* Do NOT delete the source file.
* Abort the move safely.
* Inform the user that the move could not be completed because the target
  file already exists.

A simple error message is appropriate here.

---

# 25. BAS Conflict

If the target Project already contains the matching BAS file:

```text
BAS-<same timestamp>.md
```

do not overwrite it automatically.

Abort the move safely.

Do not leave the Markdown moved while the BAS remains behind.

---

# 26. Safe Move / Atomic User Experience

The Markdown and associated BAS/log file must be treated as one logical move.

If a BAS exists, the application must not report success unless both files
are successfully relocated.

From the user's perspective:

```text
Move succeeds completely
```

or:

```text
Move fails safely
```

Avoid a half-moved session.

---

# 27. Failure Handling

If moving either file fails:

* Preserve the source files.
* Do not update the current Project association.
* Do not update the active file path.
* Do not apply the target Project Font Color as if the move succeeded.
* Show a concise error indication.

Do not silently lose data.

---

# 28. Implementation Strategy for Safe Move

Use a safe filesystem move strategy appropriate to the existing Android
storage architecture.

Where direct atomic filesystem moves are supported, use them appropriately.

If true atomic movement of both files is not available, implement a safe
transaction-like sequence that avoids deleting the original until the target
operation is known to have succeeded.

The exact implementation is up to Gemini.

Data safety is more important than minimizing a few filesystem operations.

---

# 29. Source Cleanup

After a successful move:

* The old Markdown file must no longer exist.
* The old BAS file must no longer exist if one was moved.

Do not leave duplicate copies behind.

---

# 30. Empty Source Directories

Do not delete Project directories after moving their last file.

Do not delete:

```text
08 Dailies/
01 Inbox/
99 Operations/
99 Log/
YYYY/
MM/
```

merely because they become empty.

Project folder structure should remain intact.

---

# 31. Project Rescan

After a successful move, do not require a full app restart.

Update the in-memory/current Project association immediately.

If Project discovery is refreshed, the moved file should naturally appear
under the target Project path.

---

# 32. External Filesystem Changes

Before performing a move, verify that:

* Source Project still exists.
* Source Markdown file still exists.
* Target Project still exists.

This matters because Projects can be manually deleted using Android Files.

Do not rely solely on stale in-memory Project state.

---

# 33. Target Project Deleted

If the user opens Project management, then the target Project is externally
deleted before Move is tapped or completed:

* Detect that the target directory no longer exists.
* Abort safely.
* Do not recreate the externally deleted Project.
* Do not move the file elsewhere.

---

# 34. Current Project Deleted Externally

If the current document's Project was externally deleted while the document
is still open:

* Do not pretend Move can proceed normally.
* Preserve in-memory text.
* Do not silently recreate the deleted source Project.
* Report the filesystem problem safely.

Do not lose the user's current editor content.

---

# 35. Requirement 16 Compatibility

Requirement 16 remains unchanged.

A fresh unsaved empty/whitespace-only document:

```text
has no file
```

and therefore cannot be moved.

A previously saved document that was later intentionally cleared still has a
real file and may be moved.

The fact that its content is empty must not prevent the move.

---

# 36. Timer

Requirement 15 remains unchanged.

Moving a document must not:

* Reset timer
* Pause timer
* Restart timer

The timer continues independently.

---

# 37. Typewriter Sounds

Requirement 14 remains unchanged.

The Move operation itself must not produce a typewriter sound.

Typing after the move continues normally.

---

# 38. Keyboard Shortcuts

Requirement 13 remains unchanged:

```text
Ctrl+S → Save
Ctrl+N → New
Ctrl+Q → Close
```

After moving, Ctrl+S must save to the new Project.

Ctrl+N still creates a new session in the Default Project.

---

# 39. Fade and Editor State

Do not alter:

* Fade logic
* Sentence visibility
* Cursor behavior
* Typography spacing
* Font size
* Margins

Only Project Font Color changes to match the target Project.

---

# 40. No Project Rename

Do NOT implement Project renaming.

---

# 41. No In-App Project Delete

Do NOT implement Project deletion.

External deletion via Android Files remains the supported mechanism.

---

# 42. No Default Change as Side Effect

Moving a file must never change:

```text
isDefault
```

for any Project.

---

# Testing

## Test 1 — Saved Document Move

Have:

```text
Current Project = Writing
```

and:

```text
Research
```

as another Project.

Open Project management.

Expected:

```text
Writing     Current
Research    Move here
```

Tap:

```text
Research → Move here
```

Verify the Markdown file moves.

---

## Test 2 — Current Project Has No Move Action

Verify the current Project does not display an active:

```text
Move here
```

button.

---

## Test 3 — Unsaved Session

Press New.

Type something but do not save.

Open Project management.

Expected:

* Move actions hidden or disabled.
* No file created merely because the Project dialog was opened.
* No automatic Save occurs.

---

## Test 4 — Markdown Filename

Move:

```text
DA-2026-08-22-SAT-10_15_30.md
```

Verify the target filename remains exactly:

```text
DA-2026-08-22-SAT-10_15_30.md
```

---

## Test 5 — Move With BAS

Use:

```text
DA-2026-08-22-SAT-10_15_30.md
```

and matching:

```text
BAS-2026-08-22-SAT-10_15_30.md
```

Move Writing → Research.

Verify both move.

Verify neither remains in Writing.

---

## Test 6 — BAS Directory

Verify the BAS file remains under the correct:

```text
99 Operations/99 Log/YYYY/MM/
```

structure in Research.

---

## Test 7 — No BAS

Move a Markdown file that has no BAS file.

Expected:

* Markdown moves.
* No BAS file is created.
* Move succeeds.

---

## Test 8 — Editor Remains Open

Move the current document.

Expected:

* Document remains open.
* Text remains intact.
* Cursor remains usable.
* No document reload required.

---

## Test 9 — Font Color

Set:

```text
Writing → blue
Research → green
```

Move current document Writing → Research.

Expected:

```text
Editor text → green
```

Markdown content itself must remain unchanged.

---

## Test 10 — Save After Move

Move Writing → Research.

Edit the document.

Press Save.

Expected:

```text
Research copy updated
```

Verify no Writing copy is recreated.

---

## Test 11 — Auto-Save After Move

Move the document.

Edit it.

Wait for Auto-save.

Verify the target Project file is updated.

Verify the old Project is not recreated.

---

## Test 12 — Close After Move

Move the document.

Continue writing.

Press Close.

Verify Close-related save/log operations use the target Project.

---

## Test 13 — New After Move

Set:

```text
Default Project = Writing
```

Move current document:

```text
Writing → Research
```

Then press New.

Expected:

* Moved document remains in Research.
* New session belongs to Writing.
* Default Project remains Writing.

---

## Test 14 — Target Conflict

Create a target file with the same DA filename before attempting Move.

Attempt Move.

Expected:

* Move rejected.
* Target file untouched.
* Source file untouched.
* Current Project remains unchanged.

---

## Test 15 — BAS Conflict

Create a matching BAS filename in the target Project.

Attempt Move.

Expected:

* Move rejected safely.
* No half-move.
* Source DA and BAS remain intact.

---

## Test 16 — Project Deleted Before Move

Open Project dialog.

Externally delete the intended target Project.

Attempt Move.

Expected:

* Move fails safely.
* Deleted Project is not recreated.
* Current document remains in source Project.

---

## Test 17 — Previously Saved Empty Document

Save a document.

Delete all content.

Save the empty file.

Then move it.

Expected:

* Empty saved Markdown file moves successfully.
* Requirement 16 is preserved.

---

## Test 18 — Existing Features Regression

After moving a document, verify:

* Save
* Auto-save
* Close
* New
* BAS logging
* Fade
* Timer
* Typewriter sounds
* Settings
* Full-screen
* Ctrl+S
* Ctrl+N
* Ctrl+Q

continue to behave correctly.

---

# Completion Criteria

Requirement 17C is complete when:

1. The existing Project dialog is reused.
2. Other Projects show a `Move here` action.
3. The current Project does not show `Move here`.
4. Unsaved sessions cannot be moved.
5. No file is created merely to enable Move.
6. A saved Markdown file can be moved to another Project.
7. The Markdown filename remains unchanged.
8. The embedded timestamp remains unchanged.
9. A matching BAS file moves with the Markdown when present.
10. BAS location hierarchy is preserved.
11. No BAS file is created when none exists.
12. Markdown and BAS end up in the same target Project.
13. The editor remains open after the move.
14. Current Project association updates immediately.
15. Target Project Font Color is applied immediately.
16. Explicit Save uses the target Project after the move.
17. Auto-save uses the target Project after the move.
18. Close-triggered save/logging uses the target Project.
19. New still uses the configured Default Project.
20. Moving a document never changes the Default Project.
21. Project metadata is not otherwise changed.
22. Target-file conflicts are never overwritten automatically.
23. BAS conflicts are handled safely.
24. A move with an associated BAS succeeds completely or fails safely.
25. Failed moves do not lose source files.
26. Source copies are removed only after a successful move.
27. Project directories are not deleted when files move out.
28. Externally deleted target Projects are not recreated.
29. Requirement 16 remains intact.
30. Timer behavior remains intact.
31. Typewriter sounds remain intact.
32. Fade/editor behavior remains intact.
33. Keyboard shortcuts remain intact.
34. No Project rename functionality is added.
35. No in-app Project deletion functionality is added.
36. No unrelated functionality is changed.

---

# Required Source-Code Comments

Add concise comments explaining:

1. Why only saved documents can be moved.
2. Why the existing Project dialog is reused.
3. How DA and BAS files are associated by timestamp.
4. Why the DA filename/timestamp must remain unchanged.
5. Why BAS files move with their Markdown document.
6. Why absence of a BAS file is valid.
7. How the move avoids leaving a half-moved DA/BAS pair.
8. Why target conflicts are never overwritten automatically.
9. How the active document path is updated after a successful move.
10. Why future Save/Auto-save operations must use the new path.
11. Why moving a document does not change the Default Project.
12. Why the editor adopts the target Project's Font Color without modifying
    Markdown.
13. Why externally deleted Projects are never recreated as part of Move.

---

# After Implementation

Provide a short summary explaining:

1. Which files were changed.
2. How `Move here` was added to the existing Project dialog.
3. How unsaved sessions are handled.
4. How the Markdown source and destination paths are calculated.
5. How the matching BAS file is found.
6. How BAS year/month structure is preserved.
7. How conflict checking works.
8. How safe/transaction-like movement is implemented.
9. How the active document path and Project are updated.
10. How Font Color changes after a move.
11. How Save and Auto-save behave afterward.
12. How New continues to use the Default Project.
13. Confirmation that Project rename and in-app deletion were not added.
14. Confirmation that no unrelated functionality was changed.

Stop after implementing Requirement 17C.
