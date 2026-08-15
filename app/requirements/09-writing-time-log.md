# Requirement 09 — Writing Time Log

## Objective

Track the amount of active writing time during each writing session and,
when the session ends, create a separate Markdown log file containing the
total writing time.

A writing session ends when the user presses:

- NEW
- CLOSE

The existing main Markdown file continues to be saved as before.

This requirement adds a second Markdown file containing the writing-time
information.

---

# 1. Writing Time

Writing time means time spent actively writing/editing the document.

The timer must NOT start when the application opens.

The timer starts when the user first enters text into the editor.

The timer tracks active writing activity rather than total application-open
time.

---

# 2. Activity Detection

Use text-editing activity as the indication that the user is actively
writing.

When the document changes because the user types or edits text:

- Record the current activity time.
- Update the last-activity timestamp.
- Consider the user to be actively writing.

The implementation should use the editor's existing text-change mechanism
rather than creating a separate keyboard-monitoring system.

Do not track:

- Screen touches by themselves
- Scrolling
- Cursor movement
- Selection
- Screen rotation
- Application focus
- Time while the application is simply open

Only actual document editing activity should count as writing activity.

---

# 3. First Writing Activity

The first document edit starts the writing-time tracking.

Example:

    Application opened at 09:00
    First character typed at 09:17

The 17 minutes before the first character was typed must NOT be counted.

The writing session's active-time tracking begins at approximately:

    09:17

---

# 4. Active Writing

While the user continues editing the document, writing time accumulates.

For example:

    09:00 first character
    09:05 continues typing
    09:08 continues typing

Approximately 8 minutes of active writing time has accumulated.

The exact implementation should account for the intervals between document
changes rather than relying on the application being open.

---

# 5. Idle Period

An idle period is defined as:

    More than 10 minutes without document editing activity.

If the user stops editing for more than 10 minutes, the idle period must NOT
be counted as writing time.

The writing activity period is considered ended.

---

# 6. Resuming After Idle

If the user starts editing again after a long idle period, writing-time
tracking resumes.

The new editing activity starts a new active writing period.

Example:

    09:00 — start typing
    09:05 — stop typing
    09:15 — still idle
    10:00 — start typing again
    10:10 — continue typing

The time between 09:05 and 10:00 must NOT be counted.

The active periods are approximately:

    09:00–09:05
    10:00–10:10

Total:

    15 minutes

---

# 7. Exactly 10 Minutes

An idle gap of 10 minutes or less remains part of the current active
writing period.

An idle gap greater than 10 minutes ends the active writing period.

Use:

    IDLE_TIMEOUT = 10 minutes

Do not make the threshold configurable in this requirement.

---

# 8. Important Idle-Time Rule

Do NOT count the first 10 minutes of a long idle period.

For example:

    Type for 5 minutes
    Stop typing for 60 minutes
    Type again for 5 minutes

The result must be approximately:

    10 minutes

NOT:

    70 minutes

and NOT:

    20 minutes

The long idle period contributes:

    0 minutes

---

# 9. Session End

Writing time is finalized when the current session ends.

A session ends when:

- NEW is pressed
- CLOSE is pressed

Before ending the session:

1. Save the current main Markdown document.
2. Finalize the writing-time calculation.
3. Create the writing-time log file if appropriate.
4. End the current writing session.

---

# 10. Writing Time Calculation

Maintain the accumulated active writing time internally during the session.

The implementation should conceptually maintain:

    totalWritingTime
    lastActivityTime
    activeWritingPeriod

The implementation may use a different internal design if it produces the
same behavior.

Do NOT create a continuously running timer that simply measures how long the
application is open.

---

# 11. Final Active Period

When NEW or CLOSE is pressed, account for the final active writing period.

If the user has been actively editing and then immediately presses CLOSE,
the time since the most recent editing activity should be included as part
of the active writing period.

However, if the last editing activity occurred more than 10 minutes before
NEW or CLOSE, that idle period must not be counted.

---

# 12. Minimum Writing Time

If the total active writing time is less than one minute:

    dailying:: 0

must NOT be created as a meaningful writing-time entry.

Instead, do not create the BAS log file at all.

A session with less than one minute of active writing time produces:

- The normal main Markdown file.
- No BAS time-log file.

---

# 13. Rounding

The logged writing time must be expressed in complete minutes.

Round DOWN to the nearest whole minute.

