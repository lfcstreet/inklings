# Requirement 18 — Timer Start/Completion Sound, Optional Document Rename and Word Count Logging

## Objective

Add three independent enhancements:

1. Play `pop.mp3` three times when a fresh countdown timer starts, and three times again when the timer naturally reaches zero.
2. Allow the user to **optionally rename** the currently saved Markdown document by explicitly invoking a Rename action.
3. Add the current document word count to its BAS/log file as:

```text
words:: 345
```

Do not otherwise change existing timer, Project, save, logging, filename, or editor behavior.

The existing timer-completion visual behavior must remain exactly as currently implemented:

> When the timer completes, the writing text becomes red for a short period.

Do **not** reintroduce text flashing or blinking.

---

# PART A — TIMER START / COMPLETION SOUND

## 1. Audio File

Use the supplied file:

```text
pop.mp3
```

Use the actual file supplied with the project.

Do not generate, download, or substitute another sound.

---

## 2. Fresh Timer Start Sound

When the user starts a **fresh countdown** from the ready/stopped state:

```text
Play pop.mp3 exactly 3 times
```

Conceptually:

```text
Timer = 30:00
State = stopped
     ↓
User presses Play
     ↓
POP
POP
POP
     ↓
Timer continues running normally
```

The timer itself should begin immediately.

Do not delay countdown start while waiting for the sounds to finish.

---

## 3. Exactly Three Plays at Start

The start sound must play exactly three times.

Do not:

* Play it once
* Play it twice
* Play it more than three times
* Loop indefinitely
* Start all three copies simultaneously

The intended result should sound like three distinct sequential pops.

---

## 4. Resume Must NOT Play the Start Sound

There is an important distinction between:

```text
START
```

and:

```text
RESUME
```

If a running timer is paused and then resumed, do **not** play `pop.mp3`.

Example:

```text
30:00 stopped
     ↓ Play
POP × 3
     ↓
Timer running
     ↓
Pause at 24:30
     ↓
Play / Resume
     ↓
NO POP
```

Only a fresh countdown start triggers the three start sounds.

---

## 5. Timer Completion Sound

When the timer naturally reaches:

```text
00:00
```

play:

```text
pop.mp3
```

exactly three times.

Conceptually:

```text
Timer reaches 00:00
       ↓
POP
POP
POP
```

The three plays should be sequential and clearly distinguishable.

---

## 6. Timer Completion Visual Behavior

Do NOT modify the current timer-completion visual behavior.

The writing text currently becomes red for a short period when the timer ends.

Keep that behavior exactly as implemented.

Do NOT add or restore:

* Text flashing
* Text blinking
* Three text flashes
* Whole-screen flashing

Requirement 18 changes timer audio only.

---

## 7. Start and Completion Are the Only Pop Events

`pop.mp3` should play only for:

```text
Fresh timer start → 3 plays

Natural timer completion → 3 plays
```

Do NOT play `pop.mp3` when:

* Timer is paused
* Timer is resumed
* Timer is manually reset
* Timer is long-press reset
* Timer duration changes
* Settings are opened
* Timer control merely appears
* Timer is dismissed/hidden

---

## 8. Non-Blocking Playback

Timer audio must not block:

* Timer countdown
* Text editing
* UI rendering
* Existing red-text completion indication
* Other application behavior

Use appropriate asynchronous/non-blocking Android audio playback.

---

## 9. Playback Timing

The three pops should not overlap so heavily that they sound like a single distorted sound.

Use appropriate sequential playback based on the supplied clip duration or a short suitable interval.

Do not build a delayed queue that continues long after the event.

---

## 10. Typewriter Sounds Are Independent

The timer pop sound is separate from Requirement 14's:

```text
typewriterKS1.wav
typewriterKS2.wav
typewriterSPACE.wav
```

The existing:

```text
Typewriter Sounds ON/OFF
```

setting controls typing sounds only.

It must NOT disable `pop.mp3`.

Do not add another timer-sound setting in Requirement 18.

---

# PART B — OPTIONAL DOCUMENT RENAME

## 11. Rename Is Completely Optional

