# Requirement 17A — Project Storage and Data Model

## Objective

Introduce the underlying **Project** concept into the application.

This requirement establishes the Project data model and filesystem structure.

Do **not** implement the Project management UI yet.

Do **not** implement moving files between Projects yet.

Do **not** implement Project renaming or deletion.

Those will be handled by later requirements.

The goal of 17A is to establish a clean foundation that later requirements
can build upon.

---

# 1. Project Concept

The application currently stores its files under:

# Requirement 17A — Project Storage and Data Model

## Objective

Introduce the underlying **Project** concept into the application.

This requirement establishes the Project data model and filesystem structure.

Do **not** implement the Project management UI yet.

Do **not** implement moving files between Projects yet.

Do **not** implement Project renaming or deletion.

Those will be handled by later requirements.

The goal of 17A is to establish a clean foundation that later requirements
can build upon.

---

# 1. Project Concept

The application currently stores its files under:

```
Inklings/
```

Requirement 17A introduces a new Project layer immediately below `Inklings/`.

Each Project is represented by a directory.

Example:

```
Inklings/
├── Project A/
├── Project B/
└── Project C/
```

Each Project has its own copy of the application's existing folder structure.

---

# 2. Project Directory Structure

Each Project must contain the existing folder structure used by the
application.

For example:

```
Inklings/
└── Project A/
    ├── 08 Dailies/
    │   └── 01 Inbox/
    │
    └── 99 Operations/
        └── 99 Log/
```

The existing internal folder structure must not be redesigned.

The Project layer is simply added above it.

---

# 3. Required Project Properties

Each Project must have exactly these properties for Requirement 17A:

1. Project Name
2. Font Color
3. Default Project

These properties must be stored persistently.

---

# 4. Project Name

The Project Name identifies the Project.

It will eventually also be used as the Project directory name.

For example:

```
Project Name: Writing
```

corresponds to:

```
Inklings/Writing/
```

Project names must not be empty or consist only of whitespace.

Do not implement Project renaming in this requirement.

---

# 5. Font Color

Each Project has a configurable font color.

Store the color as Project metadata.

The actual visual color-selection UI will be implemented in Requirement 17B.

For 17A:

* Store the color in a suitable persistent format.
* Provide a sensible default color for newly created Projects.
* Make the Project model capable of returning its configured color.

Do not modify the Markdown content to store the color.

Do not add HTML, Markdown, YAML front matter, or other formatting to represent
the Project color.

The color is application-level presentation metadata.

---

# 6. Default Project

Each Project has a Boolean:

```
isDefault
```

There must be **at most one** Default Project.

The application must enforce this rule at the data/model level.

When a Project becomes the Default Project:

* Its `isDefault` becomes true.
* Every other Project must become non-default.

This prevents multiple Projects from being marked as the Default Project.

---

# 7. Default Project Requirement

The application must always be able to determine the Default Project once
the Project system has been initialized.

If no Project exists when the new Project system is initialized, create an
appropriate initial Project and make it the Default Project.

This ensures that the existing New-document functionality always has a valid
destination.

---

# 8. New Project Creation — Data Layer

17A must provide a reusable application-level operation for creating a
Project.

Conceptually:

```
createProject(name, fontColor, isDefault)
```

The exact method/class names are up to the implementation.

Creating a Project must:

1. Validate the Project Name.
2. Create the Project directory under `Inklings/`.
3. Create the required existing folder structure inside it.
4. Store the Project metadata.
5. Enforce the single-default rule.

Do not create a Markdown document when creating a Project.

---

# 9. Project Configuration Persistence

Project configuration must survive application restarts.

At minimum, persist:

```
Project Name
Font Color
Default Project
```

Do not rely solely on in-memory variables.

The exact persistence mechanism is up to the existing Android architecture.

Use the application's existing persistence approach where appropriate.

---

# 10. Project Metadata Must Be Separate From Markdown

Do not store Project metadata inside individual Markdown files.

For example, do NOT add:

```markdown
project: Writing
font-color: ...
default-project: true
```

to the Markdown document.

Project information belongs to the application's Project configuration.

---

# 11. Project Identity

The implementation should maintain a stable internal identity for a Project
rather than relying only on its display name.

For example, the Project model may contain:

```
projectId
projectName
fontColor
isDefault
```

The exact implementation is up to Gemini.

The important point is that Project identity should not depend entirely on
the current UI display name.

This will make later Project functionality safer.

---

# 12. Project Directory Name

For Requirement 17A, the Project Name will initially correspond to the
directory name.

