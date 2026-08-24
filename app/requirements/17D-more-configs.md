# Requirement 17D — Configurable Project Paths and File Prefixes

## Objective

Extend each Project's `.inklings-project.json` metadata so that every Project
can define:

1. The subfolder used for Markdown documents.
2. The base subfolder used for BAS/log files.
3. The filename prefix used for Markdown documents.
4. The filename prefix used for BAS/log files.

These settings are intentionally **JSON-only configuration**.

Do NOT add a user-facing UI for these properties.

The user will manually edit:

```text
Inklings/<Project>/.inklings-project.json
```

when customization is required.

The application must automatically populate all existing Projects and all new
Projects with the current behavior as the default configuration.

---

# 1. New Project Metadata Properties

Extend `.inklings-project.json` with:

```json
{
  "fontColor": "#4285F4",
  "isDefault": false,
  "documentSubfolder": "08 Dailies/01 Inbox",
  "logSubfolder": "99 Operations/99 Log",
  "documentPrefix": "DA",
  "logPrefix": "BAS"
}
```

The exact JSON property ordering does not matter.

The property meanings are:

```text
documentSubfolder
    Relative Project path where Markdown documents are stored.

logSubfolder
    Relative Project base path where BAS/log files are stored.

documentPrefix
    Filename prefix for Markdown documents belonging to the Project.

logPrefix
    Filename prefix for BAS/log files belonging to the Project.
```

---

# 2. Default Values

The default values must preserve the application's existing behavior:

```json
{
  "documentSubfolder": "08 Dailies/01 Inbox",
  "logSubfolder": "99 Operations/99 Log",
  "documentPrefix": "DA",
  "logPrefix": "BAS"
}
```

Requirement 17D must therefore introduce no visible change unless the user
manually edits these values.

---

# 3. Example Project Metadata

A normal Project may contain:

```json
{
  "fontColor": "#4285F4",
  "isDefault": true,
  "documentSubfolder": "08 Dailies/01 Inbox",
  "logSubfolder": "99 Operations/99 Log",
  "documentPrefix": "DA",
  "logPrefix": "BAS"
}
```

A customized Project may contain:

```json
{
  "fontColor": "#4285F4",
  "isDefault": false,
  "documentSubfolder": "Research Notes/Inbox",
  "logSubfolder": "Research Logs",
  "documentPrefix": "RN",
  "logPrefix": "RL"
}
```

No additional UI is required for this customization.

---

# 4. Paths Are Relative to the Project

Both configured subfolder paths are relative to:

```text
Inklings/<Project>/
```

For example:

```json
"documentSubfolder": "Research Notes/Inbox"
```

for Project:

```text
Research
```

means:

```text
Inklings/Research/Research Notes/Inbox/
```

It must NOT be interpreted relative to:

* Device root
* `Inklings/` root
* Internal application storage
* Current working directory

The Project directory is always the base path.

---

# 5. Document Subfolder

The setting:

```json
"documentSubfolder": "08 Dailies/01 Inbox"
```

defines where new Markdown files for that Project are created.

Default result:

```text
Inklings/<Project>/08 Dailies/01 Inbox/
```

If manually changed to:

```json
"documentSubfolder": "Writing/Inbox"
```

new documents belonging to that Project must instead be created under:

```text
Inklings/<Project>/Writing/Inbox/
```

---

# 6. Log Subfolder

The setting:

```json
"logSubfolder": "99 Operations/99 Log"
```

defines the base folder for writing-session logs.

The existing year/month organization remains unchanged.

Default result:

```text
Inklings/<Project>/
└── 99 Operations/
    └── 99 Log/
        └── YYYY/
            └── MM/
                └── <log file>
```

If manually changed to:

```json
"logSubfolder": "Logs/Writing"
```

the resulting path becomes:

```text
Inklings/<Project>/
└── Logs/
    └── Writing/
        └── YYYY/
            └── MM/
                └── <log file>
```

Requirement 17D changes only the Project-relative base log path.

The existing `YYYY/MM` hierarchy remains appended after it.