Examples:

    0:45  → no BAS file
    0:59  → no BAS file
    1:00  → dailying:: 1
    1:30  → dailying:: 1
    1:59  → dailying:: 1
    2:00  → dailying:: 2
    2:59  → dailying:: 2
    10:45 → dailying:: 10

Do not round to the nearest minute.

---

# 14. BAS Log File

When the writing session contains at least one complete minute of active
writing time, create a separate Markdown file.

The file must contain exactly one entry:

    dailying:: X

where:

    X = total active writing time in complete minutes

Example:

    dailying:: 2

There must be no additional text.

Do not add:

- Headers
- Blank explanatory text
- Metadata
- Session information
- Filenames
- Comments

---

# 15. BAS File Name

The log filename must follow this format:

    BAS-YYYY-MM-DD - HH-MM-SS.md

Example:

    BAS-2026-01-27 - 07-44-35.md

Use:

- `BAS-` prefix
- Four-digit year
- Two-digit month
- Two-digit day
- Space
- Hyphen
- Space
- Two-digit hour
- Hyphen
- Two-digit minute
- Hyphen
- Two-digit second
- `.md` extension

Do NOT use colons in the filename.

Use the device's local date and time.

---

# 16. BAS File Timestamp

The BAS filename timestamp represents the time at which the session is
ended and the BAS log is created.

For example, if the user presses CLOSE at:

    2026-08-15 14:46:35

the resulting file should be:

    BAS-2026-08-15 - 14-46-35.md

The timestamp does NOT represent the beginning of the writing session.

---

# 17. BAS Directory Structure

The BAS file must be stored separately from the main writing files.

Use:

    99 Operations/99 Log/YYYY/MM/

For example:

    99 Operations/
    └── 99 Log/
        └── 2026/
            └── 01/
                └── BAS-2026-01-27 - 07-44-35.md

For August 2026:

    99 Operations/
    └── 99 Log/
        └── 2026/
            └── 08/
                └── BAS-2026-08-15 - 14-46-35.md

---

# 18. Main Folder

The BAS directory is relative to the same main folder used by the
application.

For the current development configuration, the main folder is:

    Documents/Inklings/

Therefore the complete development path is:

    Documents/Inklings/99 Operations/99 Log/YYYY/MM/

Example:

    Documents/
    └── Inklings/
        └── 99 Operations/
            └── 99 Log/
                └── 2026/
                    └── 08/
                        └── BAS-2026-08-15 - 14-46-35.md

The main folder will become configurable in a later requirement.

Do NOT implement configurable storage in this requirement.

---

# 19. Directory Creation

If the required BAS directories do not exist, create them.

For example:

    99 Operations/
    99 Operations/99 Log/
    99 Operations/99 Log/2026/
    99 Operations/99 Log/2026/08/

The application must not fail simply because the directories do not already
exist.

Use the same Android storage mechanism established in Requirement 06.

---

# 20. NEW Integration

When NEW is pressed:

1. Save the current main Markdown document.
2. Calculate the final writing time.
3. If writing time is at least one minute, create the BAS log file.
4. End the current session.
5. Start a new writing session.
6. Reset all writing-time tracking state.
7. The new session begins with zero writing time.
8. The new session does not inherit any timing information from the
   previous session.

The previous session's BAS file must remain unchanged.

---

# 21. CLOSE Integration

When CLOSE is pressed:

1. Save the current main Markdown document.
2. Calculate the final writing time.
3. If writing time is at least one minute, create the BAS log file.
4. End the current session.
5. Close the application.

The BAS file must be created before the application closes.

---

# 22. Save Failure

If saving the main Markdown file fails:

- Do NOT create the BAS log file.
- Do NOT end the session.
- Do NOT start a new session.
- Do NOT close the application.
- Show a clear error message.
- Keep the document and timing state intact.
- Allow the user to try again.

The BAS log must only be created after the main Markdown file has been
successfully saved.

---

# 23. BAS File Failure

If the main Markdown file saves successfully but the BAS log cannot be
created:

- Do NOT lose the main Markdown document.
- Show a clear error message.
- Do NOT pretend that the BAS log was successfully created.

The implementation should keep the application/session state safe.

Do not delete or modify the main Markdown file because the BAS file failed.

---

# 24. Existing Functionality

Do not change existing editor behavior.

The following must continue to work:

- Courier Prime
- 22sp editor text
- Typewriter cursor
- Typewriter scrolling
- Distraction-free writing
- One visual line above the cursor
- Current visual line
- One visual line below the cursor
- SAVE
- NEW
- CLOSE
- Main Markdown file creation
- Main Markdown file saving
- Rotation/state preservation

