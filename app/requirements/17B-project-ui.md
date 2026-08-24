# Requirement 17B — Project UI, Creation, Default Selection and Font Color

## Objective

Build the user-facing Project functionality on top of the Project storage and
data model implemented in Requirement 17A.

Requirement 17B adds:

* Project-management button
* Project list
* Project creation
* Project selection for management
* Project Font Color editing
* Default Project selection
* Current Project indication
* Project-specific editor Font Color
* Routing of new writing sessions to the Default Project

Do **not** implement moving an existing document between Projects yet.

That functionality belongs to Requirement 17C.

Do **not** implement:

* Project rename
* In-app Project deletion
* Project merge

The filesystem remains the source of truth for Project existence, as defined
in Requirement 17A.

---

# 1. Existing Project Architecture

Requirement 17A established:

```text
Inklings/
├── default/
│   ├── .inklings-project.json
│   ├── 08 Dailies/
│   │   └── 01 Inbox/
│   └── 99 Operations/
│       └── 99 Log/
│
├── Writing/
│   ├── .inklings-project.json
│   ├── 08 Dailies/
│   └── 99 Operations/
│
└── Research/
    ├── .inklings-project.json
    ├── 08 Dailies/
    └── 99 Operations/
```

Do not redesign this structure.

Project existence continues to be determined from the actual directories
inside:

```text
Inklings/
```

---

# 2. Project Management Button

Add a Project-management action button to the writing interface.

The Project button should be positioned on the **right side** of the writing
area.

It should follow the same general visual language as the existing action
controls:

* Vector icon
* Theme-aware
* Compact
* Suitable touch target
* No permanent text label

Use an appropriate Project/folder-style icon.

Do not use an emoji.

---

# 3. Project Button Display Behavior

The Project button should use the same general temporary display behavior
already established for the application's action controls.

It should not permanently clutter the writing area.

When the action controls are visible, the Project button should be available.

Do not change the existing behavior of:

* Save
* New
* Close
* Settings
* Timer

The exact layout should avoid overlap with the Timer control already placed
on the right side.

Use responsive positioning rather than hard-coded screen coordinates.

---

# 4. Opening Project Management

Tapping the Project button opens a compact Project-management panel.

Do not open a completely separate full-screen Activity unless genuinely
required by the existing architecture.

Prefer a dialog, panel, sheet, or similar compact Compose interface that fits
the existing minimalist application design.

---

# 5. Project List

The Project-management panel must show all currently discovered Projects.

Before displaying the list, refresh/reconcile the Projects using the
filesystem-based Project discovery from Requirement 17A.

This is important because the user may have added or deleted Project folders
using Android Files while the application was running.

Conceptually:

```text
Open Project panel
       ↓
Rescan Inklings/
       ↓
Reconcile metadata
       ↓
Display current Projects
```

Do not display stale Projects that no longer exist.

---

# 6. Project List Information

For each Project, show at minimum:

* Project Name
* Font Color visually
* Whether it is the Default Project

Example conceptually:

```text
● default                     Default
● Writing
● Research
```

The colored circle/swatch should represent that Project's configured Font
Color.

Do not display filesystem implementation details such as:

```text
.inklings-project.json
08 Dailies
99 Operations
```

in the Project UI.

---

# 7. Current Document Project Indicator

The UI should make it possible to identify which Project the current writing
session belongs to.

Keep this subtle.

Do not add a large permanent heading such as:

```text
CURRENT PROJECT: WRITING
```

A small indication in the Project-management panel is sufficient.

For example:

```text
Writing        Current
```

The Project associated with the current document/session must be derived from
the session/document's actual Project association, not merely from whichever
Project happens to be Default.

---

# 8. Project Selection

Tapping a Project in the Project list selects it for Project management.

This allows the user to inspect or edit:

* Font Color
* Default Project status

Selecting a Project in this panel must **NOT move the current document**.

Selecting a Project also must **NOT automatically make it the Default
Project**.

Those are separate operations.

---

# 9. Add Project Control

The Project-management panel must provide an obvious control to create a new
Project.

Use a simple:

```text
+
```

or an appropriate Add Project vector icon.

Do not add another permanently visible writing-area button solely for Project
creation.

The Add Project control belongs inside the Project-management interface.

---

# 10. Create Project Dialog

Selecting Add Project opens a Project creation interface.

The user must specify:

1. Project Name
2. Font Color
3. Default Project

The Project Name is specified only during creation.