Document titles are optional.

The normal/default filename behavior must remain exactly as it currently works.

For example:

```text
DA-2026-08-24-MON-11_30_45.md
```

must continue to be the normal filename.

The application must NOT automatically ask the user for a title.

Do NOT request a title:

* When creating a new session
* During Save
* During Auto-save
* During Close
* During New
* When the first text is entered
* When the file is first created
* When opening a Project
* When moving between Projects

Do NOT automatically derive a title from document text.

A title is added only when the user explicitly invokes Rename from the UI.

---

## 12. Untitled Is the Normal State

An untitled document is a fully valid document.

The normal/default format remains:

```text
<prefix>-<date>-<DAY>-<time>.md
```

Example:

```text
DA-2026-08-24-MON-11_30_45.md
```

If the user never invokes Rename, the file may remain in this format permanently.

---

## 13. Rename Action

Add a Rename action to an appropriate existing action interface.

Do not permanently clutter the writing screen.

The user must explicitly select Rename.

Rename should be available only when the current document:

* Has already been saved
* Has a real Markdown file on disk

---

## 14. Unsaved Documents

If the current session has never been saved:

* Rename should be hidden or disabled.
* Do not create a file merely to enable Rename.
* Do not trigger Auto-save.
* Do not trigger explicit Save.
* Do not ask for a title.

Requirement 16 remains authoritative for whether a fresh document exists on disk.

---

## 15. Rename Dialog

When the user explicitly invokes Rename, show a simple title-entry dialog.

Example:

```text
Rename document

Title

[ Tolkien________________ ]

             Cancel    Rename
```

The exact UI should follow the application's current visual style.

---

## 16. Filename Format After Rename

The existing filename format is:

```text
<prefix>-<date>-<DAY>-<time>.md
```

After explicit Rename:

```text
<prefix>-<date>-<DAY>-<title>-<time>.md
```

The title must be inserted:

> Immediately after the weekday and immediately before the time.

Example:

Before:

```text
DA-2026-08-24-MON-11_30_45.md
```

User enters:

```text
Tolkien
```

After:

```text
DA-2026-08-24-MON-Tolkien-11_30_45.md
```

---

## 17. Preserve Original Session Timestamp

Rename must NOT generate a new timestamp.

For:

```text
DA-2026-08-24-MON-11_30_45.md
```

renamed to:

```text
DA-2026-08-24-MON-Tolkien-11_30_45.md
```

the original date/time remains:

```text
2026-08-24
11_30_45
```

The timestamp of the Rename operation itself is irrelevant.

---

## 18. Preserve Current Project Prefix During Normal Rename

The current Project's document prefix must remain unchanged when Rename is performed.

For example, if Requirement 17D configures:

```text
documentPrefix = RN
```

then:

```text
RN-2026-08-24-MON-11_30_45.md
```

renamed to:

```text
Tolkien
```

becomes:

```text
RN-2026-08-24-MON-Tolkien-11_30_45.md
```

Do not change `RN` to `DA`.

---

## 19. BAS / Log File Must NOT Be Renamed During Rename

Normal Rename applies only to the main Markdown document.

Suppose:

```text
DA-2026-08-24-MON-11_30_45.md
BAS-2026-08-24-MON-11_30_45.md
```

After renaming the main document to `Tolkien`:

```text
DA-2026-08-24-MON-Tolkien-11_30_45.md
BAS-2026-08-24-MON-11_30_45.md
```

The BAS/log filename deliberately remains unchanged.

---

## 20. Document/Log Association After Rename

The Markdown document and BAS/log remain associated through the original session timestamp.

Example:

```text
DA-2026-08-24-MON-Tolkien-11_30_45.md

BAS-2026-08-24-MON-11_30_45.md
```

still represent the same session.

The optional title must not interfere with document/log association.

Any existing filename parsing/matching logic must support titled and untitled Markdown filenames.

Do NOT assume every document filename always has exactly:

```text
prefix-date-day-time
```

---

## 21. Both Filename Formats Must Be Supported

The application must support both:

### Untitled/default

```text
<prefix>-<date>-<DAY>-<time>.md
```

