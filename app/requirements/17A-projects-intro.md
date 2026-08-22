# Requirement 17A — Project Storage and Data Model

## Objective

Introduce the underlying **Project** concept into the application.

This requirement establishes:

* Project folders under `Inklings/`
* Project discovery from the filesystem
* Project metadata stored inside each Project folder
* Default Project handling
* Safe migration of the existing pre-Project folder structure

Do **not** implement the Project management UI yet.

Do **not** implement moving files between Projects yet.

Do **not** implement Project renaming.

Do **not** implement an in-app Project delete function.

Those will be handled by later requirements.

The fundamental design rule is:

> **The filesystem is the source of truth for which Projects exist.**

A Project exists because its directory exists inside `Inklings/`.

---

# 1. Project Concept

The application currently uses:

```text
Inklings/
```

Requirement 17A introduces a Project layer immediately below this directory.

Example:

```text
Inklings/
├── default/
├── Writing/
├── Work/
└── Research/
```

Each immediate valid Project directory inside `Inklings/` represents one
Project.

Each Project contains its own copy of the application's existing internal
folder structure.

---

# 2. Project Directory Structure

Each Project must contain:

```text
<Project>/
├── .inklings-project.json
├── 08 Dailies/
│   └── 01 Inbox/
└── 99 Operations/
    └── 99 Log/
```

Example:

```text
Inklings/
└── Writing/
    ├── .inklings-project.json
    ├── 08 Dailies/
    │   └── 01 Inbox/
    └── 99 Operations/
        └── 99 Log/
```

The existing internal folder structure must remain unchanged.

The Project layer is simply added above it.

---

# 3. Filesystem Is the Source of Truth

Do NOT maintain an authoritative Project list elsewhere in the application.

On application startup:

1. Locate `Inklings/`.
2. Inspect its immediate subdirectories.
3. Treat valid Project directories as Projects.
4. Load each Project's metadata from its own metadata file.
5. Initialize missing metadata when necessary.
6. Determine the Default Project.

Conceptually:

```text
App starts
    ↓
Scan Inklings/
    ↓
Discover Project directories
    ↓
Read each .inklings-project.json
    ↓
Build current Project list
```

A stored metadata record must never cause a Project to exist if its directory
does not exist.

---

# 4. Project Metadata File

Every Project should contain:

```text
.inklings-project.json
```

The metadata file is stored inside the corresponding Project directory.

Example:

```text
Inklings/
└── Research/
    ├── .inklings-project.json
    ├── 08 Dailies/
    └── 99 Operations/
```

For Requirement 17A, the metadata should contain:

* `fontColor`
* `isDefault`

The Project Name does not need to be stored because:

```text
Project directory name = Project Name
```

---

# 5. Metadata Example

Conceptually:

```json
{
  "fontColor": "#4285F4",
  "isDefault": false
}
```

The exact JSON serialization implementation is up to Gemini.

Keep the metadata format:

* Small
* Simple
* Human-readable
* Versionable if practical

Do not put document content in this file.

---

# 6. Project Name

The Project Name is the Project directory name.

Examples:

```text
Inklings/Writing/
```

means:

```text
Project Name = Writing
```

and:

```text
Inklings/default/
```

means:

```text
Project Name = default
```

Do not maintain a separate Project Name value that can disagree with the
directory name.

Do not implement Project rename functionality in 17A.

---

# 7. Initial Project

The initial Project must be named exactly:

```text
default
```

Lowercase.

The initial Project path must therefore be:

```text
Inklings/default/
```

with:

```text
Inklings/default/
├── .inklings-project.json
├── 08 Dailies/
│   └── 01 Inbox/
└── 99 Operations/
    └── 99 Log/
```

The `default` Project must initially be the Default Project.

---

# 8. Project Properties

Each Project has these properties:

1. Project Name
2. Font Color
3. Default Project

Project Name comes from the filesystem.

Font Color and Default status come from:

```text
.inklings-project.json
```

---

# 9. Font Color

Each Project has a Font Color.

For Requirement 17A:

* Store the Font Color in `.inklings-project.json`.
* Provide a sensible default color.
* Make the Project model able to return the configured color.
* If metadata is missing, initialize a default Font Color.

The color-picker UI will be implemented later in Requirement 17B.

Do not modify Markdown content to represent Font Color.

Do not add:

* HTML
* YAML
* Markdown formatting

for Project colors.

Project Font Color is presentation metadata only.

---

# 10. Default Project