Once created, Project Name cannot be changed through the application in the
scope of Requirement 17B.

---

# 11. Project Name Creation

Project Name is entered as text.

Examples:

```text
Writing
Research
Book
Journal
```

Use the validation already implemented in Requirement 17A.

At minimum:

* Reject empty names
* Reject whitespace-only names
* Reject unsafe directory names
* Reject duplicate Project directory names

Do not silently overwrite or merge with an existing Project.

---

# 12. Project Name Is Not Editable Later

After Project creation:

```text
Project Name = directory name
```

Do NOT provide a Rename field or Edit Name operation.

For example:

```text
Inklings/Writing/
```

remains:

```text
Writing
```

Project renaming is outside the scope of Requirement 17B.

---

# 13. Font Color Picker

Project Font Color must be visually selectable.

Do NOT require the user to manually enter:

```text
#4285F4
```

or another hexadecimal value.

Provide a visual color-selection interface.

The user should be able to:

* See available colors
* See the currently selected color
* Select another color
* Confirm/apply the selection

---

# 14. Color Picker Design

Keep the color picker simple.

A set of visually selectable color swatches is acceptable and preferred over
an unnecessarily complex professional color-wheel interface.

For example:

```text
● ● ● ● ● ●
● ● ● ● ● ●
```

The exact palette may be chosen to provide a useful range of readable colors.

The user should not have to understand RGB/HEX values.

---

# 15. Font Color Readability

The application should prevent or discourage selecting a color that is
effectively invisible against the current editor background.

At minimum:

* Colors presented by the standard picker should be reasonably readable.
* The picker must work in both light and dark themes.

Do not introduce complicated accessibility configuration as part of this
requirement.

---

# 16. Project Font Color Persistence

When the user changes a Project's Font Color, write the selected color to:

```text
Inklings/<Project>/.inklings-project.json
```

Do not store the color in:

* Markdown
* BAS files
* YAML front matter
* HTML
* A separate Project registry

The existing Project metadata file remains authoritative for this property.

---

# 17. Apply Project Font Color to Editor

The editor text color must reflect the Project associated with the current
writing session.

Example:

```text
Writing Project
fontColor = blue
```

The editor should display the document text using blue.

If:

```text
Research Project
fontColor = green
```

then a writing session associated with Research should use green.

---

# 18. Font Color Is Presentation Only

Changing the Project Font Color must NOT modify the Markdown file.

For example, do not insert:

```html
<span style="color: blue">
```

and do not add:

```yaml
fontColor: blue
```

The underlying `.md` file remains ordinary Markdown/plain text.

---

# 19. Font Color Changes Immediately

If the currently active writing session belongs to the Project whose Font
Color was changed:

```text
Change Font Color
       ↓
Editor updates immediately
```

The user should not need to close and reopen the application.

Document text itself must remain unchanged.

---

# 20. Default Project Control

Project management must allow the user to make a Project the Default Project.

For an existing Project, provide a control such as:

```text
Default Project    [ON/OFF]
```

or an equivalent single-selection UI.

---

# 21. Exactly One Default

When Project B is made Default:

```text
Project B → isDefault = true
```

all other Projects must become:

```text
isDefault = false
```

Persist these changes to the respective:

```text
.inklings-project.json
```

files.

Do not allow multiple effective Default Projects.

---

# 22. Do Not Leave No Default

The UI should not allow the user to simply switch OFF the only active Default
Project and leave no replacement.

Preferred behavior:

If the current Default Project is selected and its Default control is already
ON, do not allow the user to turn it OFF directly.

Instead, making another Project Default automatically removes Default status
from the previous one.

This produces a radio-button-style concept even if the UI uses another
visual control.

---

# 23. `default` Project

The Project named:

```text
default
```

is not permanently forced to remain Default.

For example:

```text
default
Writing
Research
```

The user may make:

```text
Writing
```

the Default Project.

Then:

```text
Writing → isDefault = true
default → isDefault = false
Research → isDefault = false
```

The `default` Project remains the filesystem fallback defined by 17A.

---

# 24. New Project as Default

During Project creation, the user may choose:

```text
Default Project = ON
```

If selected:

* The new Project becomes Default.
* The previous Default Project becomes non-default.

If not selected:

* Existing Default remains unchanged.

---

# 25. New Writing Sessions Use the Default Project

Every new writing session must be assigned to the currently configured
Default Project.

This applies to:

* Application startup creating a fresh session
* NEW button
* Ctrl+N

Conceptually:

```text
Create new writing session
          ↓
getDefaultProject()
          ↓
Bind session to that Project
```

---

# 26. Bind the Project When the Session Is Created

The Project for a new writing session should be determined **when the new
session is created**, not later when auto-save happens.

Example:

```text
10:00
Default Project = Writing

10:01
NEW session created
→ session belongs to Writing

10:02
User changes Default Project to Research

10:03
Current unsaved session is saved
```

The current session must still save to:

```text
Writing
```

because that was its Project when the session was created.

Changing the Default Project affects **future new sessions**, not the current
one.

---

# 27. New File Path

When a session assigned to:

```text
Writing
```

is saved for the first time, its Markdown file must be created under:

```text
Inklings/Writing/08 Dailies/01 Inbox/
```

using the existing filename convention.

Example:

```text
Inklings/
└── Writing/
    └── 08 Dailies/
        └── 01 Inbox/
            └── DA-2026-08-22-SAT-10_15_30.md
```

Do not change the existing DA filename convention.

---

# 28. BAS Log Path

A BAS log belonging to a writing session must be created inside the same
Project.

For example:

```text
Markdown:
Inklings/Writing/08 Dailies/01 Inbox/
    DA-....md
```

Associated BAS:

```text
Inklings/Writing/99 Operations/99 Log/YYYY/MM/
    BAS-....md
```

Do not place the Markdown file in one Project and its BAS file in another.

---

# 29. Preserve Timestamp Association

The existing DA/BAS timestamp relationship must remain unchanged.

If the existing implementation associates:

```text
DA-<timestamp>.md
BAS-<timestamp>.md
```

preserve that exact convention.

Do not create a different timestamp merely because Projects now exist.

---

# 30. Changing Default Does Not Move Current Document

If:

```text
Current document → Writing
```

and the user changes:

```text
Default Project → Research
```

the current document remains in:

```text
Writing
```

It must not move.

Its Font Color remains Writing's Font Color.

Only future new sessions use Research.

---

# 31. Changing Default Does Not Move Existing Files

Changing the Default Project must not move any existing:

* Markdown files
* BAS files
* Directories

Default Project determines only the destination of future writing sessions.

---

# 32. Project Management Does Not Move Files

Requirement 17B must not include:

```text
Move current file to Project
```

functionality.

That belongs entirely to Requirement 17C.

Do not attempt partial file movement in 17B.

---

# 33. Refresh Projects When Panel Opens

Because Projects may be externally added/deleted with Android Files, opening
Project management should perform a filesystem refresh.

For example:

```text
User deletes Research using Android Files
        ↓
Returns to app
        ↓
Opens Project panel
        ↓
Research should disappear
```

The user should not necessarily have to restart the entire application.

Reuse the Project discovery/reconciliation logic implemented in 17A.

---

# 34. External Project Added While App Is Running

If the user adds/copies:

```text
Inklings/Imported/
```

outside the application and then opens Project management:

* Rescan Projects.
* Discover Imported if valid.
* Initialize missing metadata if needed.
* Display Imported.

No continuous filesystem watcher is required.

---

# 35. External Current Project Deletion

If the filesystem Project associated with the currently open document/session
has been externally deleted while the application is running, do not blindly
recreate the Project or save into another Project.

Handle this safely.

At minimum:

* Detect that the expected Project directory no longer exists before saving.
* Do not silently redirect an existing document to Default.
* Do not lose the editor's current text.

The exact recovery UI can remain minimal in Requirement 17B.

Do not silently destroy or redirect data.

---

# 36. Project Management Panel Close Behavior

The Project-management panel should close when appropriate, such as:

* User taps outside it
* User completes Project creation
* User completes a Project metadata change
* User explicitly closes it

Do not leave it permanently covering the writing area.

---

# 37. Interaction With Typing

Opening Project management should not alter document content.

When the user returns to writing:

* Cursor behavior continues normally.
* Fade behavior resumes normally.
* Typewriter sounds continue normally.
* Timer continues normally.

Do not reset the writing session merely because Project management was
opened.

---

# 38. Timer

Requirement 15 remains unchanged.

Opening Project management or changing Project metadata must not:

* Reset timer
* Pause timer
* Restart timer

The timer remains independent.

---

# 39. Typewriter Sounds

Requirement 14 remains unchanged.

Project UI interactions themselves should not produce typewriter key sounds.

