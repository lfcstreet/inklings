# Requirement 04 — Distraction-Free Writing Window

## Objective

Implement the distraction-free writing behavior around the typewriter cursor.

The editor has two visual modes:

1. Normal/Browsing Mode
2. Distraction-Free Writing Mode

The underlying document must always remain complete and unchanged.

---

# 1. Normal / Browsing Mode

When the user scrolls through the document manually, the entire document
should be visible.

The user must be able to scroll upward and see previously written text.

For example:

    Line 1
    Line 2
    Line 3
    Line 4
    Line 5
    Line 6
    Line 7
    Line 8
    Line 9

The application must NOT hide lines merely because they are far away from
the cursor while the user is browsing.

This is important because the user must be able to navigate through the
document and find previously written text.

---

# 2. Entering Distraction-Free Writing Mode

When the user starts typing/editing text, the application should return to
the distraction-free writing view.

Typing includes:

- Inserting characters
- Pressing Enter
- Deleting characters
- Backspace
- Replacing selected text

The exact trigger should be based on actual text editing rather than merely
touching the screen.

---

# 3. Distraction-Free Writing Window

When distraction-free writing mode is active, only a small window of visual
lines around the cursor should be visible.

The target window is:

- Two visual lines above the cursor
- The current visual line
- Two visual lines below the cursor

Everything outside this window should NOT be rendered.

Example:

    [hidden]
    [hidden]
    [hidden]
    [hidden]

    line above cursor
    line above cursor

    CURRENT LINE |

    line below cursor
    line below cursor

    [hidden]
    [hidden]
    [hidden]

The exact number of visible lines may vary slightly because of screen size,
font metrics and line wrapping.

The important principle is:

> Keep a small writing window around the cursor and hide everything outside
> that window.

---

# 4. Hide BOTH Directions

This is different from the previous implementation.

Do NOT only hide text above the cursor.

Text below the cursor must also become invisible when it is outside the
writing window.

For example:

    [hidden]
    [hidden]

    visible line
    visible line
    CURRENT LINE |
    visible line
    visible line

    [hidden]
    [hidden]

The user should not see the entire future document while actively writing.

---

# 5. Browsing → Writing Transition

Example:

The document contains:

    Line 1
    Line 2
    Line 3
    Line 4
    Line 5
    Line 6
    Line 7
    Line 8
    Line 9
    Line 10

The user scrolls upward.

The entire document becomes visible.

The user can inspect:

    Line 1
    Line 2
    Line 3
    ...
    Line 10

The user then starts typing.

The application immediately returns to the distraction-free writing window
around the cursor.

---

# 6. Cursor Movement

Do NOT require the user to type into a previously hidden line in order to
reveal it.

This was a problem with the previous implementation.

If the user scrolls upward, the entire document becomes visible.

The user can then move the cursor normally.

Once the user starts editing/typing, the distraction-free writing window is
re-established around the cursor.

---

# 7. Scrolling

The user must be able to manually scroll through the entire document.

Manual scrolling should temporarily enter Browsing Mode.

While Browsing Mode is active:

- All document text is visible.
- The user can inspect earlier text.
- The user can navigate to different parts of the document.
- The application must not hide text based on cursor position.

Do not fight the user's scrolling gesture by immediately forcing the cursor
back to the typewriter position.

---

# 8. Typewriter Behavior

The typewriter behavior from Requirement 03 must remain active during
distraction-free writing.

When the user is actively writing:

- The cursor remains approximately centered vertically.
- The writing window moves with the cursor.
- Text outside the writing window is hidden.

The combination should feel like writing on a typewriter.

---

# 9. Visual Lines

The application must use VISUAL lines, not only newline-separated logical
lines.

For example:

    This is a very long paragraph that wraps
    onto another visual line and continues
    onto another visual line.

This represents three visual lines.

The writing window must be based on the actual rendered/wrapped lines.

It must therefore work with:

- Explicit newlines
- Automatic wrapping
- Long paragraphs
- Short paragraphs
- Empty lines
- Different screen widths

Courier Prime remains the editor font.

---

# 10. Underlying Document

The entire document must remain intact at all times.

Hiding text means:

    NOT RENDERED

It does NOT mean:

    DELETED
    REMOVED
    TRUNCATED
    CLEARED
    REPLACED

All text must remain in the editor's underlying state.

When file saving is implemented later, the complete document must be saved,
including all currently hidden text.

---

# 11. Selection

Normal text selection must continue to work.

If the user scrolls to an earlier part of the document:

- The entire document is visible.
- The user can select text normally.
- The user can edit selected text normally.

Once editing begins, the distraction-free writing window may be restored.

Correct editing behavior is more important than maintaining the visual hiding
rule during selection.

---

# 12. Keyboard

The Android keyboard must continue to work normally.

When actively writing:

- The cursor must remain visible.
- The typewriter behavior must continue.
- The writing window must remain visible.
- Text outside the writing window should remain hidden.

The available writing area must account for the keyboard.

---

# 13. Rotation

Existing state preservation must remain intact.

After rotating the Nokia T20:

- Text must remain intact.
- Courier Prime must remain active.
- The editor must remain usable.
- The typewriter behavior must continue.
- Browsing Mode must continue to work.
- Distraction-Free Writing Mode must continue to work.

---

# 14. Performance

The editor must remain responsive while typing.

Avoid unnecessarily rebuilding or rendering the entire document on every
keystroke if a simpler approach is possible.

However, correctness is more important than premature optimization.

---

# 15. Implementation

Prefer the existing Android/Jetpack Compose editor architecture.

Do not introduce a third-party text editor unless absolutely necessary.

Do not replace the entire editor architecture without first explaining why
the current implementation cannot support this behavior.

The distinction between Browsing Mode and Distraction-Free Writing Mode should
be explicit in the code.

For example, the implementation should have a clear concept equivalent to:

    browsingMode = true

or:

    distractionFreeMode = true

The exact implementation is up to the developer.

---

# 16. Do NOT Implement

Do not implement:

- Markdown file creation
- Timestamp filenames
- Templates
- Configurable directories
- Automatic saving
- Session timing
- Final SAVE behavior
- Final NEW behavior
- Final CLOSE behavior
- Settings
- File opening
- File browser

These belong to later requirements.

---

# 17. Testing

The Nokia T20 is the primary development and testing device.

## Test 1 — Normal Writing

Type several paragraphs.

Expected:

- Cursor remains approximately centered.
- Small writing window is visible.
- Text above and below the window is hidden.

## Test 2 — Scroll Up

While the document contains many lines, manually scroll upward.

Expected:

- The entire document becomes visible.
- Previously hidden text becomes visible.
- The application does not immediately hide it again.

## Test 3 — Browse

Scroll through the document.

Expected:

- The entire document remains visible.
- The user can inspect earlier and later text.

## Test 4 — Start Typing

While browsing, begin typing.

Expected:

- The application returns to distraction-free writing mode.
- The cursor becomes the center of the writing window.
- Text outside the writing window becomes invisible.
- Text both ABOVE and BELOW the writing window is hidden.

## Test 5 — Edit Earlier Text

Scroll upward and find an earlier paragraph.

Place the cursor there.

Do not require the application to hide/show text merely because the cursor
was moved.

When the user actually edits the text, return to distraction-free writing
mode around the edited location.

## Test 6 — Long Wrapped Paragraph

Create a paragraph spanning many visual lines.

Expected:

The writing window is based on visual lines rather than newline characters.

## Test 7 — Rotation

Rotate the device.

Expected:

The document remains intact and the current visual mode remains sensible.

---

# Completion Criteria

Requirement 04 is complete when:

1. The entire document is visible while manually browsing/scrolling.
2. Scrolling upward reveals previously hidden text.
3. The application does not fight manual scrolling.
4. Starting to edit returns to distraction-free writing mode.
5. The writing window follows the cursor.
6. Approximately two visual lines above the cursor remain visible.
7. The current visual line remains visible.
8. Approximately two visual lines below the cursor remain visible.
9. Text outside the writing window is invisible.
10. Text is hidden in BOTH directions.
11. Hidden text remains intact in the underlying document.
12. Wrapped visual lines are handled correctly.
13. Normal editing and selection continue to work.
14. Typewriter behavior continues to work.
15. Rotation does not lose text.
16. The application remains responsive.
17. The behavior works on the Nokia T20.

---

# After Implementation

Explain:

1. How Browsing Mode is detected.
2. How Distraction-Free Writing Mode is detected.
3. How the transition from browsing to writing is detected.
4. How the visible writing window is calculated.
5. How visual/wrapped lines are determined.
6. How text is hidden without modifying the underlying document.
7. How the behavior interacts with the typewriter scrolling.
8. Which files were changed.
9. Whether any dependencies were added.
10. Any limitations or edge cases discovered.

Do not implement Requirement 05.

Stop after completing this requirement.