Exactly one discovered Project should ultimately be the Default Project.

Its metadata contains:

```json
"isDefault": true
```

When another Project later becomes Default:

* Set its `isDefault` to `true`.
* Set all other discovered Projects to `false`.

There must never be more than one effective Default Project.

---

# 11. `default` Name vs Default Status

The Project named:

```text
default
```

is the initial and fallback Project.

However, another Project may later be selected as the Default Project.

For example:

```text
Inklings/
├── default/
├── Writing/
└── Research/
```

could later have:

```text
Writing → isDefault = true
default → isDefault = false
Research → isDefault = false
```

Therefore:

> The Project named `default` does not have to remain the active Default
> Project forever.

It remains the fallback Project.

---

# 12. External Project Deletion

The user must be able to delete a Project using Android's Files app.

Example:

Before:

```text
Inklings/
├── default/
├── Writing/
└── Research/
```

The user deletes:

```text
Inklings/Research/
```

using Android Files.

On the next application startup:

```text
Research
```

must no longer exist as a Project.

The application must NOT recreate it.

Because its metadata file was inside the Project folder, its Project metadata
was deleted together with the Project.

---

# 13. No Separate Authoritative Project Registry

Do NOT store an authoritative list such as:

```text
Projects = [default, Writing, Research]
```

inside:

* SharedPreferences
* DataStore
* Room/database
* Internal application files
* Any other separate registry

for the purpose of defining Project existence.

The filesystem defines which Projects exist.

Application preferences may still be used for unrelated settings such as:

* Typewriter Sounds
* Timer duration

but not as the Project inventory.

---

# 14. Missing Project Metadata

A Project directory may exist without:

```text
.inklings-project.json
```

This may happen if the user manually creates/copies a Project directory.

For example:

```text
Inklings/
└── Imported/
    ├── 08 Dailies/
    └── 99 Operations/
```

On startup, the application should discover:

```text
Imported
```

as a Project.

Initialize safe metadata:

```text
Project Name = Imported
Font Color = default font color
Default Project = false
```

and create:

```text
Inklings/Imported/.inklings-project.json
```

Do not reject the Project merely because metadata is missing.

---

# 15. External Project Addition

The application should tolerate a Project directory being copied or created
under `Inklings/` outside the app.

On the next startup, it should be discovered.

The filesystem remains authoritative.

Do not require that every Project was originally created by the application.

---

# 16. Project Folder Validation

A directory directly under `Inklings/` should only be treated as a Project if
it is reasonably compatible with the Project structure.

Do not treat arbitrary loose files under `Inklings/` as Projects.

Be conservative.

Do not delete unexpected files or directories simply because they are not
recognized.

---

# 17. Deleted Current Default Project

Suppose:

```text
Inklings/
├── default/
├── Writing/
└── Research/
```

and:

```text
Writing/.inklings-project.json
```

contains:

```json
"isDefault": true
```

The user deletes:

```text
Inklings/Writing/
```

using Android Files.

On the next startup:

* `Writing` must disappear.
* `Writing` must NOT be recreated.
* `default` becomes the Default Project.

Update:

```text
Inklings/default/.inklings-project.json
```

to contain:

```json
"isDefault": true
```

---

# 18. Deleted `default` Project

If the user deletes:

```text
Inklings/default/
```

the application should not immediately recreate it merely because of stale
metadata.

However, if the application needs a fallback because no valid Default Project
exists, recreate:

```text
Inklings/default/
```

with:

```text
.inklings-project.json
08 Dailies/01 Inbox/
99 Operations/99 Log/
```

and make it the Default Project.

Do NOT recreate any other externally deleted Project.

---

# 19. Multiple Defaults — Recovery

It is possible that metadata becomes inconsistent.

For example:

```text
Writing/.inklings-project.json  → isDefault = true
Research/.inklings-project.json → isDefault = true
```

On startup, detect and correct this inconsistency.

After reconciliation:

```text
Exactly one Project → isDefault = true
```

Persist the corrected metadata.

Use a deterministic/safe rule.

Prefer the existing valid Default if one can be determined.

Otherwise fall back to:

```text
default
```

---

# 20. No Default — Recovery

If no discovered Project contains:

```json
"isDefault": true
```

then make:

```text
default
```

the Default Project.

If `default` does not exist and is required as the fallback, create it.

---

# 21. Project Discovery on Startup

On every application startup:

1. Locate `Inklings/`.
2. Detect whether migration from the pre-Project structure is needed.
3. Perform migration if required.
4. Scan immediate Project directories.
5. Read each `.inklings-project.json`.
6. Initialize missing metadata.
7. Reconcile Default Project status.
8. Remove the effect of stale/nonexistent Project references.
9. Build the Project list used by the application.

Do not implement continuous filesystem watching.

Startup reconciliation is sufficient for 17A.

---

# 22. Project Creation — Data Layer Only

Provide a reusable application-level operation for creating a Project.

Conceptually:

```text
createProject(name, fontColor, isDefault)
```

The exact method/class names are up to Gemini.

Creating a Project must:

1. Validate the Project Name.
2. Verify that the directory does not already exist.
3. Create:

```text
Inklings/<Project Name>/
```

4. Create:

```text
.inklings-project.json
08 Dailies/01 Inbox/
99 Operations/99 Log/
```

5. Store the selected Font Color.
6. Enforce the single-Default rule.

Do not create a Markdown document during Project creation.

No Project creation UI is required yet.

---

# 23. Project Name Validation

Project names must:

* Not be empty.
* Not consist only of whitespace.
* Be safe as Android directory names.
* Not collide with an existing Project directory.

Trim leading/trailing whitespace before validation.

Do not silently alter a Project name into something substantially different.

Fail safely if the name cannot be used.

---

# 24. Duplicate Project Names

If:

```text
Inklings/Research/
```

already exists, attempting to create another Project named:

```text
Research
```

must fail.

Do not:

* Overwrite it.
* Merge it.
* Delete it.
* Add files into it accidentally.

---

# 25. Existing Folder Migration

Before Requirement 17A, the application may currently have:

```text
Inklings/
├── 08 Dailies/
│   └── 01 Inbox/
└── 99 Operations/
    └── 99 Log/
```

Requirement 17A must migrate this existing application structure into the
initial `default` Project.

After migration:

```text
Inklings/
└── default/
    ├── .inklings-project.json
    ├── 08 Dailies/
    │   └── 01 Inbox/
    └── 99 Operations/
        └── 99 Log/
```

The existing `08 Dailies` and `99 Operations` trees move under:

```text
Inklings/default/
```

---

# 26. Migration Safety

Migration must preserve exactly:

* Existing Markdown files
* Existing log files
* Existing filenames
* Existing timestamps
* Existing file contents
* Existing year/month log directories
* Existing subdirectories

Do not regenerate:

* DA filenames
* BAS filenames
* timestamps

Do not modify Markdown content.

Do not modify BAS content.

---

# 27. Migration Must Be One-Time and Idempotent

Migration must be safe if startup/initialization occurs repeatedly.

After:

```text
Inklings/default/
```

has been established, future startups must not produce:

```text
Inklings/default/default/
```

or:

```text
Inklings/default 2/
```

or duplicate the old files.

Initialization logic must be idempotent.

---

# 28. Conservative Migration

If `Inklings/` contains unexpected user-created data, do not blindly move or
delete it.

Only migrate the existing application structures that can be confidently
identified, especially:

```text
08 Dailies/
99 Operations/
```

Preserving user data is more important than forcing every object into the new
structure.

---

# 29. Metadata Created During Migration

During migration, create:

```text
Inklings/default/.inklings-project.json
```

with:

```text
Font Color = current/default application font color
isDefault = true
```

Do not insert Project metadata into existing Markdown files.

---

# 30. Default Project Lookup

Expose a reliable application-level way to retrieve the current Default
Project.

Conceptually:

```text
getDefaultProject()
```

This should return a Project that actually exists in the filesystem.

Do not return stale/nonexistent Project metadata.

Later requirements will use this to determine where new files are created.

---

# 31. Document Project Association

The application must be able to determine a saved document's Project from its
actual path.

Example:

```text
Inklings/Research/08 Dailies/01 Inbox/
    DA-2026-08-22-SAT-10_15_30.md
```

belongs to:

```text
Research
```

Do not infer its Project from whichever Project happens to be Default.

---

# 32. Timestamp Compatibility

Do not alter the existing timestamp-based file conventions.

Main file example:

```text
DA-2026-08-22-SAT-10_15_30.md
```

Associated BAS example:

```text
BAS-2026-08-22-SAT-10_15_30.md
```

The exact existing timestamp convention in the current implementation must be
preserved.

Requirement 17C will later rely on this timestamp association when moving a
document and its log together.

---

# 33. No File Movement Between Projects Yet

Do NOT implement normal document movement between Projects in 17A.