---

# 7. Document Filename Prefix

The setting:

```json
"documentPrefix": "DA"
```

controls the filename prefix for Markdown documents belonging to that Project.

With:

```json
"documentPrefix": "DA"
```

the existing behavior remains:

```text
DA-2026-08-24-MON-11_30_45.md
```

If changed to:

```json
"documentPrefix": "JOURNAL"
```

a new document created in that Project should use:

```text
JOURNAL-2026-08-24-MON-11_30_45.md
```

Preserve the existing timestamp format exactly.

Only the prefix changes.

---

# 8. Log Filename Prefix

The setting:

```json
"logPrefix": "BAS"
```

controls the filename prefix for BAS/log files belonging to that Project.

Default:

```text
BAS-<existing timestamp format>.md
```

If changed to:

```json
"logPrefix": "SESSION"
```

new log files should use:

```text
SESSION-<same session timestamp>.md
```

Do not modify log contents because of prefix changes.

---

# 9. Timestamp Is the Permanent Session Identity

The Markdown document and its corresponding writing log must continue to
share the same session timestamp.

For example:

```text
JOURNAL-2026-08-24-MON-11_30_45.md
SESSION-2026-08-24-MON-11_30_45.md
```

The permanent association is:

```text
2026-08-24-MON-11_30_45
```

The prefixes are Project-specific naming conventions.

Do NOT use:

```text
DA
BAS
RN
RL
JOURNAL
SESSION
```

as the primary mechanism for associating the document with its log.

The shared timestamp is authoritative.

---

# 10. Prefix Changes and Existing Files

If the user changes a Project's prefix manually in JSON, existing files must
NOT be automatically renamed merely because the metadata changed.

Example:

Existing file:

```text
DA-2026-08-20-THU-10_00_00.md
```

User changes:

```json
"documentPrefix": "JOURNAL"
```

The existing file remains:

```text
DA-2026-08-20-THU-10_00_00.md
```

A newly created file in that Project becomes:

```text
JOURNAL-2026-08-24-MON-11_30_45.md
```

Changing configuration alone does not trigger bulk renaming.

---

# 11. Path Changes and Existing Files

The same principle applies to configured paths.

Suppose an existing file is stored under:

```text
08 Dailies/01 Inbox/
```

and the user changes:

```json
"documentSubfolder": "Writing/Inbox"
```

Do NOT automatically relocate all existing files.

The new configured path applies to subsequently created documents and to
documents explicitly moved into the Project under Requirement 17C.

Existing files otherwise remain where they currently are.

---

# 12. Existing Open Documents Retain Their Current Path

If a document is already open and has already been saved, manually changing
the Project's `documentSubfolder` must NOT redirect that open existing file.

Its actual existing path remains authoritative.

Explicit Save and Auto-save continue updating the current existing file.

The new configured path is used for:

* New documents
* Documents explicitly moved into that Project

---

# 13. New Writing Session

When a new writing session is created:

1. Determine the session's Project according to Requirement 17B.
2. Read that Project's `.inklings-project.json`.
3. Use `documentSubfolder`.
4. Use `documentPrefix`.
5. Generate the existing session timestamp.
6. Create the Markdown file only when normal save rules permit it.

Conceptually:

```text
New session
    ↓
Assigned Project
    ↓
.inklings-project.json
    ↓
documentSubfolder
documentPrefix
    ↓
documentPrefix-<timestamp>.md
```

Requirement 16 empty-file behavior remains unchanged.

---

# 14. New BAS / Log File

When the session log is created:

1. Use the same Project as the document/session.
2. Read `logSubfolder`.
3. Append the existing `YYYY/MM` hierarchy.
4. Use `logPrefix`.
5. Use the same session timestamp as the document.

Conceptually:

```text
Project
   ↓
logSubfolder
   ↓
YYYY/MM
   ↓
logPrefix-<same timestamp>.md
```

---

# 15. Project Creation

Whenever Requirement 17B creates a new Project, automatically include:

```json
{
  "documentSubfolder": "08 Dailies/01 Inbox",
  "logSubfolder": "99 Operations/99 Log",
  "documentPrefix": "DA",
  "logPrefix": "BAS"
}
```

in its:

```text
.inklings-project.json
```

The user must not need to add these properties manually to every new Project.

---

# 16. Existing Project Metadata Upgrade

Projects created before Requirement 17D may contain:

```json
{
  "fontColor": "#4285F4",
  "isDefault": false
}
```

When such a Project is discovered, safely add/use defaults for missing 17D
properties.

Conceptually:

```json
{
  "fontColor": "#4285F4",
  "isDefault": false,
  "documentSubfolder": "08 Dailies/01 Inbox",
  "logSubfolder": "99 Operations/99 Log",
  "documentPrefix": "DA",
  "logPrefix": "BAS"
}
```

Do not lose or reset existing metadata.

---

# 17. Missing Individual Properties

A metadata file may contain only some new properties.

Example:

```json
{
  "fontColor": "#4285F4",
  "isDefault": false,
  "documentPrefix": "JOURNAL"
}
```

Do NOT overwrite:

```text
documentPrefix = JOURNAL
```

Use defaults only for the missing values:

```text
documentSubfolder = 08 Dailies/01 Inbox
logSubfolder      = 99 Operations/99 Log
logPrefix         = BAS
```

Valid manually configured values must be preserved.

---

# 18. Manual JSON Editing Is Explicitly Supported

The user is expected to manually edit:

```text
Inklings/<Project>/.inklings-project.json
```

using an external text editor.

Therefore:

* Keep the JSON human-readable.
* Keep the configuration values straightforward.
* Do not encode paths or prefixes into opaque identifiers.
* Do not reset valid manually edited settings on startup.
* Preserve valid custom values.

---

# 19. Configuration Reload

Because metadata may be changed externally, the application must re-read
Project metadata during normal reconciliation.

At minimum, re-read it:

* On application startup
* Whenever the Project-management dialog performs its existing refresh

A continuous filesystem watcher is not required.

---

# 20. No UI for These Settings

Do NOT add Project UI controls for:

* `documentSubfolder`
* `logSubfolder`
* `documentPrefix`
* `logPrefix`

Do NOT add them to Settings.

Do NOT add:

* Path picker
* Prefix editor
* Folder chooser
* Advanced settings screen

These are intentionally manual JSON configuration options.

---

# 21. Path Safety

Configured subfolder paths must remain inside the Project directory.

Reject unsafe values such as:

```text
../../OtherFolder
```

or:

```text
/storage/emulated/0/OtherFolder
```

Do not permit path traversal outside:

```text
Inklings/<Project>/
```

---

# 22. Valid Nested Paths

Valid relative nested paths are allowed.

Examples:

```text
Notes
Notes/Daily
Writing/Inbox
Research Notes/Inbox
System/Logs/Writing
```

The application should create missing configured subdirectories when they
are needed for writing/moving files.

---

# 23. Prefix Validation

Filename prefixes must be safe filename components.

Valid examples:

```text
DA
BAS
RN
RL
NOTE
JOURNAL
SESSION
```

Do not allow path separators or path traversal in a filename prefix.

Examples that must be rejected or safely handled:

```text
../DA
folder/DA
DA\TEST
```

---

# 24. Empty Prefix

If manual configuration contains:

```json
"documentPrefix": ""
```

or:

```json
"logPrefix": ""
```

do not generate malformed filenames.

Use safe fallback defaults:

```text
documentPrefix → DA
logPrefix      → BAS
```

or otherwise fail safely.

Do not crash.

---

# 25. Invalid JSON

Because the user may manually edit `.inklings-project.json`, malformed JSON
must be handled safely.

If the metadata file is invalid:

* Do not crash.
* Do not delete the Project.
* Do not delete Markdown files.
* Do not delete BAS files.
* Do not silently move files.
* Use safe defaults where required.
* Produce a concise error/log useful for debugging.

Do not destructively overwrite the manually edited metadata file if avoidable.