For example:

```
Project Name = Research
```

creates:

```
Inklings/Research/
```

Validate the name before creating the directory.

At minimum, reject:

* Empty names.
* Whitespace-only names.
* Names containing characters that cannot safely be used as Android
  directory names.

Do not implement rename functionality.

---

# 13. Duplicate Project Names

Do not allow two Projects to have the same directory name.

If creation would result in a directory collision:

* Do not overwrite the existing Project.
* Do not merge the Projects.
* Fail the creation operation safely.

The UI for displaying this error will be handled in 17B.

---

# 14. Project Folder Initialization

When a Project is created, ensure the required directories exist.

At minimum:

```
<Project>/
    08 Dailies/
        01 Inbox/

    99 Operations/
        99 Log/
```

If the existing application currently has additional required directories
inside these structures, preserve and initialize those as well.

Do not remove or rename any existing directories.

---

# 15. Existing Files — Migration

Requirement 17A must account for documents created by the application before
the Project system existed.

Existing content must not become inaccessible.

Before changing the existing filesystem structure, inspect how the current
application stores its files.

The migration must preserve:

* Markdown files.
* Log files.
* Existing filenames.
* Existing file contents.
* Existing folder structure.

A safe initial migration strategy is to associate the existing structure with
an initial Project.

For example:

```
Inklings/
    Existing/
        08 Dailies/
        99 Operations/
```

However, **do not blindly move files** if doing so could break existing paths
or application state.

Implement the safest migration compatible with the existing application's
current storage design.

---

# 16. Migration Must Be One-Time and Safe

Migration must not repeatedly move or duplicate files every time the
application starts.

The application should be able to determine whether Project initialization
has already occurred.

If migration has already completed:

* Do not repeat it.
* Do not duplicate files.
* Do not create duplicate Projects.

---

# 17. Existing Project Initialization

If the application has never used Projects before:

1. Initialize the Project system.
2. Create/identify the initial Project.
3. Ensure it is the Default Project.
4. Ensure its folder structure exists.
5. Preserve access to existing documents and logs.

Do not require the user to manually configure the application before existing
documents can be accessed.

---

# 18. New Documents — Preparation for 17B

17A should expose a reliable way for the rest of the application to obtain
the Default Project.

For example:

```
getDefaultProject()
```

The actual method name is up to the implementation.

Requirement 17B will use this when implementing the Project UI and New-file
behavior.

Do not redesign the New UI in 17A.

---

# 19. Existing Document Project Association

The Project model must support associating an existing document with its
Project based on its filesystem location.

For example:

```
Inklings/Research/08 Dailies/01 Inbox/note.md
```

belongs to:

```
Research
```

Do not assume that every currently open document belongs to the Default
Project.

The current document's Project will be important for later requirements.

---

# 20. No File-Moving Functionality Yet

Do NOT implement document movement between Projects in 17A.

Do NOT move:

* Markdown files between Projects.
* Log files between Projects.

That functionality will be implemented separately in Requirement 17C.

17A only establishes the Project structure required for that later feature.

---

# 21. No Project Rename

Do NOT implement:

* Rename Project.
* Rename Project directory.
* Merge Projects.

These are explicitly outside the scope of 17A.

---

# 22. No Project Delete

Do NOT implement Project deletion.

Project deletion is outside the scope of 17A and the planned Project
requirements.

---

# 23. No Project UI Yet

Do not redesign the existing writing interface in this requirement.

Do not add:

* Project selector.
* Project list.
* Project management button.
* Project dialog.
* Color picker UI.

These belong to Requirement 17B.

The underlying model and persistence should nevertheless be ready for those
features.

---

# 24. No Changes to Existing Editor Behavior

Do not change:

* Fade behavior.
* Font size.
* Letter spacing.
* Word spacing.
* Line spacing.
* Margins.
* Cursor behavior.
* Auto-capitalization.
* Typewriter sounds.
* Settings.
* Timer.
* Keyboard shortcuts.
* Save behavior except where necessary to safely establish the Project
  destination.

---

# 25. Requirement 16 Compatibility

The Project implementation must preserve Requirement 16.

For a fresh document that contains only whitespace:

* Do not create a Markdown file.
* Do not create a log file.

For a previously saved document:

* Empty content may still be intentionally saved.

Project initialization must not break this behavior.

---

# 26. Log Folder

Every Project must contain:

```
99 Operations/99 Log/
```

This is where the existing writing-session log files will eventually be
stored.

Do not change the log filename format in 17A.