Do NOT add:

```text
Move to Project
```

functionality.

Do NOT move associated BAS files between existing Projects.

The only movement in this requirement is the one-time migration of the
pre-Project structure into:

```text
Inklings/default/
```

Normal Project-to-Project movement belongs to Requirement 17C.

---

# 34. No Project Rename

Do NOT implement:

* Project rename
* Project directory rename
* Project merge

The folder name is the Project name for now.

---

# 35. No In-App Project Delete

Do NOT add a Delete Project action to the application.

The supported deletion mechanism for now is:

```text
Android Files
    ↓
Delete Project folder
    ↓
Restart application
    ↓
Project disappears
```

---

# 36. No Project UI Yet

Do not add:

* Project button
* Project selector
* Project list
* Project dialog
* Project editor
* Color picker

Those belong to Requirement 17B.

17A implements storage and data-model functionality only.

---

# 37. Requirement 16 Compatibility

Requirement 16 must remain intact.

### Fresh unsaved document

If content is empty/whitespace only:

```text
Do not create Markdown file
Do not create BAS file
```

### Previously saved document

If the user deletes all content:

```text
Save empty content to the existing file
```

Adding Projects must not change these rules.

---

# 38. Existing Save Behavior

Do not redesign Save/Auto-save/New/Close in this requirement except for the
minimum Project-path integration required by the new storage structure.

Existing behavior must remain intact.

---

# 39. Existing Log Behavior

Every Project contains:

```text
99 Operations/99 Log/
```

Do not change:

* BAS contents
* Writing-time calculation
* BAS naming
* Year/month directory behavior

Only the Project layer is new.

---

# 40. Existing Editor Behavior

Do not change:

* Courier Prime
* Font size
* Letter spacing
* Word spacing
* Line spacing
* Margins
* Fade behavior
* Cursor behavior
* Auto-capitalization
* Double-space period behavior
* Typewriter sounds
* Settings
* Timer
* Full-screen mode
* Keyboard shortcuts
* Action controls

Project Font Color is stored in 17A but does not require the UI/changeable
visual behavior until 17B.

---

# 41. Architecture

Keep Project handling separated from the editor UI.

The implementation should conceptually provide reusable operations such as:

```text
discoverProjects()
createProject(...)
getDefaultProject()
getProjectForDocument(...)
ensureProjectMetadata(...)
```

These names are illustrative only.

Use architecture appropriate to the existing application.

The important separation is:

```text
Filesystem / Project layer
        ↓
Application/editor logic
        ↓
UI added later
```

---

# 42. Error Handling

Project initialization and migration must fail safely.

Do not:

* Delete user documents
* Delete user logs
* Overwrite an existing Project directory
* Lose metadata silently
* Claim migration succeeded if required file operations failed

If migration fails partway through, preserve user data and return/report a
clear internal error state.

Do not continue destructively.

---

# Testing

## Test 1 — Migration

Before updating:

```text
Inklings/
├── 08 Dailies/
└── 99 Operations/
```

Run the 17A version.

Expected:

```text
Inklings/
└── default/
    ├── .inklings-project.json
    ├── 08 Dailies/
    └── 99 Operations/
```

Verify all previous files remain present.

---

## Test 2 — Metadata

Inspect:

```text
Inklings/default/.inklings-project.json
```

Verify it exists and contains at least:

```text
fontColor
isDefault
```

Verify:

```text
isDefault = true
```

---

## Test 3 — Restart Repeatedly

Restart the application several times.

Verify:

* No repeated migration
* No duplicate files
* No nested `default/default/`
* No duplicate metadata
* No duplicate Projects

---

## Test 4 — Filesystem Project Discovery

Create/copy a valid directory:

```text
Inklings/Research/
```

with the required Project subfolders.

Restart the app.

Verify the Project layer discovers:

```text
Research
```

---

## Test 5 — Missing Metadata

Remove or omit:

```text
Research/.inklings-project.json
```

Restart.

Verify:

* Research remains a valid Project.
* Metadata is initialized.
* Default Font Color is supplied.
* Research does not unexpectedly become Default.

---

## Test 6 — External Project Deletion

Have:

```text
Inklings/
├── default/
├── Writing/
└── Research/
```

Delete:

```text
Research/
```

using Android Files.

Restart.

Verify:

* Research is gone.
* Research is not recreated.
* No stale Project metadata makes it reappear.

---

## Test 7 — External Default Project Deletion

Have:

```text
default
Writing
Research
```