---

# 25. Testing

The Nokia T20 is the primary development and testing device.

## Test 1 — Basic Timing

1. Start a new session.
2. Wait several minutes before typing.
3. Type for approximately 2 minutes.
4. Press CLOSE.

Expected:

The waiting time before typing is NOT counted.

A BAS file is created containing approximately:

    dailying:: 2

---

## Test 2 — Less Than One Minute

1. Start a session.
2. Type for less than one minute.
3. Press CLOSE.

Expected:

The main Markdown file is created.

No BAS file is created.

---

## Test 3 — Multiple Active Periods

1. Type for approximately 2 minutes.
2. Stop typing.
3. Resume typing within 10 minutes.
4. Type for approximately 2 more minutes.
5. Press CLOSE.

Expected:

The active periods are accumulated.

The BAS file should contain approximately:

    dailying:: 4

---

## Test 4 — Long Idle Period

1. Type for approximately 2 minutes.
2. Stop typing for more than 10 minutes.
3. Type for approximately 2 more minutes.
4. Press CLOSE.

Expected:

The long idle period is NOT counted.

The BAS file should contain approximately:

    dailying:: 4

NOT:

    dailying:: 14

---

## Test 5 — NEW

1. Type for approximately 2 minutes.
2. Press NEW.
3. Type for approximately 3 minutes.
4. Press CLOSE.

Expected:

Two separate BAS files are created.

First:

    dailying:: 2

Second:

    dailying:: 3

The timing information from the first session must not carry over into the
second session.

---

## Test 6 — BAS Directory

Verify that the BAS file is stored under:

    Documents/Inklings/99 Operations/99 Log/YYYY/MM/

---

## Test 7 — BAS Filename

Verify that the filename follows:

    BAS-YYYY-MM-DD - HH-MM-SS.md

---

## Test 8 — BAS Contents

Open the BAS file externally.

Expected:

Exactly one line:

    dailying:: X

No other content should be present.

---

## Test 9 — Main File and BAS File

After completing a writing session, verify that both files exist:

Main document:

    Documents/Inklings/08 Dailies/01 Inbox/DA-....md

Time log:

    Documents/Inklings/99 Operations/99 Log/YYYY/MM/BAS-....md

The two files must be independent.

---

## Test 10 — Rotation

1. Start writing.
2. Rotate the Nokia T20.
3. Continue writing.
4. Press CLOSE.

Expected:

Writing-time tracking continues correctly.

No timing information is lost because of rotation.

---

# 26. Do NOT Implement

Do NOT implement:

- Configurable idle timeout
- Configurable storage
- Configurable writing-time rules
- Statistics
- Daily totals
- Weekly totals
- Monthly totals
- Analytics
- Graphs
- Reports
- BAS file reading
- BAS file editing
- File opening
- Automatic two-minute saving
- Templates
- BOOX-specific font size

These belong to later requirements.

---

# Completion Criteria

Requirement 09 is complete when:

1. Writing time starts with the first document edit.
2. Application-open time is not counted.
3. Only active writing periods are counted.
4. Idle periods greater than 10 minutes are excluded.
5. Writing resumes correctly after a long idle period.
6. Less than one minute produces no BAS file.
7. Writing time is rounded down to complete minutes.
8. NEW creates a BAS file for the completed session when appropriate.
9. CLOSE creates a BAS file for the completed session when appropriate.
10. BAS files contain exactly one `dailying:: X` entry.
11. BAS files use the required filename format.
12. BAS files are stored under `99 Operations/99 Log/YYYY/MM/`.
13. BAS directories are created automatically when necessary.
14. Main Markdown saving remains independent and safe.
15. Timing state resets correctly for every new session.
16. Rotation does not reset or corrupt timing.
17. The implementation works on the Nokia T20.

---

# After Implementation

Provide a short summary explaining:

1. How writing activity is detected.
2. How active writing time is accumulated.
3. How the 10-minute idle rule is implemented.
4. How the final writing time is calculated.
5. How the less-than-one-minute case is handled.
6. How the BAS filename is generated.
7. Where BAS files are stored.
8. How NEW integrates with timing.
9. How CLOSE integrates with timing.
10. How timing state is preserved across rotation.
11. Which files were changed.
12. Any Android-specific considerations.

Do not implement Requirement 10.

Stop after completing this requirement.