Typing after closing Project management should behave normally.

---

# 40. Settings

The existing Settings panel remains unchanged.

Do not place Project configuration inside the existing Typewriter
Sounds/Timer Settings panel.

Project management has its own dedicated Project interface.

---

# 41. Requirement 16

Requirement 16 remains fully applicable.

For a fresh writing session assigned to a Project:

```text
Whitespace only + never saved
        ↓
No Markdown file
No BAS file
```

The mere fact that the session has a Project association must not cause an
empty file to be created.

For a previously saved document, intentional clearing remains supported.

---

# 42. Full-Screen Mode

Requirement 12 remains unchanged.

The Project-management UI must work correctly while the application is in
immersive full-screen mode.

Do not exit full-screen mode merely because Project management is opened.

---

# 43. Keyboard Shortcuts

Requirement 13 remains unchanged:

```text
Ctrl+S → Save
Ctrl+N → New
Ctrl+Q → Close
```

Important:

```text
Ctrl+N
```

must create the new session in the Default Project using the same logic as
the NEW icon.

No Project-management keyboard shortcut is required.

---

# 44. Light Theme

Project UI must work in light theme.

Verify:

* Project names readable
* Default indicator visible
* Font Color swatches visible
* Add Project control visible
* Project icon visible
* Dialog/panel readable

---

# 45. Dark Theme

Project UI must work in dark theme.

Do not hard-code colors that work only in light theme.

Font Color swatches should still accurately represent the selected Project
colors.

---

# 46. Small and Large Screens

The Project-management UI must work on:

* Nokia T20
* Pixel 7a
* Boox Go 10.3
* Similar Android devices

Use responsive Compose layout.

Do not hard-code screen pixel coordinates.

---

# 47. No Project Rename

Do NOT provide:

```text
Rename Project
Edit Project Name
```

for an existing Project.

Project Name is entered only when creating a Project.

---

# 48. No In-App Project Delete

Do NOT add a Delete Project button.

For now, Project deletion remains filesystem-driven:

```text
Android Files
    ↓
Delete Inklings/<Project>/
    ↓
Project disappears after refresh
```

This behavior is already established by Requirement 17A.

---

# 49. No File Move

Do NOT implement moving an existing document or BAS file between Projects.

That belongs to Requirement 17C.

---

# Testing

## Test 1 — Open Project Panel

Tap the Project action button.

Expected:

* Project-management panel opens.
* Existing Projects are listed.
* `default` appears.
* Default Project is indicated.

---

## Test 2 — Create Project

Create:

```text
Writing
```

Choose a Font Color.

Leave:

```text
Default Project = OFF
```

Expected:

```text
Inklings/Writing/
├── .inklings-project.json
├── 08 Dailies/
│   └── 01 Inbox/
└── 99 Operations/
    └── 99 Log/
```

exists.

---

## Test 3 — Metadata

Inspect:

```text
Inklings/Writing/.inklings-project.json
```

Verify the selected Font Color was stored.

Verify:

```text
isDefault = false
```

---

## Test 4 — New Default Project

Set:

```text
Writing = Default
```

Expected:

```text
Writing → isDefault = true
default → isDefault = false
```

Verify the metadata files reflect this.

---

## Test 5 — New Session Destination

With:

```text
Writing = Default
```

create a NEW writing session.

Type meaningful content.

Save.

Expected Markdown path:

```text
Inklings/Writing/08 Dailies/01 Inbox/
```

---

## Test 6 — BAS Destination

Write long enough to create a BAS log.

End the session.

Expected:

```text
Markdown → Inklings/Writing/08 Dailies/01 Inbox/
BAS      → Inklings/Writing/99 Operations/99 Log/YYYY/MM/
```

Both belong to Writing.

---

## Test 7 — Change Default During Current Session

Set:

```text
Writing = Default
```

Create a new writing session.

Before saving it, change:

```text
Default Project = Research
```

Then Save the current session.

Expected:

Current file still saves to:

```text
Writing
```

A subsequently created NEW session should use:

```text
Research
```

---

## Test 8 — Project Font Color

Set:

```text
Writing = Blue
Research = Green
```

Create a Writing session.

Verify editor text is blue.

Create a later Research session after making Research Default.

Verify editor text is green.

---

## Test 9 — Change Current Project Font Color

While writing in Writing:

Change Writing's Font Color.

Expected:

* Editor text color changes immediately.
* Markdown contents do not change.

---