with:

```text
Writing → isDefault = true
```

Delete:

```text
Writing/
```

externally.

Restart.

Expected:

```text
default → isDefault = true
```

Writing must not be recreated.

---

## Test 8 — Delete Fallback `default`

Externally delete:

```text
Inklings/default/
```

while no other usable Default Project exists.

Restart.

Verify:

```text
Inklings/default/
```

is safely recreated with:

```text
.inklings-project.json
08 Dailies/01 Inbox/
99 Operations/99 Log/
```

---

## Test 9 — Multiple Default Metadata

Manually/create a test state where two Projects have:

```json
"isDefault": true
```

Restart.

Verify the application repairs the state so exactly one Project remains
Default.

---

## Test 10 — Create Project Through Data Layer

Create:

```text
Research
```

through the internal Project creation function.

Verify:

```text
Inklings/Research/
├── .inklings-project.json
├── 08 Dailies/
│   └── 01 Inbox/
└── 99 Operations/
    └── 99 Log/
```

exists.

---

## Test 11 — Requirement 16 Regression

Verify:

* Fresh empty document → no file
* Fresh whitespace-only document → no file
* Previously saved document cleared → existing file becomes empty

---

## Test 12 — Existing Feature Regression

Verify:

* Save
* Auto-save
* New
* Close
* BAS logging
* Fade
* Timer
* Typewriter sounds
* Settings
* Full-screen
* Ctrl+S
* Ctrl+N
* Ctrl+Q

continue to work.

---

# Completion Criteria

Requirement 17A is complete when:

1. The root directory is consistently `Inklings/`.
2. Projects are represented by immediate directories below `Inklings/`.
3. The filesystem is authoritative for Project existence.
4. Every application-created Project contains `.inklings-project.json`.
5. Metadata is stored inside the Project directory.
6. Project Name is derived from the directory name.
7. Font Color is stored in Project metadata.
8. Default status is stored in Project metadata.
9. The initial Project is named exactly `default`.
10. Existing pre-Project application content is safely migrated under
    `Inklings/default/`.
11. Migration preserves filenames, timestamps, contents and folder structure.
12. Migration is idempotent.
13. Missing metadata is safely initialized.
14. Externally added valid Project directories can be discovered.
15. Externally deleted Projects disappear after restart.
16. Deleted Projects are not recreated from stale metadata.
17. A deleted configured Default Project falls back to `default`.
18. `default` can be recreated when necessary as the required fallback.
19. Multiple-default inconsistencies are repaired.
20. Duplicate Project directories are prevented.
21. Invalid Project names are rejected.
22. Project creation exists at the data layer.
23. The application can retrieve the actual Default Project.
24. The application can determine a saved document's Project from its path.
25. Existing timestamp-based DA/BAS association remains unchanged.
26. No Project management UI is added.
27. No Project rename functionality is added.
28. No in-app Project deletion functionality is added.
29. No normal Project-to-Project file movement is added.
30. Requirement 16 remains intact.
31. Existing editor functionality remains intact.
32. No unrelated behavior is changed.

---

# Required Source-Code Comments

Add concise comments explaining:

1. Why the filesystem is the source of truth for Project existence.
2. Why `.inklings-project.json` lives inside the Project directory.
3. Why deleting a Project folder externally also naturally removes its
   metadata.
4. Why there is no authoritative external Project registry.
5. Why Project Name comes from the directory name.
6. Why `default` is the initial and fallback Project.
7. How the single-Default rule is reconciled.
8. How missing metadata is initialized.
9. Why externally deleted Projects must not be recreated.
10. Why migration into `default` must be idempotent.
11. How a document's Project is determined from its actual path.
12. Why normal file movement, Project UI, rename and in-app deletion are
    deliberately deferred.

---

# After Implementation

Provide a short summary explaining:

1. Which files were changed.
2. How Projects are discovered.
3. How `Inklings/` is scanned.
4. How `.inklings-project.json` is structured.
5. How metadata is read/written.
6. How the `default` Project is created.
7. How existing files are migrated into `default`.
8. How migration is kept idempotent.
9. How externally added Projects are handled.
10. How missing metadata is handled.
11. How externally deleted Projects are handled.
12. What happens when the active Default Project has been deleted.
13. How the application determines a document's Project.
14. Confirmation that no Project UI, rename, in-app deletion, or normal
    Project-to-Project movement was added.
15. Confirmation that existing application behavior remains unchanged.

Stop after implementing Requirement 17A.