---

# 26. Requirement 17C — Target Project Configuration

Requirement 17C must now use the target Project's configured:

```text
documentSubfolder
logSubfolder
documentPrefix
logPrefix
```

when moving a document into another Project.

A Project move is therefore not merely a directory move.

It also adopts the target Project's naming convention.

---

# 27. Rename Markdown File During Project Move

When moving a saved Markdown document to another Project:

1. Preserve the original session timestamp.
2. Read the target Project's `documentPrefix`.
3. Construct a new target filename using the target prefix and original
   timestamp.
4. Move the file into the target Project's `documentSubfolder`.

Example:

Source Project configuration:

```json
{
  "documentPrefix": "DA"
}
```

Source file:

```text
DA-2026-08-24-MON-11_30_45.md
```

Target Project configuration:

```json
{
  "documentPrefix": "RN"
}
```

After move:

```text
RN-2026-08-24-MON-11_30_45.md
```

The timestamp must remain:

```text
2026-08-24-MON-11_30_45
```

---

# 28. Rename BAS / Log File During Project Move

If the moved document has an associated BAS/log file:

1. Preserve the same original session timestamp.
2. Read the target Project's `logPrefix`.
3. Construct a new log filename using the target prefix.
4. Move it into the target Project's configured `logSubfolder/YYYY/MM`.

Example:

Source:

```text
BAS-2026-08-24-MON-11_30_45.md
```

Target configuration:

```json
"logPrefix": "RL"
```

After move:

```text
RL-2026-08-24-MON-11_30_45.md
```

The timestamp remains unchanged.

---

# 29. Full Move Example

Source Project metadata:

```json
{
  "documentSubfolder": "08 Dailies/01 Inbox",
  "logSubfolder": "99 Operations/99 Log",
  "documentPrefix": "DA",
  "logPrefix": "BAS"
}
```

Source files:

```text
Inklings/Writing/08 Dailies/01 Inbox/
    DA-2026-08-24-MON-11_30_45.md

Inklings/Writing/99 Operations/99 Log/2026/08/
    BAS-2026-08-24-MON-11_30_45.md
```

Target Project metadata:

```json
{
  "documentSubfolder": "Research Notes/Inbox",
  "logSubfolder": "Research Logs",
  "documentPrefix": "RN",
  "logPrefix": "RL"
}
```

Move:

```text
Writing → Research
```

Expected result:

```text
Inklings/Research/Research Notes/Inbox/
    RN-2026-08-24-MON-11_30_45.md

Inklings/Research/Research Logs/2026/08/
    RL-2026-08-24-MON-11_30_45.md
```

The Markdown contents remain unchanged.

The BAS contents remain unchanged.

The session timestamp remains unchanged.

---

# 30. Prefix Changes Are Different From Explicit Project Moves

There is an important distinction:

### User merely edits the JSON prefix

Existing files are NOT automatically renamed.

Example:

```text
DA-<timestamp>.md
```

stays:

```text
DA-<timestamp>.md
```

after changing `documentPrefix`.

### User explicitly moves a file into another Project

The file MUST adopt the destination Project's configured prefix.

Example:

```text
DA-<timestamp>.md
```

moved to a Project with:

```text
documentPrefix = RN
```

becomes:

```text
RN-<same timestamp>.md
```

This distinction is intentional.

---

# 31. Target Conflict Check Uses New Filename

Requirement 17C conflict detection must use the destination filename after
applying the target Project prefix.

Example:

Source:

```text
DA-2026-08-24-MON-11_30_45.md
```

Target prefix:

```text
RN
```

Before moving, check for:

```text
Inklings/Research/<documentSubfolder>/
    RN-2026-08-24-MON-11_30_45.md
```

Do NOT check only for the source filename.

If the calculated target filename already exists:

* Do not overwrite it.
* Do not generate another timestamp.
* Abort the move safely.

---

# 32. BAS Conflict Check Uses New Prefix

Likewise, if the associated source log is:

```text
BAS-2026-08-24-MON-11_30_45.md
```

and target log prefix is:

```text
RL
```

check for:

```text
RL-2026-08-24-MON-11_30_45.md
```

in the target Project's configured log location.

If it already exists:

* Abort the move safely.
* Do not half-move the document/log pair.

---

# 33. Update Active File Path After Rename/Move

After a successful move, Requirement 17C must update the active document path
to reflect both:

* The target configured subfolder
* The target configured prefix

Example:

Before:

```text
Inklings/Writing/08 Dailies/01 Inbox/
    DA-2026-08-24-MON-11_30_45.md
```

After:

```text
Inklings/Research/Research Notes/Inbox/
    RN-2026-08-24-MON-11_30_45.md
```

Future:

* Save
* Auto-save
* Close-triggered save

must use the new path and new filename.

Do not recreate the old DA file afterward.

---

# 34. Future Log Updates After Move

After a successful move, future log behavior must use:

* Target Project
* Target `logSubfolder`
* Target `logPrefix`
* Original session timestamp

Example:

After moving to Research:

```text
RL-2026-08-24-MON-11_30_45.md
```

must remain the associated session log.

Do not create a new:

```text
BAS-2026-08-24-MON-11_30_45.md
```

in the old Project.

---

# 35. Move With No Existing Log

If a document has no BAS/log file when it is moved:

* Move and rename the Markdown using target `documentPrefix`.
* Do not create a log merely because of the move.

If a log is legitimately created later for that same session, it must use:

```text
target logPrefix + original session timestamp
```

and the target Project's configured log path.

---

# 36. Move Safety Remains Required

All Requirement 17C safety requirements remain.

If a document and log pair are being moved:

```text
Both succeed
```

or:

```text
Move fails safely
```

Do not leave:

* Markdown renamed/moved but BAS left behind
* BAS moved but Markdown left behind
* Active path updated after a failed operation

The new prefix/path behavior must be incorporated into the same safe
transaction-like move operation.

---

# 37. Requirement 16 Compatibility

Requirement 16 remains unchanged.

Configurable folders and prefixes must not cause a fresh whitespace-only
document to be created.

Previously saved documents may still intentionally be cleared.

Moving a previously saved empty document remains allowed.

---

# 38. Existing Project UI

Do not change the Project UI implemented in Requirements 17B/17C merely to
support these configuration properties.

The existing:

```text
Move here
```

behavior remains.

The new target path and prefix rules are applied internally after the user
selects Move.

---

# 39. Existing Features

Do not change:

* Project creation UI
* Project Font Color UI
* Default Project selection
* Project dialog layout
* Timer
* Typewriter sounds
* Fade behavior
* Full-screen mode
* Keyboard shortcuts
* Auto-capitalization
* Writing-time calculation
* Requirement 16 save semantics

17D is specifically about configurable filesystem paths and naming rules.

---

# Testing

## Test 1 — Existing Metadata Upgrade

Start with:

```json
{
  "fontColor": "#4285F4",
  "isDefault": true
}
```

Run Requirement 17D.

Verify the four new properties receive defaults without losing the old
metadata.

---

## Test 2 — New Project Metadata

Create a new Project through the existing Project UI.

Inspect:

```text
.inklings-project.json
```

Verify it contains:

```text
documentSubfolder = 08 Dailies/01 Inbox
logSubfolder      = 99 Operations/99 Log
documentPrefix    = DA
logPrefix         = BAS
```

---

## Test 3 — Custom Document Path

Set:

```json
"documentSubfolder": "Writing/Inbox"
```

Restart/refresh Project metadata.

Create and save a new session.

Expected:

```text
Inklings/<Project>/Writing/Inbox/
```

---

## Test 4 — Custom Document Prefix

Set:

```json
"documentPrefix": "JOURNAL"
```

Create a new file.

Expected:

```text
JOURNAL-<existing timestamp format>.md
```

---

## Test 5 — Custom Log Path

Set:

```json
"logSubfolder": "Logs/Writing"
```

Generate a log.

Expected:

```text
Inklings/<Project>/Logs/Writing/YYYY/MM/
```

---

## Test 6 — Custom Log Prefix

Set:

```json
"logPrefix": "SESSION"
```

Generate a log.

Expected:

```text
SESSION-<same session timestamp>.md
```

---

## Test 7 — Existing File Not Renamed by Config Change

Create:

```text
DA-<timestamp>.md
```

Then change:

```json
"documentPrefix": "JOURNAL"
```

Save the existing document again.

Expected:

```text
DA-<timestamp>.md
```

remains its filename.

---

## Test 8 — New File Uses New Prefix

After Test 7, create a new session.

Expected:

```text
JOURNAL-<new timestamp>.md
```

---

## Test 9 — Existing File Path Not Changed by Config Change

Save a document under the old configured path.

Change:

```json
"documentSubfolder": "New/Inbox"
```

Save the already-existing/open document.

Expected:

* Existing path remains unchanged.

Create a NEW session.

Expected:

* New session uses `New/Inbox/`.

---

## Test 10 — Move Adopts Target Prefix

Source Project:

```text
documentPrefix = DA
```

Target Project:

```text
documentPrefix = RN
```

Move:

```text
DA-2026-08-24-MON-11_30_45.md
```

Expected target filename:

```text
RN-2026-08-24-MON-11_30_45.md
```

Verify timestamp is unchanged.

---

## Test 11 — Move Adopts Target Log Prefix

Source:

```text
BAS-2026-08-24-MON-11_30_45.md
```

Target Project:

```text
logPrefix = RL
```

Move the associated document.

Expected:

```text
RL-2026-08-24-MON-11_30_45.md
```

in the target log path.

---

## Test 12 — Move Adopts Target Paths

Configure target:

```json
{
  "documentSubfolder": "Research Notes/Inbox",
  "logSubfolder": "Research Logs",
  "documentPrefix": "RN",
  "logPrefix": "RL"
}
```

Move a DA/BAS pair into the target Project.

Expected:

```text
Inklings/<Target>/Research Notes/Inbox/
    RN-<original timestamp>.md

Inklings/<Target>/Research Logs/YYYY/MM/
    RL-<original timestamp>.md
```

---

## Test 13 — Target Filename Conflict

Pre-create:

```text
RN-<timestamp>.md
```

in the target Project.

Attempt to move:

```text
DA-<same timestamp>.md
```

Expected:

* Move aborted.
* Source remains intact.
* Target remains intact.
* No overwrite.

---

## Test 14 — Target Log Conflict

Pre-create:

```text
RL-<timestamp>.md
```

in the target log folder.

Attempt move of the matching DA/BAS pair.

Expected:

* Entire move fails safely.
* No half-move occurs.

---

## Test 15 — Move With No BAS

Move:

```text
DA-<timestamp>.md
```

to a Project using:

```text
documentPrefix = RN
logPrefix = RL
```

when no BAS exists.

Expected:

```text
RN-<same timestamp>.md
```

is moved successfully.

No RL file is created merely because of the move.

---

## Test 16 — Future Log After Move

Move a document with no existing BAS into a Project configured with:

```text
logPrefix = RL
```

If the application later legitimately creates that session's log, verify it
uses:

```text
RL-<original session timestamp>.md
```

---

## Test 17 — Unsafe Path

Set:

```json
"documentSubfolder": "../../Outside"
```

Verify:

* App does not write outside the Project.
* App does not crash.
* Unsafe configuration is rejected/falls back safely.

---

## Test 18 — Invalid Prefix

Use a prefix containing:

```text
/
..
\
```

Verify no unsafe filename/path is created.

---

## Test 19 — Invalid JSON

Break `.inklings-project.json` manually.

Restart/open Project management.

Verify:

* App does not crash.
* Existing documents remain untouched.
* Existing log files remain untouched.
* Safe error/default behavior occurs.

---

## Test 20 — Requirement 16 Regression

Verify:

* Fresh whitespace-only session still creates no files.
* Previously saved document can still intentionally be cleared.

