# Requirement 17D-FIX-01 — Document Filename Format, Blank Prefix and Rename Integration

## Objective

Modify the document filename behavior introduced by Requirement 17D.

This fix makes three changes:

1. An empty `documentPrefix` is valid and means that the filename starts directly with the date.
2. Change the filename format for newly created Markdown documents across all Projects.
3. Adapt the **existing document Rename functionality** so that it replaces the `TITLE` component of the new filename format.

This requirement applies primarily to Markdown document filenames.

Do NOT change the BAS/log filename format.

Do NOT create a new Rename feature or Rename UI. Modify the existing Rename functionality to work with the new filename structure.

---

# 1. New Document Filename Format

Replace the current Markdown filename format:

```text
PREFIX-YYYY-MM-DD-DDD-HH_MM_SS.md
```

with:

```text
PREFIX-YYYY-MM-DD, Ddd - TITLE - HH_MM_SS.md
```

If the Project has a blank prefix:

```text
YYYY-MM-DD, Ddd - TITLE - HH_MM_SS.md
```

where:

```text
Ddd = Mon / Tue / Wed / Thu / Fri / Sat / Sun
```

Example with prefix:

```text
DA-2026-08-27, Thu - TITLE - 21_43_10.md
```

Example without prefix:

```text
2026-08-27, Thu - TITLE - 21_43_10.md
```

---

# 2. Filename Components

Conceptually, treat the filename as these components:

```text
[optional prefix]
[date]
[weekday]
[title]
[time]
[extension]
```

Rendered as:

```text
[PREFIX-]YYYY-MM-DD, Ddd - TITLE - HH_MM_SS.md
```

Example:

```text
DA-2026-08-27, Thu - TITLE - 21_43_10.md
│  │           │     │       │
│  │           │     │       └── session time
│  │           │     └────────── title
│  │           └──────────────── weekday
│  └──────────────────────────── date
└─────────────────────────────── optional Project prefix
```

The filename architecture must distinguish these components rather than treating the filename as an arbitrary string.

---

# 3. Empty Document Prefix Is Valid

Requirement 17D previously treated:

```json
"documentPrefix": ""
```

as invalid and could fall back to:

```text
DA
```

That behavior must be removed for `documentPrefix`.

An empty `documentPrefix` is now explicitly valid.

It means:

> Do not place a Project prefix before the date.

Do NOT substitute `DA`.

---

# 4. Non-Empty Prefix

If:

```json
"documentPrefix": "DA"
```

the filename is:

```text
DA-2026-08-27, Thu - TITLE - 21_43_10.md
```

If:

```json
"documentPrefix": "RN"
```

the filename is:

```text
RN-2026-08-27, Thu - TITLE - 21_43_10.md
```

The existing hyphen between a non-empty prefix and the date remains.

---

# 5. Blank Prefix

If:

```json
"documentPrefix": ""
```

the filename begins directly with the date:

```text
2026-08-27, Thu - TITLE - 21_43_10.md
```

Do NOT produce:

```text
-2026-08-27, Thu - TITLE - 21_43_10.md
```

Do NOT produce:

```text
DA-2026-08-27, Thu - TITLE - 21_43_10.md
```

Do NOT introduce leading whitespace.

Correct:

```text
2026-08-27, Thu - TITLE - 21_43_10.md
```

---

# 6. Date Format

The date portion remains:

```text
YYYY-MM-DD
```

Example:

```text
2026-08-27
```

Immediately after the date use:

```text
, 
```

followed by the weekday abbreviation.

Therefore:

```text
2026-08-27, Thu
```

---

# 7. Weekday Format

Use exactly these English weekday abbreviations:

```text
Mon
Tue
Wed
Thu
Fri
Sat
Sun
```

Do NOT use uppercase:

```text
MON
TUE
WED
THU
FRI
SAT
SUN
```

Do NOT use lowercase:

```text
mon
tue
wed
```

Do NOT use full names:

```text
Monday
Tuesday
Wednesday
```

The required capitalization is exactly:

```text
Mon
Tue
Wed
Thu
Fri
Sat
Sun
```

---

# 8. Time Format

Preserve the existing time format:

```text
HH_MM_SS
```

Example:

```text
21_43_10
```

Do not change the time format as part of this requirement.

---

# 9. Initial TITLE Placeholder

Every newly created Markdown document must initially contain the literal uppercase word:

```text
TITLE
```

in the title position.

Example:

```text
DA-2026-08-27, Thu - TITLE - 21_43_10.md
```

or with no prefix:

```text
2026-08-27, Thu - TITLE - 21_43_10.md
```

`TITLE` is a placeholder until the user uses the application's existing Rename functionality.

---

# 10. Do Not Automatically Generate a Title

Do NOT automatically derive the initial title from:

* First sentence
* First line
* First Markdown heading
* Document contents
* Project name

A newly created document always begins with:

```text
TITLE
```

as its title component.

---

# 11. Existing Rename Feature

The application already has document Rename functionality.

Do NOT:

* Create another Rename button.
* Create another Rename dialog.
* Create another Rename workflow.

Modify the existing Rename implementation so that it understands the new filename format.

---

# 12. Rename Replaces TITLE

For a newly created document:

```text
DA-2026-08-27, Thu - TITLE - 21_43_10.md
```

if the user uses the existing Rename feature and enters:

```text
Thoughts on Tolkien
```

the resulting filename must be:

```text
DA-2026-08-27, Thu - Thoughts on Tolkien - 21_43_10.md
```

Only the title component changes.

---

# 13. Rename With Blank Prefix

For:

```json
"documentPrefix": ""
```

initial filename:

```text
2026-08-27, Thu - TITLE - 21_43_10.md
```

User enters:

```text
Thoughts on Tolkien
```

Result:

```text
2026-08-27, Thu - Thoughts on Tolkien - 21_43_10.md
```

Rename must NOT introduce a prefix.

---

# 14. Rename Must Replace the Title Component

The Rename implementation must not merely search for the literal word:

```text
TITLE
```

because a document may already have been renamed.

Instead, the application must understand the title **position/component** of the filename.

For example:

```text
DA-2026-08-27, Thu - Thoughts on Tolkien - 21_43_10.md
```

renamed again to:

```text
Fairy Stories
```

must become:

```text
DA-2026-08-27, Thu - Fairy Stories - 21_43_10.md
```

Do NOT produce:

```text
DA-2026-08-27, Thu - Thoughts on Tolkien - Fairy Stories - 21_43_10.md
```

Do NOT require `TITLE` to still be present.

---

# 15. Rename Changes Only the Title

Rename must preserve:

* Project prefix
* Date
* Weekday
* Time
* `.md` extension

Example:

Before:

```text
RN-2026-08-27, Thu - TITLE - 21_43_10.md
```

User enters:

```text
Research Notes
```

After:

```text
RN-2026-08-27, Thu - Research Notes - 21_43_10.md
```

The following remain exactly unchanged:

```text
RN
2026-08-27
Thu
21_43_10
.md
```

---

# 16. Existing Rename Sanitization

Preserve the existing Rename feature's filename-safety behavior.

User-supplied titles must not introduce:

* Path traversal
* Directory separators
* Invalid filename characters
* Invalid filesystem paths

If the existing Rename implementation already sanitizes user input, reuse that logic.

Do not weaken existing filename safety.

---

# 17. Timestamp Remains the Session Identity

The stable identity of the writing session remains its original date/time.

For:

```text
DA-2026-08-27, Thu - Thoughts on Tolkien - 21_43_10.md
```

the session date/time remains:

```text
2026-08-27
21_43_10
```

Renaming the title must never change that date/time.

---

# 18. BAS / Log Association

Existing Markdown/BAS association must continue to work.

The Markdown file:

```text
DA-2026-08-27, Thu - Thoughts on Tolkien - 21_43_10.md
```

must still be associated with the BAS/log belonging to the session:

```text
2026-08-27
21_43_10
```

The title is NOT part of the session identity.

Changing:

```text
TITLE
```

to:

```text
Thoughts on Tolkien
```

must not break BAS/log association.

---

# 19. BAS Filename Format Is Unchanged

Do NOT change the BAS/log filename format in this requirement.

The existing Requirement 17D:

```json
"logPrefix"
```

behavior remains unchanged.

This filename-format change applies to Markdown documents.