### Explicitly renamed

```text
<prefix>-<date>-<DAY>-<title>-<time>.md
```

Do not bulk-rename old files.

---

## 22. Rename an Already-Titled File

The user must be able to change an existing title.

Example:

Current:

```text
DA-2026-08-24-MON-Tolkien-11_30_45.md
```

User explicitly invokes Rename and enters:

```text
Leaf by Niggle
```

Result:

```text
DA-2026-08-24-MON-Leaf by Niggle-11_30_45.md
```

Replace the previous title.

Do NOT append another title.

Incorrect:

```text
DA-2026-08-24-MON-Tolkien-Leaf by Niggle-11_30_45.md
```

---

## 23. Title Validation

Trim leading and trailing whitespace.

Reject:

* Empty title
* Whitespace-only title
* `/`
* `\`
* Path traversal
* Characters that would make the Android filename unsafe or invalid

The title must never be able to create another folder.

---

## 24. Spaces in Titles

Normal spaces inside titles are allowed.

Example:

```text
Leaf by Niggle
```

may produce:

```text
DA-2026-08-24-MON-Leaf by Niggle-11_30_45.md
```

Do not unnecessarily replace spaces with underscores.

---

## 25. Rename Conflict

Before Rename, calculate the complete proposed destination filename.

If the destination already exists:

* Do not overwrite it.
* Do not delete either file.
* Do not generate another timestamp.
* Abort Rename safely.
* Show a concise error indication.

---

## 26. Keep Editor Open

After successful Rename:

* Keep the current document open.
* Preserve document content.
* Preserve cursor position where practical.
* Preserve scroll position where practical.
* Preserve timer state.
* Preserve Project association.
* Update the active Markdown path immediately.

Do not force the user to reopen the document.

---

## 27. Save After Rename

After successful Rename, all future:

* Save
* Ctrl+S
* Auto-save
* Close-triggered save
* New-triggered save

must update the renamed file.

Do not recreate the original untitled filename.

---

## 28. Project Move — Untitled File

Requirement 17C/17D behavior remains unchanged for an untitled file.

Source:

```text
DA-2026-08-24-MON-11_30_45.md
```

Target Project:

```text
documentPrefix = RN
```

After Move:

```text
RN-2026-08-24-MON-11_30_45.md
```

No title is introduced automatically.

---

## 29. Project Move — Titled File

Project Move must preserve an existing title while adopting the target Project's configured prefix.

Source:

```text
DA-2026-08-24-MON-Tolkien-11_30_45.md
```

Target Project:

```text
documentPrefix = RN
```

After Move:

```text
RN-2026-08-24-MON-Tolkien-11_30_45.md
```

The move must:

* Adopt target Project document prefix.
* Preserve optional title.
* Preserve original session timestamp.

---

## 30. BAS During Project Move

The associated BAS/log file remains untitled.

Example source:

```text
DA-2026-08-24-MON-Tolkien-11_30_45.md
BAS-2026-08-24-MON-11_30_45.md
```

Target Project:

```text
documentPrefix = RN
logPrefix = RL
```

After Move:

```text
RN-2026-08-24-MON-Tolkien-11_30_45.md
RL-2026-08-24-MON-11_30_45.md
```

The shared timestamp remains the session association.

---

# PART C — WORD COUNT IN BAS LOG

## 31. Add Word Count

Add document word count to the existing BAS/log file using:

```text
words:: 345
```

This should be stored similarly to the existing:

```text
dailying:: 2
```

Example:

```text
dailying:: 12
words:: 345
```

---

## 32. Meaning of `words::`

`words::` represents the current word count of the associated Markdown document.

Example:

```text
dailying:: 14
words:: 826
```

means:

* Existing tracked writing time = 14 minutes
* Current document word count = 826

Do not alter the meaning of `dailying::`.

---

## 33. Word Counting Rule

Use a straightforward writing-app word count.

A word is generally:

> A non-empty sequence separated by whitespace.

Example:

```text
This is a simple test.
```

must produce:

```text
words:: 5
```

Do not introduce sophisticated linguistic tokenization.

---

## 34. Whitespace Handling

Multiple spaces, tabs and newlines must not inflate word count.

Example:

```text
This     is

