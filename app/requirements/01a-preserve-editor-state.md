# Requirement 01A — Preserve Editor State Across Configuration Changes

## Objective

Fix the current writing screen so that text entered into the editor is not
lost when the Android Activity is recreated.

This is a corrective requirement for Requirement 01.

Do not implement any new application functionality.

## Problem

Currently, when the device is rotated, the text entered into the editor
disappears.

Android may recreate the Activity when the device configuration changes,
including screen orientation.

The editor contents must survive this recreation.

## Required Behavior

If the user enters text such as:

Hello world

This is some test text.

and then rotates the device:

- The text must still be present.
- The text must be unchanged.
- The cursor position should be preserved if reasonably possible.
- The editor should remain usable.
- Courier Prime must continue to be used.

The same behavior should work when rotating back.

## Implementation

Use the standard Android/Jetpack Compose state-saving mechanisms appropriate
for this situation.

Prefer the simplest correct solution.

Do not introduce a database, file storage, or third-party library just to
solve this problem.

Do not implement permanent Markdown file saving yet.

This requirement is only about preserving the current editor state when the
Activity is recreated.

## Do NOT Implement

Do not implement:

- File saving
- Autosave
- Templates
- Timestamp filenames
- Session timing
- Typewriter cursor
- Hidden lines
- New session functionality
- Close functionality
- Configurable directories

These belong to later requirements.

## Testing

Test on the Nokia T20.

1. Launch the application.
2. Enter several paragraphs of text.
3. Rotate from portrait to landscape.
4. Verify that all text remains.
5. Rotate back to portrait.
6. Verify that all text remains.
7. Verify that Courier Prime is still being used.
8. Verify that normal editing still works.

## After Implementation

Explain:

1. Why the text was previously disappearing.
2. What state-saving mechanism was used.
3. Which files were changed.
4. Why the chosen solution is appropriate.
5. Whether any dependencies were added.

Do not implement Requirement 03.

Stop after completing this correction.