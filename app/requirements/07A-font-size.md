# Requirement 07A — Increase Editor Font Size

## Objective

Increase the editor text size slightly to improve readability.

## Font

Continue using:

    Courier Prime

Do not change the font.

## Font Size

Set the editor font size to:

    22sp

This applies to the actual editable document text.

## Consistency

The font size must be consistent throughout the editor.

Do not change font size based on:

- Cursor position
- Scrolling
- Browsing mode
- Distraction-free mode
- Text length

## Existing Behavior

Do not change any other editor behavior.

The following must remain unchanged:

- Courier Prime
- Typewriter cursor
- Typewriter scrolling
- Browsing mode
- Distraction-free writing window
- One visual line above the cursor
- Current visual line
- One visual line below the cursor
- Text hiding behavior
- SAVE
- NEW
- CLOSE
- Markdown file handling

## Testing

Test on the Nokia T20.

Verify:

1. Text is visibly larger than before.
2. Text remains comfortable to read.
3. Courier Prime remains the font.
4. Typewriter positioning still works correctly.
5. The one-line-above / one-line-below behavior still works correctly.
6. Wrapped lines are calculated correctly at the new font size.
7. SAVE still saves the complete document.
8. No text is lost.

## Completion Criteria

Requirement 07A is complete when:

1. The editor uses Courier Prime.
2. The editor text size is 22sp.
3. The text is comfortably readable on the Nokia T20.
4. Existing editor behavior is unchanged.
5. The application builds and runs successfully.

## After Implementation

Explain:

1. Where the font size is defined.
2. Confirm that it is 22sp.
3. Which files were changed.
4. Confirm that no other functionality was changed.

Do not implement any additional functionality.

Stop after completing this requirement.