a       test
```

must produce:

```text
words:: 4
```

Leading/trailing whitespace must not count.

---

## 35. Empty Document

An empty document has:

```text
words:: 0
```

A previously saved document that has intentionally been cleared also has:

```text
words:: 0
```

However, Requirement 16 remains unchanged.

Do NOT create a Markdown or BAS file merely to record `words:: 0` for a fresh never-saved empty/whitespace-only session.

---

## 36. When Word Count Is Written

Whenever the existing BAS/log is created or updated through the application's normal logging lifecycle, write/update the current word count.

There should be exactly one:

```text
words:: <number>
```

entry.

Do not append a new line every time.

---

## 37. Update Existing Word Count

Suppose the BAS currently contains:

```text
dailying:: 5
words:: 200
```

The document later reaches 345 words.

The log should become:

```text
dailying:: 5
words:: 345
```

Not:

```text
dailying:: 5
words:: 200
words:: 345
```

There must be one authoritative `words::` entry.

---

## 38. `dailying::` Logic Must Not Change

Do not change:

* How `dailying::` is calculated
* When it is updated
* Its meaning
* Its units

Word count is an additional independent property only.

---

## 39. Rename Must Not Reset Log State

Renaming:

```text
DA-2026-08-24-MON-11_30_45.md
```

to:

```text
DA-2026-08-24-MON-Tolkien-11_30_45.md
```

must NOT:

* Reset `dailying::`
* Reset `words::`
* Create a new BAS file
* Rename the BAS file
* Create a second logical writing session

It remains the same session.

---

## 40. Project Move Must Not Reset Word Count

Moving a document/log pair to another Project must:

* Preserve session identity.
* Preserve `dailying::`.
* Preserve/update `words::`.
* Preserve timestamp.
* Apply target Project path/prefix behavior from Requirement 17D.

Do not reset word count because a Project changed.

---

## 41. Word Count Performance

Word counting must not introduce noticeable typing lag.

Do not perform unnecessarily expensive processing or filesystem writes on every keypress.

If appropriate, calculate/update word count during the existing Save/log-update lifecycle rather than continuously writing to the BAS file while typing.

Typing responsiveness remains the priority.

---

## 42. No Word Count UI

Do NOT add a live word-count display to:

* Writing screen
* Settings
* Project dialog
* Timer

Requirement 18 only requires:

```text
words:: <count>
```

inside the BAS/log file.

A live visible word count is outside scope.

---

# PART D — PRESERVE EXISTING BEHAVIOR

## 43. Requirement 16

Requirement 16 remains fully applicable.

In particular:

* Fresh whitespace-only sessions are not saved.
* Previously saved documents may intentionally be cleared.
* Rename never forces a fresh unsaved session to be saved.

---

## 44. Requirement 17D

Project-specific:

```text
documentSubfolder
logSubfolder
documentPrefix
logPrefix
```

remain authoritative.

Rename must work with custom Project prefixes.

Project Move must preserve optional title while adopting the target Project's prefixes.

---

## 45. Existing Timer Behavior

Do not change:

* Timer duration
* Play/Pause behavior
* Reset behavior
* Circular progress
* Existing red-text timer-completion indication

Only add the new `pop.mp3` behavior described here.

---

## 46. Existing Features

Do not otherwise modify:

* Save
* Auto-save
* New
* Close
* Existing BAS logging except adding `words::`
* Project creation
* Default Project
* Project Font Color
* Project Move UI
* Project paths
* Project prefixes
* Fade
* Typewriter sounds
* Full-screen mode
* Auto-capitalization
* Keyboard shortcuts
* Writing-time calculation

---

# TESTING

## Test 1 — Fresh Timer Start

Reset timer to its configured duration.

Tap Play.

Expected:

* Timer starts immediately.
* `pop.mp3` plays exactly 3 times.
* UI remains responsive.

---

## Test 2 — Pause / Resume

Start the timer.

Pause it.

Resume it.

Expected:

* Resume does NOT play `pop.mp3`.
* Timer resumes normally.

---

## Test 3 — Timer Completion

Allow timer to naturally reach:

```text
00:00
```

Expected:

* `pop.mp3` plays exactly 3 times.
* Existing red-text completion behavior occurs.
* Text does NOT flash or blink.

---

## Test 4 — Timer Reset

Start timer and long-press Reset.

Expected:

* No `pop.mp3`.
* Existing Reset behavior remains unchanged.

---

## Test 5 — Normal File Creation Without Rename

Create and save a normal document without invoking Rename.

Expected:

```text
<prefix>-<date>-<DAY>-<time>.md
```

There must be:

* No Rename prompt.
* No title.
* No automatically generated title.

---

## Test 6 — Auto-save Without Rename

Create a new writing session.

Let Auto-save save it.

Expected normal untitled filename.

No Rename UI appears.

---

## Test 7 — Explicit Rename

Start with:

```text
DA-2026-08-24-MON-11_30_45.md
```

Explicitly select Rename.

Enter:

```text
Tolkien
```

Expected:

```text
DA-2026-08-24-MON-Tolkien-11_30_45.md
```

---

## Test 8 — BAS Not Renamed

Before:

```text
DA-2026-08-24-MON-11_30_45.md
BAS-2026-08-24-MON-11_30_45.md
```

Rename document to Tolkien.

Expected:

```text
DA-2026-08-24-MON-Tolkien-11_30_45.md
BAS-2026-08-24-MON-11_30_45.md
```

---

## Test 9 — Rename Existing Title

Start:

```text
DA-2026-08-24-MON-Tolkien-11_30_45.md
```

Rename to:

```text
Leaf by Niggle
```

Expected:

```text
DA-2026-08-24-MON-Leaf by Niggle-11_30_45.md
```

---

## Test 10 — Unsaved Rename

Create a fresh session that has never been saved.

Expected:

* Rename unavailable/disabled.
* No file is created.
* No title is requested.

---

## Test 11 — Save After Rename

Rename a file.

Continue typing.

Press Save.

Expected:

* Renamed file updated.
* Old untitled filename not recreated.

---

## Test 12 — Auto-save After Rename

Rename a file.

Continue typing.

Wait for Auto-save.

Expected:

* Renamed file updated.
* Old filename not recreated.

---

## Test 13 — Rename Conflict

Create the proposed destination filename beforehand.

Attempt Rename.

Expected:

* Rename rejected.
* Destination untouched.
* Current source untouched.
* No overwrite.

---

## Test 14 — Move Untitled Document

Move:

```text
DA-2026-08-24-MON-11_30_45.md
```

to a Project configured with:

```text
documentPrefix = RN
```

Expected:

```text
RN-2026-08-24-MON-11_30_45.md
```

No title is added.

---

## Test 15 — Move Titled Document

Move:

```text
DA-2026-08-24-MON-Tolkien-11_30_45.md
```

to a Project configured with:

```text
documentPrefix = RN
logPrefix = RL
```

Expected:

```text
RN-2026-08-24-MON-Tolkien-11_30_45.md
```

Associated log:

```text
RL-2026-08-24-MON-11_30_45.md
```

Verify:

* Title preserved.
* Timestamp preserved.
* Target prefixes applied.

---

## Test 16 — Basic Word Count

Document:

```text
This is a simple test.
```

Expected BAS:

```text
words:: 5
```

---

## Test 17 — Whitespace Word Count

Document:

```text
This     is