---

# 20. Existing Old-Format Documents

Do NOT automatically rename existing old-format Markdown documents simply because this requirement has been installed.

For example:

```text
DA-2026-08-20-THU-10_00_00.md
```

must remain as-is unless an existing operation explicitly requires otherwise.

Opening and saving such an existing document must not automatically convert its filename to the new format.

---

# 21. New Documents

All newly created Markdown documents after implementation must use:

```text
[PREFIX-]YYYY-MM-DD, Ddd - TITLE - HH_MM_SS.md
```

Example with prefix:

```text
DA-2026-08-27, Thu - TITLE - 21_43_10.md
```

Example without prefix:

```text
2026-08-27, Thu - TITLE - 21_43_10.md
```

---

# 22. Project Move — Existing 17C/17D Behavior

Requirement 17D established that moving a document to another Project adopts the target Project's configured `documentPrefix`.

Preserve that behavior.

Under the new filename format, a Project move changes the prefix while preserving:

* Date
* Weekday
* Current title
* Time

---

# 23. Project Move Example

Source:

```text
DA-2026-08-27, Thu - Thoughts on Tolkien - 21_43_10.md
```

Target Project configuration:

```json
"documentPrefix": "RN"
```

After move:

```text
RN-2026-08-27, Thu - Thoughts on Tolkien - 21_43_10.md
```

The title must remain:

```text
Thoughts on Tolkien
```

Do NOT reset it to:

```text
TITLE
```

---

# 24. Move to Project With Blank Prefix

Source:

```text
DA-2026-08-27, Thu - Thoughts on Tolkien - 21_43_10.md
```

Target:

```json
"documentPrefix": ""
```

Result:

```text
2026-08-27, Thu - Thoughts on Tolkien - 21_43_10.md
```

The source prefix must be removed cleanly.

Do NOT produce:

```text
-2026-08-27, Thu - Thoughts on Tolkien - 21_43_10.md
```

---

# 25. Move From Blank Prefix to Prefix

Source:

```text
2026-08-27, Thu - Thoughts on Tolkien - 21_43_10.md
```

Target:

```json
"documentPrefix": "RN"
```

Result:

```text
RN-2026-08-27, Thu - Thoughts on Tolkien - 21_43_10.md
```

---

# 26. Rename After Project Move

Rename must continue working after a Project move.

Example:

```text
RN-2026-08-27, Thu - Thoughts on Tolkien - 21_43_10.md
```

User renames to:

```text
On Fairy-Stories
```

Result:

```text
RN-2026-08-27, Thu - On Fairy-Stories - 21_43_10.md
```

Do not alter the target Project prefix or original session date/time.

---

# 27. Project Move After Rename

The reverse sequence must also work.

Initial:

```text
DA-2026-08-27, Thu - TITLE - 21_43_10.md
```

Rename:

```text
DA-2026-08-27, Thu - On Fairy-Stories - 21_43_10.md
```

Move to Project using:

```json
"documentPrefix": "RN"
```

Result:

```text
RN-2026-08-27, Thu - On Fairy-Stories - 21_43_10.md
```

The existing title must survive the move.

---

# 28. Centralize Filename Construction and Parsing

Do not implement this by scattering string manipulation across different code paths.

Create/reuse centralized filename logic that understands:

```text
documentPrefix
sessionDate
weekday
title
sessionTime
```

It should be capable of building:

```text
[PREFIX-]YYYY-MM-DD, Ddd - TITLE - HH_MM_SS.md
```

and parsing an existing new-format filename into its components.

This logic should be reused by:

* New document creation
* Existing Rename functionality
* Project move
* Any logic that needs the session timestamp
* BAS association where relevant

---

# 29. Parsing a Prefixed Filename

For:

```text
RN-2026-08-27, Thu - Thoughts on Tolkien - 21_43_10.md
```

the parser should conceptually identify:

```text
prefix    = RN
date      = 2026-08-27
weekday   = Thu
title     = Thoughts on Tolkien
time      = 21_43_10
extension = .md
```

---

# 30. Parsing a Blank-Prefix Filename

For:

```text
2026-08-27, Thu - Thoughts on Tolkien - 21_43_10.md
```

the parser should conceptually identify:

```text
prefix    = ""
date      = 2026-08-27
weekday   = Thu
title     = Thoughts on Tolkien
time      = 21_43_10
extension = .md
```

The parser must not require a prefix.

---

# 31. Titles Containing Spaces

The title component may contain spaces.

Examples:

```text
Thoughts on Tolkien
My Writing Session
Ideas for the Novel
```

The parser must not assume that the title is a single word.

---

# 32. Title Parsing

Use the filename structure to identify the title.

Conceptually:

```text
[PREFIX-]YYYY-MM-DD, Ddd - [TITLE] - HH_MM_SS.md
```

The title is the middle component between the date/day portion and the final time portion.

Do not identify the title merely by looking for the literal word `TITLE`.

---

# 33. Existing Open Documents

If an already-saved document is currently open, continue using its actual path and filename.

Do not reconstruct its filename on every Save.

Explicit Save and Auto-save should continue updating the active file.

This prevents configuration changes from unexpectedly renaming existing files.

---

# 34. Requirement 16 Compatibility

Requirement 16 remains unchanged.

A fresh document containing only whitespace must still not create a Markdown file.

The new filename is used only when existing save rules determine that a file should actually be created.

---

# 35. No Unrelated Changes

Do not change:

* Project creation UI
* Project dialog
* Project Font Color
* Default Project behavior
* `documentSubfolder`
* `logSubfolder`
* `logPrefix`
* BAS/log filename format
* Timer
* Fade
* Typewriter sounds
* Full-screen mode
* Keyboard shortcuts
* Auto-capitalization
* Save semantics
* Auto-save interval
* Writing-time calculation

Only implement the filename/prefix/rename integration described here.

---

# Testing

## Test 1 — Prefix Present

Configure:

```json
"documentPrefix": "DA"
```

Create a new document.

Expected:

```text
DA-YYYY-MM-DD, Ddd - TITLE - HH_MM_SS.md
```

---

## Test 2 — Blank Prefix

Configure:

```json
"documentPrefix": ""
```

Create a new document.

Expected:

```text
YYYY-MM-DD, Ddd - TITLE - HH_MM_SS.md
```

Verify there is:

* No `DA`
* No leading hyphen
* No leading space

---

## Test 3 — Weekday

Verify weekdays are exactly:

```text
Mon
Tue
Wed
Thu
Fri
Sat
Sun
```

---

## Test 4 — Initial TITLE

Create a new document.

Verify filename contains exactly:

```text
 - TITLE - 
```

---

## Test 5 — First Rename

Start with:

```text
DA-2026-08-27, Thu - TITLE - 21_43_10.md
```

Rename to:

```text
Thoughts on Tolkien
```

Expected:

```text
DA-2026-08-27, Thu - Thoughts on Tolkien - 21_43_10.md
```

---

## Test 6 — Rename Again

Start with:

```text
DA-2026-08-27, Thu - Thoughts on Tolkien - 21_43_10.md
```

Rename to:

```text
Fairy Stories
```

Expected:

```text
DA-2026-08-27, Thu - Fairy Stories - 21_43_10.md
```

Verify the new title replaces the old title rather than being appended.

---

## Test 7 — Rename With Blank Prefix

Start with:

```text
2026-08-27, Thu - TITLE - 21_43_10.md
```

Rename to:

```text
My Notes
```

Expected:

```text
2026-08-27, Thu - My Notes - 21_43_10.md
```

No prefix should appear.

---

## Test 8 — Session Identity After Rename

Rename:

```text
DA-2026-08-27, Thu - TITLE - 21_43_10.md
```

to:

```text
DA-2026-08-27, Thu - My Notes - 21_43_10.md
```

Verify:

```text
2026-08-27
21_43_10
```

remain unchanged and BAS association still works.

---

## Test 9 — Move After Rename

Source:

```text
DA-2026-08-27, Thu - My Notes - 21_43_10.md
```

Target:

```json
"documentPrefix": "RN"
```

Expected:

```text
RN-2026-08-27, Thu - My Notes - 21_43_10.md
```

---

## Test 10 — Move to Blank Prefix

Source:

```text
DA-2026-08-27, Thu - My Notes - 21_43_10.md
```