---

## Test 21 — Existing Feature Regression

Verify:

* Save
* New
* Close
* Auto-save
* BAS logging
* Project creation
* Default Project
* Font Color
* Move here
* Fade
* Timer
* Typewriter sounds
* Full-screen
* Ctrl+S
* Ctrl+N
* Ctrl+Q

continue to work correctly.

---

# Completion Criteria

Requirement 17D is complete when:

1. Every Project supports `documentSubfolder`.
2. Every Project supports `logSubfolder`.
3. Every Project supports `documentPrefix`.
4. Every Project supports `logPrefix`.
5. Defaults exactly preserve existing behavior.
6. New Projects automatically receive all four defaults.
7. Existing Projects are safely upgraded with missing defaults.
8. Valid manual JSON customization is preserved.
9. Metadata is re-read on startup/Project refresh.
10. No UI is added for these four properties.
11. Paths are resolved relative to the Project directory.
12. Nested relative paths are supported.
13. Unsafe path traversal is blocked.
14. Existing `YYYY/MM` log organization remains.
15. Custom document prefixes work.
16. Custom log prefixes work.
17. Shared timestamp remains the document/log association mechanism.
18. Merely changing a prefix does not rename existing files.
19. Merely changing a path does not relocate existing files.
20. Existing open documents remain at their actual existing paths.
21. New documents use the Project's configured path and prefix.
22. New logs use the Project's configured path and prefix.
23. Moving a document under Requirement 17C uses the target Project's
    `documentSubfolder`.
24. Moving an associated log uses the target Project's `logSubfolder`.
25. A moved Markdown file is renamed to the target Project's
    `documentPrefix`.
26. A moved BAS/log file is renamed to the target Project's `logPrefix`.
27. The original session timestamp is preserved during move/rename.
28. Target conflict detection uses the target prefix-derived filename.
29. Existing Markdown and BAS contents remain unchanged during move.
30. Future saves after a move use the new target path and renamed filename.
31. Future log updates after a move use the target log prefix/path.
32. A move with no existing BAS does not create one merely because of move.
33. Requirement 17C move safety remains intact.
34. Malformed metadata does not cause user-file loss.
35. Requirement 16 remains intact.
36. Existing Project UI remains unchanged.
37. No unrelated functionality is changed.

---

# Required Source-Code Comments

Add concise comments explaining:

1. Why Project paths and prefixes live in `.inklings-project.json`.
2. Why these four properties intentionally have no UI.
3. Why paths are Project-relative.
4. Why path traversal must be prevented.
5. Why prefix changes alone do not rename existing files.
6. Why path changes alone do not move existing files.
7. Why an explicit Project move DOES adopt the target Project's prefixes.
8. Why the session timestamp remains unchanged when prefixes change during a
   move.
9. Why the shared timestamp, not the prefix, identifies the Markdown/BAS pair.
10. How missing metadata receives defaults without overwriting valid custom
    values.
11. How Requirement 17C calculates target filenames before conflict checking.
12. How active paths and future Save/Auto-save/log operations are updated
    after a renamed Project move.

---

# After Implementation

Provide a short summary explaining:

1. Which files were changed.
2. The final `.inklings-project.json` structure.
3. How Project-relative document paths are resolved.
4. How Project-relative log paths are resolved.
5. How new Markdown filenames use `documentPrefix`.
6. How new BAS filenames use `logPrefix`.
7. How existing Project metadata is upgraded.
8. How manually edited JSON values are preserved/reloaded.
9. How path/prefix validation works.
10. Why configuration changes alone do not rename/move existing files.
11. How 17C now renames moved Markdown files to the target Project prefix.
12. How 17C now renames moved BAS files to the target Project prefix.
13. How the original timestamp is preserved during those renames.
14. How target conflicts are checked.
15. How future Save/Auto-save/log updates work after a move.
16. Confirmation that no UI was added for these four advanced properties.
17. Confirmation that no unrelated functionality was changed.

Stop after implementing Requirement 17D.