Do not change the timestamp relationship between Markdown files and logs.

---

# 27. Timestamp Compatibility

Requirement 17A must not alter the existing timestamp-based filenames.

For example:

```
DA-2026-08-22-SAT-10_15_30.md
```

and:

```
BAS-2026-08-22-SAT-10_15_30.md
```

must continue to use the same timestamp convention.

The later document-moving requirement will depend on this association.

---

# 28. Architecture

Implement the Project functionality in a clean, reusable way.

The Project model/storage logic should not be tightly coupled to:

* The editor UI.
* The Project management UI.
* The color-picker UI.
* The timer.
* The typewriter sound system.

Keep Project data and filesystem operations in an appropriate application
layer.

The exact architecture and class names are up to Gemini, provided they fit
the existing application.

---

# 29. Error Handling

Project creation and initialization must fail safely.

Do not:

* Delete existing files.
* Overwrite an existing Project.
* Silently lose documents.
* Silently lose logs.
* Leave the application believing a Project exists when its directory was
  not successfully created.

Handle filesystem errors appropriately.

---

# 30. Testing

## Test 1 — First Initialization

Start the application with no Project configuration.

Expected:

* Project system initializes.
* An initial Project exists.
* A Default Project exists.
* Required folders exist.

---

## Test 2 — Project Creation Through Data Layer

Create a Project programmatically/application-internally with:

```
Name = Research
Font Color = a valid color
Default = false
```

Expected:

```
Inklings/Research/
```

exists with:

```
08 Dailies/01 Inbox/
99 Operations/99 Log/
```

---

## Test 3 — Default Project

Create Project A as Default.

Create Project B as non-default.

Expected:

* A is Default.
* B is not Default.

---

## Test 4 — Change Default

Make B the Default.

Expected:

* B is Default.
* A is no longer Default.
* There is never more than one Default Project.

---

## Test 5 — Persistence

Create Projects.

Close and reopen the application.

Expected:

* Projects remain available.
* Names remain unchanged.
* Font colors remain unchanged.
* Default Project remains unchanged.

---

## Test 6 — Duplicate Project

Attempt to create a second Project with the same directory name.

Expected:

* Creation fails safely.
* Existing Project remains untouched.

---

## Test 7 — Invalid Project Name

Attempt to create a Project with:

```
""
```

or whitespace-only content.

Expected:

* Creation fails.
* No invalid directory is created.

---

## Test 8 — Existing Files

Run the application against an installation containing existing Markdown
and log files.

Expected:

* Existing files remain accessible.
* Existing filenames remain unchanged.
* Existing contents remain unchanged.
* Existing logs remain accessible.

---

## Test 9 — Restart After Migration

Restart the application after Project initialization/migration.

Expected:

* Migration does not run again.
* No duplicate Project is created.
* No files are duplicated.

---

## Test 10 — Requirement 16 Regression

Verify:

* Fresh empty document is not saved.
* Previously saved document can still be intentionally cleared.

---

# Completion Criteria

Requirement 17A is complete when:

1. The application has a persistent Project model.
2. Projects exist as directories under `Inklings/`.
3. Each Project has the existing internal folder structure.
4. Project Name is persisted.
5. Project Font Color is persisted.
6. Default Project status is persisted.
7. At most one Project can be Default.
8. A usable Default Project always exists after initialization.
9. Project creation works at the data/storage layer.
10. Duplicate Project directories are prevented.
11. Invalid Project names are rejected safely.
12. Existing pre-Project files remain accessible.
13. Migration, if required, is safe and does not repeat.
14. The application can determine the Project associated with a document from
    its path.
15. The application can retrieve the Default Project.
16. No Project UI is implemented yet.
17. No Project rename functionality is implemented.
18. No Project deletion functionality is implemented.
19. No document-moving functionality is implemented.
20. Requirement 16 behavior remains intact.
21. Existing editor functionality remains intact.
22. No unrelated behavior is changed.

---

# Required Source-Code Comments

Add concise comments explaining:

1. Why Projects are a storage layer above the existing folder structure.
2. Why Project metadata is persisted separately from Markdown files.
3. How the single Default Project rule is enforced.
4. How the application identifies the Default Project.
5. How a document's Project is determined from its filesystem location.
6. Why migration must be safe and one-time.
7. Why document/log movement is deliberately not implemented in 17A.
8. Why Project Font Color is presentation metadata and is not written into
   Markdown.

---

# After Implementation

Provide a short summary explaining:

1. Where the Project model was implemented.
2. How Project metadata is persisted.
3. How Project directories are created.
4. How the internal folder structure is initialized.
5. How the Default Project is enforced.
6. How existing files are handled/migrated.
7. How the application determines a document's Project.
8. How the Default Project can be retrieved by later functionality.
9. Confirmation that no Project UI was added yet.
10. Confirmation that no file-moving, rename, or delete functionality was
    added.
11. Confirmation that existing functionality was not otherwise changed.

Stop after implementing this requirement.

Requirement 17A introduces a new Project layer immediately below `inkings/`.

Each Project is represented by a directory.

Example:

```
inkings/
├── Project A/
├── Project B/
└── Project C/
```

Each Project has its own copy of the application's existing folder structure.

---

# 2. Project Directory Structure

Each Project must contain the existing folder structure used by the
application.

For example:

```
inkings/
└── Project A/
    ├── 08 Dailies/
    │   └── 01 Inbox/
    │
    └── 99 Operations/
        └── 99 Log/
```

The existing internal folder structure must not be redesigned.

The Project layer is simply added above it.

---

# 3. Required Project Properties

Each Project must have exactly these properties for Requirement 17A:

1. Project Name
2. Font Color
3. Default Project

These properties must be stored persistently.

---

# 4. Project Name

The Project Name identifies the Project.

It will eventually also be used as the Project directory name.

For example:

```
Project Name: Writing
```

corresponds to:

```
inkings/Writing/
```

Project names must not be empty or consist only of whitespace.

Do not implement Project renaming in this requirement.

---

# 5. Font Color

Each Project has a configurable font color.

Store the color as Project metadata.

The actual visual color-selection UI will be implemented in Requirement 17B.

For 17A:

* Store the color in a suitable persistent format.
* Provide a sensible default color for newly created Projects.
* Make the Project model capable of returning its configured color.

Do not modify the Markdown content to store the color.

Do not add HTML, Markdown, YAML front matter, or other formatting to represent
the Project color.

The color is application-level presentation metadata.

---

# 6. Default Project

Each Project has a Boolean:

```
isDefault
```

There must be **at most one** Default Project.

The application must enforce this rule at the data/model level.

When a Project becomes the Default Project:

* Its `isDefault` becomes true.
* Every other Project must become non-default.

This prevents multiple Projects from being marked as the Default Project.

---

# 7. Default Project Requirement

The application must always be able to determine the Default Project once
the Project system has been initialized.

If no Project exists when the new Project system is initialized, create an
appropriate initial Project and make it the Default Project.

This ensures that the existing New-document functionality always has a valid
destination.

---

# 8. New Project Creation — Data Layer

17A must provide a reusable application-level operation for creating a
Project.

Conceptually:

```
createProject(name, fontColor, isDefault)
```

The exact method/class names are up to the implementation.

Creating a Project must:

1. Validate the Project Name.
2. Create the Project directory under `inkings/`.
3. Create the required existing folder structure inside it.
4. Store the Project metadata.
5. Enforce the single-default rule.

Do not create a Markdown document when creating a Project.

---

# 9. Project Configuration Persistence

Project configuration must survive application restarts.

At minimum, persist:

```
Project Name
Font Color
Default Project
```

Do not rely solely on in-memory variables.

The exact persistence mechanism is up to the existing Android architecture.

Use the application's existing persistence approach where appropriate.

---

# 10. Project Metadata Must Be Separate From Markdown

Do not store Project metadata inside individual Markdown files.

For example, do NOT add:

```markdown
project: Writing
font-color: ...
default-project: true
```

to the Markdown document.

Project information belongs to the application's Project configuration.

---

# 11. Project Identity

The implementation should maintain a stable internal identity for a Project
rather than relying only on its display name.

For example, the Project model may contain:

```
projectId
projectName
fontColor
isDefault
```

The exact implementation is up to Gemini.

The important point is that Project identity should not depend entirely on
the current UI display name.

This will make later Project functionality safer.

---

# 12. Project Directory Name

For Requirement 17A, the Project Name will initially correspond to the
directory name.

For example:

```
Project Name = Research
```

creates:

```
inkings/Research/
```

Validate the name before creating the directory.

At minimum, reject:

* Empty names.
* Whitespace-only names.
* Names containing characters that cannot safely be used as Android
  directory names.

Do not implement rename functionality.

---

# 13. Duplicate Project Names

Do not allow two Projects to have the same directory name.

If creation would result in a directory collision:

* Do not overwrite the existing Project.
* Do not merge the Projects.
* Fail the creation operation safely.

The UI for displaying this error will be handled in 17B.

---

# 14. Project Folder Initialization

When a Project is created, ensure the required directories exist.

At minimum:

```
<Project>/
    08 Dailies/
        01 Inbox/

    99 Operations/
        99 Log/
```

If the existing application currently has additional required directories
inside these structures, preserve and initialize those as well.

Do not remove or rename any existing directories.

---

# 15. Existing Files — Migration

Requirement 17A must account for documents created by the application before
the Project system existed.

Existing content must not become inaccessible.

Before changing the existing filesystem structure, inspect how the current
application stores its files.

The migration must preserve:

* Markdown files.
* Log files.
* Existing filenames.
* Existing file contents.
* Existing folder structure.

A safe initial migration strategy is to associate the existing structure with
an initial Project.

For example:

```
inkings/
    Existing/
        08 Dailies/
        99 Operations/
```

However, **do not blindly move files** if doing so could break existing paths
or application state.

Implement the safest migration compatible with the existing application's
current storage design.

---

# 16. Migration Must Be One-Time and Safe

Migration must not repeatedly move or duplicate files every time the
application starts.

The application should be able to determine whether Project initialization
has already occurred.

If migration has already completed:

* Do not repeat it.
* Do not duplicate files.
* Do not create duplicate Projects.

---

# 17. Existing Project Initialization

If the application has never used Projects before:

1. Initialize the Project system.
2. Create/identify the initial Project.
3. Ensure it is the Default Project.
4. Ensure its folder structure exists.
5. Preserve access to existing documents and logs.

Do not require the user to manually configure the application before existing
documents can be accessed.

---

# 18. New Documents — Preparation for 17B

17A should expose a reliable way for the rest of the application to obtain
the Default Project.

For example:

```
getDefaultProject()
```

The actual method name is up to the implementation.

Requirement 17B will use this when implementing the Project UI and New-file
behavior.

Do not redesign the New UI in 17A.

---

# 19. Existing Document Project Association

The Project model must support associating an existing document with its
Project based on its filesystem location.

For example:

```
inkings/Research/08 Dailies/01 Inbox/note.md
```

belongs to:

```
Research
```

Do not assume that every currently open document belongs to the Default
Project.

The current document's Project will be important for later requirements.

---

# 20. No File-Moving Functionality Yet

Do NOT implement document movement between Projects in 17A.

Do NOT move:

* Markdown files between Projects.
* Log files between Projects.

That functionality will be implemented separately in Requirement 17C.

17A only establishes the Project structure required for that later feature.

---

# 21. No Project Rename

Do NOT implement:

* Rename Project.
* Rename Project directory.
* Merge Projects.

These are explicitly outside the scope of 17A.

---

# 22. No Project Delete

Do NOT implement Project deletion.

Project deletion is outside the scope of 17A and the planned Project
requirements.

---

# 23. No Project UI Yet

Do not redesign the existing writing interface in this requirement.

Do not add:

* Project selector.
* Project list.
* Project management button.
* Project dialog.
* Color picker UI.

These belong to Requirement 17B.

The underlying model and persistence should nevertheless be ready for those
features.

---

# 24. No Changes to Existing Editor Behavior

Do not change:

* Fade behavior.
* Font size.
* Letter spacing.
* Word spacing.
* Line spacing.
* Margins.
* Cursor behavior.
* Auto-capitalization.
* Typewriter sounds.
* Settings.
* Timer.
* Keyboard shortcuts.
* Save behavior except where necessary to safely establish the Project
  destination.

---

# 25. Requirement 16 Compatibility

The Project implementation must preserve Requirement 16.

For a fresh document that contains only whitespace:

* Do not create a Markdown file.
* Do not create a log file.

For a previously saved document:

* Empty content may still be intentionally saved.

Project initialization must not break this behavior.

---

# 26. Log Folder

Every Project must contain:

```
99 Operations/99 Log/
```

This is where the existing writing-session log files will eventually be
stored.

Do not change the log filename format in 17A.

Do not change the timestamp relationship between Markdown files and logs.

---

# 27. Timestamp Compatibility

Requirement 17A must not alter the existing timestamp-based filenames.

For example:

```
DA-2026-08-22-SAT-10_15_30.md
```

and:

```
BAS-2026-08-22-SAT-10_15_30.md
```

must continue to use the same timestamp convention.

The later document-moving requirement will depend on this association.

---

# 28. Architecture

Implement the Project functionality in a clean, reusable way.