Target:

```json
"documentPrefix": ""
```

Expected:

```text
2026-08-27, Thu - My Notes - 21_43_10.md
```

---

## Test 11 — Rename After Move

After moving to RN:

```text
RN-2026-08-27, Thu - My Notes - 21_43_10.md
```

rename to:

```text
Tolkien
```

Expected:

```text
RN-2026-08-27, Thu - Tolkien - 21_43_10.md
```

---

## Test 12 — Existing Old-Format File

Open:

```text
DA-2026-08-20-THU-10_00_00.md
```

Edit and save.

Verify it is not automatically converted to the new filename format.

---

## Test 13 — New File After Upgrade

Create a completely new writing session.

Verify it uses the new filename format.

---

## Test 14 — BAS Association

Create a new-format Markdown document.

Allow its BAS/log behavior to occur.

Rename the document.

Verify the correct BAS/log continues to be associated with the session.

---

## Test 15 — Regression

Verify:

* Save
* Auto-save
* New
* Close
* Existing Rename
* Project move
* BAS logging
* Requirement 16
* Fade
* Timer
* Typewriter sounds
* Keyboard shortcuts

continue to function normally.

---

# Completion Criteria

Requirement 17D-FIX-01 is complete when:

1. Empty `documentPrefix` is valid.
2. Empty prefix no longer falls back to `DA`.
3. No separator is emitted before the date when prefix is blank.
4. Non-empty prefixes continue to work.
5. New Markdown filenames use:

```text
[PREFIX-]YYYY-MM-DD, Ddd - TITLE - HH_MM_SS.md
```

6. Weekdays are exactly `Mon`, `Tue`, `Wed`, `Thu`, `Fri`, `Sat`, `Sun`.
7. New documents initially contain literal `TITLE`.
8. Existing Rename functionality replaces the title component.
9. No new Rename UI/workflow is created.
10. A document can be renamed repeatedly.
11. Re-renaming replaces the previous title rather than appending another title.
12. Rename preserves prefix.
13. Rename preserves date.
14. Rename preserves weekday.
15. Rename preserves time.
16. Rename preserves `.md`.
17. Rename with a blank prefix does not introduce a prefix.
18. Rename preserves BAS/log association.
19. Existing filename-safety/sanitization remains.
20. Project move preserves the current title.
21. Project move changes only the prefix according to the target Project naming configuration.
22. Moving to a blank-prefix Project removes the prefix cleanly.
23. Rename continues to work after Project move.
24. Project move continues to work after Rename.
25. Filename parsing supports prefixed filenames.
26. Filename parsing supports non-prefixed filenames.
27. Filename parsing supports multi-word titles.
28. Date/time remains the stable session identity.
29. Existing old-format documents are not automatically renamed.
30. BAS/log filename format is unchanged.
31. Requirement 16 remains unchanged.
32. No unrelated behavior is changed.

---

# Required Source-Code Comments

Add concise comments explaining:

1. Why an empty `documentPrefix` is intentional and valid.
2. Why the prefix separator is emitted only for a non-empty prefix.
3. The components of the new Markdown filename.
4. Why `TITLE` is the initial placeholder.
5. Why Rename replaces the title component rather than searching only for the literal word `TITLE`.
6. Why Rename preserves prefix/date/weekday/time.
7. Why the date/time remains the stable session identity for BAS association.
8. Why Project moves preserve title while adopting the target Project prefix.
9. Why filename parsing must support both prefixed and blank-prefix filenames.
10. Why existing old-format documents are not automatically migrated.

---

# After Implementation

Provide a short summary explaining:

1. Which files were changed.
2. How new filenames are constructed.
3. How blank prefixes are handled.
4. How `Mon` through `Sun` are generated.
5. How `TITLE` is initialized.
6. How the existing Rename implementation was adapted.
7. How repeated Rename works.
8. How filename parsing identifies the title.
9. How Rename preserves session identity/BAS association.
10. How Project move and Rename interact.
11. How old-format documents remain compatible.
12. Confirmation that BAS/log naming was not changed.
13. Confirmation that no new Rename UI was introduced.
14. Confirmation that no unrelated functionality was changed.

Stop after implementing Requirement 17D-FIX-01.