## Test 10 — No Markdown Color Metadata

Open the saved `.md` externally.

Verify no:

* HTML color tags
* YAML color metadata
* Application-specific color markup

was added.

---

## Test 11 — External Deletion Refresh

Using Android Files, delete:

```text
Inklings/Research/
```

Return to the app.

Open Project management.

Expected:

```text
Research
```

no longer appears.

It must not be recreated.

---

## Test 12 — External Addition Refresh

Using Android Files, add/copy a valid:

```text
Inklings/Imported/
```

Project.

Open Project management.

Expected:

* Imported appears.
* Missing metadata is initialized if necessary.

---

## Test 13 — Duplicate Project

Attempt to create another:

```text
Writing
```

Expected:

* Creation rejected.
* Existing Writing Project untouched.

---

## Test 14 — Empty Name

Attempt to create:

```text
""
```

or whitespace-only name.

Expected:

* Creation rejected.
* No folder created.

---

## Test 15 — Requirement 16

Create a new session in the Default Project.

Enter only spaces.

Press Save.

Expected:

* No Markdown file created.
* No BAS file created.

---

## Test 16 — Ctrl+N

Use:

```text
Ctrl+N
```

Verify the new session is assigned to the current Default Project.

---

## Test 17 — Existing Features

Verify:

* Save
* New
* Close
* Auto-save
* BAS logs
* Fade
* Timer
* Typewriter sounds
* Settings
* Full-screen
* Ctrl+S
* Ctrl+N
* Ctrl+Q

continue to work normally.

---

# Completion Criteria

Requirement 17B is complete when:

1. A Project-management action button exists.
2. Project management lists Projects discovered from `Inklings/`.
3. Opening Project management refreshes filesystem Project discovery.
4. Project Name is displayed.
5. Font Color is displayed visually.
6. Default Project is clearly indicated.
7. Current session's Project can be identified.
8. New Projects can be created.
9. Project Name is entered at creation only.
10. Existing Projects cannot be renamed.
11. Duplicate Project creation is prevented.
12. Invalid Project names are rejected.
13. Font Color is selected visually.
14. Font Color is stored in `.inklings-project.json`.
15. Project Font Color affects editor text.
16. Font Color changes do not modify Markdown.
17. Current Project color updates immediately when changed.
18. Any Project can be made Default.
19. Exactly one effective Default Project exists.
20. Making a new Project Default removes Default status from the old one.
21. The Project named `default` does not have to remain active Default.
22. New writing sessions bind to the Default Project when the session is
    created.
23. Changing Default later does not redirect the current session.
24. New Markdown files are saved in the session's assigned Project.
25. BAS logs are created in the same Project as their Markdown file.
26. Existing DA/BAS timestamp behavior is unchanged.
27. Externally deleted Projects disappear when Project management refreshes.
28. Externally added valid Projects can appear after refresh.
29. No in-app Project deletion is implemented.
30. No Project rename is implemented.
31. No file movement between Projects is implemented.
32. Requirement 16 remains intact.
33. Existing timer, sound, fade, keyboard and save behavior remains intact.
34. Project UI works in light and dark themes.
35. Project UI works on phone and tablet-sized displays.
36. No unrelated functionality is changed.

---

# Required Source-Code Comments

Add concise comments explaining:

1. Why Project management rescans `Inklings/` when opened.
2. Why Project selection does not move the current document.
3. Why Project Name is immutable after creation.
4. Why Font Color is stored only in `.inklings-project.json`.
5. Why Font Color affects presentation but never Markdown content.
6. How the single Default Project rule is applied.
7. Why the Project is bound when a writing session is created.
8. Why changing Default does not redirect an already-created session.
9. Why Markdown and BAS files must remain within the same Project.
10. Why file movement is deliberately deferred to Requirement 17C.

---

# After Implementation

Provide a short summary explaining:

1. Which files were changed.
2. Where the Project-management button was added.
3. How the Project list is displayed.
4. How filesystem refresh works when Project management opens.
5. How Project creation works.
6. How Project Name validation works.
7. How the Font Color picker works.
8. How Project Font Color is applied to the editor.
9. How Default Project selection works.
10. How a new writing session is bound to its Project.
11. How Markdown/BAS paths are determined.
12. How external Project additions/deletions are reflected.
13. Confirmation that Project rename, in-app deletion and file movement were
    not implemented.
14. Confirmation that existing application functionality remains unchanged.

Stop after implementing Requirement 17B.