The Project model/storage logic should not be tightly coupled to:

* The editor UI.
* The Project management UI.
* The color-picker UI.
* The timer.
* The typewriter sound system.

Keep Project data and filesystem operations in an appropriate application
layer.

The exact architecture and class names are up to Gemini, provided they fit
the existing application.

---

# 29. Error Handling

Project creation and initialization must fail safely.

Do not:

* Delete existing files.
* Overwrite an existing Project.
* Silently lose documents.
* Silently lose logs.
* Leave the application believing a Project exists when its directory was
  not successfully created.

Handle filesystem errors appropriately.

---

# 30. Testing

## Test 1 — First Initialization

Start the application with no Project configuration.

Expected:

* Project system initializes.
* An initial Project exists.
* A Default Project exists.
* Required folders exist.

---

## Test 2 — Project Creation Through Data Layer

Create a Project programmatically/application-internally with:

```
Name = Research
Font Color = a valid color
Default = false
```

Expected:

```
inkings/Research/
```

exists with:

```
08 Dailies/01 Inbox/
99 Operations/99 Log/
```

---

## Test 3 — Default Project

Create Project A as Default.

Create Project B as non-default.

Expected:

* A is Default.
* B is not Default.

---

## Test 4 — Change Default

Make B the Default.

Expected:

* B is Default.
* A is no longer Default.
* There is never more than one Default Project.

---

## Test 5 — Persistence

Create Projects.

Close and reopen the application.

Expected:

* Projects remain available.
* Names remain unchanged.
* Font colors remain unchanged.
* Default Project remains unchanged.

---

## Test 6 — Duplicate Project

Attempt to create a second Project with the same directory name.

Expected:

* Creation fails safely.
* Existing Project remains untouched.

---

## Test 7 — Invalid Project Name

Attempt to create a Project with:

```
""
```

or whitespace-only content.

Expected:

* Creation fails.
* No invalid directory is created.

---

## Test 8 — Existing Files

Run the application against an installation containing existing Markdown
and log files.

Expected:

* Existing files remain accessible.
* Existing filenames remain unchanged.
* Existing contents remain unchanged.
* Existing logs remain accessible.

---

## Test 9 — Restart After Migration

Restart the application after Project initialization/migration.

Expected:

* Migration does not run again.
* No duplicate Project is created.
* No files are duplicated.

---

## Test 10 — Requirement 16 Regression

Verify:

* Fresh empty document is not saved.
* Previously saved document can still be intentionally cleared.

---

# Completion Criteria

Requirement 17A is complete when:

1. The application has a persistent Project model.
2. Projects exist as directories under `inkings/`.
3. Each Project has the existing internal folder structure.
4. Project Name is persisted.
5. Project Font Color is persisted.
6. Default Project status is persisted.
7. At most one Project can be Default.
8. A usable Default Project always exists after initialization.
9. Project creation works at the data/storage layer.
10. Duplicate Project directories are prevented.
11. Invalid Project names are rejected safely.
12. Existing pre-Project files remain accessible.
13. Migration, if required, is safe and does not repeat.
14. The application can determine the Project associated with a document from
    its path.
15. The application can retrieve the Default Project.
16. No Project UI is implemented yet.
17. No Project rename functionality is implemented.
18. No Project deletion functionality is implemented.
19. No document-moving functionality is implemented.
20. Requirement 16 behavior remains intact.
21. Existing editor functionality remains intact.
22. No unrelated behavior is changed.

---

# Required Source-Code Comments

Add concise comments explaining:

1. Why Projects are a storage layer above the existing folder structure.
2. Why Project metadata is persisted separately from Markdown files.
3. How the single Default Project rule is enforced.
4. How the application identifies the Default Project.
5. How a document's Project is determined from its filesystem location.
6. Why migration must be safe and one-time.
7. Why document/log movement is deliberately not implemented in 17A.
8. Why Project Font Color is presentation metadata and is not written into
   Markdown.

---

# After Implementation

Provide a short summary explaining:

1. Where the Project model was implemented.
2. How Project metadata is persisted.
3. How Project directories are created.
4. How the internal folder structure is initialized.
5. How the Default Project is enforced.
6. How existing files are handled/migrated.
7. How the application determines a document's Project.
8. How the Default Project can be retrieved by later functionality.
9. Confirmation that no Project UI was added yet.
10. Confirmation that no file-moving, rename, or delete functionality was
    added.
11. Confirmation that existing functionality was not otherwise changed.

Stop after implementing this requirement.