a       test
```

Expected:

```text
words:: 4
```

---

## Test 18 — Word Count Update

Create a 100-word document.

After normal BAS creation/update:

```text
words:: 100
```

Add 50 words.

After next BAS update:

```text
words:: 150
```

Verify only one `words::` line exists.

---

## Test 19 — Rename Does Not Affect Word Count

Create a document/log with:

```text
words:: 345
```

Rename the main document.

Expected:

* Same BAS file.
* Same logical session.
* Word-count tracking continues normally.

---

## Test 20 — Previously Saved Empty Document

Save a document.

Delete all its contents.

Allow normal log update.

Expected:

```text
words:: 0
```

if a BAS legitimately exists.

---

## Test 21 — Fresh Empty Session

Create a fresh whitespace-only session.

Expected:

* No Markdown file.
* No BAS file merely for `words:: 0`.

Requirement 16 remains intact.

---

## Test 22 — Regression

Verify existing behavior still works:

* Save
* Auto-save
* New
* Close
* Ctrl+S
* Ctrl+N
* Ctrl+Q
* BAS logging
* `dailying::`
* Project creation
* Default Project
* Project Font Color
* Move here
* Custom Project paths
* Custom Project prefixes
* Fade
* Timer
* Typewriter sounds
* Full-screen mode
* Auto-capitalization

---

# COMPLETION CRITERIA

Requirement 18 is complete when:

1. `pop.mp3` plays exactly three times on a fresh timer start.
2. `pop.mp3` plays exactly three times when the timer naturally reaches zero.
3. Pause/Resume does not play the sound.
4. Reset does not play the sound.
5. Existing red-text completion behavior remains unchanged.
6. Text flashing/blinking is not reintroduced.
7. Timer audio does not block the UI.
8. Rename is completely optional.
9. Normal files continue using the existing untitled filename format.
10. No title is requested during normal New/Save/Auto-save/Close.
11. A title is requested only after explicit user invocation of Rename.
12. Only saved Markdown documents can be renamed.
13. Title is inserted after weekday and before time.
14. Original session timestamp is preserved.
15. Current Project prefix is preserved during normal Rename.
16. BAS/log filename is not renamed during normal Rename.
17. Titled Markdown files remain correctly associated with their BAS logs.
18. Both titled and untitled filenames are supported.
19. Existing titles can be replaced.
20. Invalid titles are rejected.
21. Rename conflicts never overwrite files.
22. Editor remains open after Rename.
23. Save/Auto-save uses the renamed file.
24. Project Move does not invent a title for untitled files.
25. Project Move preserves an existing title.
26. Project Move applies target Project prefixes.
27. BAS contains one current `words:: <count>` property.
28. Word count reflects the associated Markdown document.
29. Whitespace handling is correct.
30. Existing `dailying::` behavior remains unchanged.
31. Rename does not reset logging.
32. Project Move does not reset word count.
33. Fresh empty sessions still follow Requirement 16.
34. No visible live word-count UI is added.
35. No unrelated functionality is changed.

---

# REQUIRED SOURCE-CODE COMMENTS

Add concise comments explaining:

1. Why `pop.mp3` plays three times on fresh Start and three times on natural Completion.
2. Why Resume and Reset intentionally do not play the pop sound.
3. Why timer-completion audio is independent of Typewriter Sounds.
4. Why the existing red-text completion behavior must remain unchanged.
5. Why Rename is explicitly user-triggered and never part of normal Save/New.
6. How optional titles are inserted/replaced in filenames.
7. Why the original session timestamp is preserved.
8. Why the BAS filename remains untitled during normal Rename.
9. How titled Markdown files match BAS files through the session timestamp.
10. How Project Move preserves titles while changing Project prefixes.
11. How word count is calculated.
12. Why only one `words::` line is maintained.
13. Why word-count logging must not affect typing performance.

---

# AFTER IMPLEMENTATION

Provide a short summary explaining:

1. Which files were changed.
2. How `pop.mp3` is loaded.
3. How exactly three pops are played at fresh timer start.
4. How exactly three pops are played at natural timer completion.
5. How Resume is distinguished from a fresh Start.
6. Confirmation that existing red-text completion behavior was not changed.
7. Where the explicit Rename action was added.
8. Confirmation that Rename is never automatically triggered.
9. How title insertion/replacement is implemented.
10. How title validation/conflict handling works.
11. How BAS association survives Rename.
12. How titled and untitled documents behave during Project Move.
13. How word count is calculated.
14. When `words::` is created/updated.
15. Confirmation that `dailying::` behavior is unchanged.
16. Confirmation that Requirement 16 remains intact.
17. Confirmation that no unrelated functionality was changed.

Stop after implementing Requirement 18.
