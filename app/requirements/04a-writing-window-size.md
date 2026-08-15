# Requirement 04A — Reduce Distraction-Free Writing Window

## Objective

Reduce the visible writing window introduced in Requirement 04.

The current implementation displays approximately:

- Two visual lines above the cursor
- Current visual line
- Two visual lines below the cursor

Change this to:

- One visual line above the cursor
- Current visual line
- One visual line below the cursor

---

## Required Behavior

During Distraction-Free Writing Mode, the target visible window is:

    [hidden]
    [hidden]

    line immediately above cursor

    CURRENT LINE |

    line immediately below cursor

    [hidden]
    [hidden]

The exact visual result may vary slightly due to font metrics and line
wrapping.

The important rule is:

> Only one visual line immediately above and one visual line immediately
> below the current line should remain visible.

---

## Browsing Mode

Requirement 04 Browsing Mode remains unchanged.

When the user manually scrolls/browses:

- The entire document is visible.
- Previously hidden text becomes visible.
- The user can navigate through the entire document.

When the user starts editing, the application returns to the reduced
Distraction-Free Writing Mode.

---

## Typewriter Behavior

The typewriter behavior from Requirement 03 remains unchanged.

The cursor should remain approximately vertically centered within the writing
area.

The smaller writing window should move with the cursor.

---

## Visual Lines

Use visual/rendered lines rather than only newline-separated logical lines.

Wrapped text therefore counts as multiple visual lines.

---

## Underlying Document

Do not modify or delete hidden text.

Hidden text must remain fully present in the editor's underlying state.

---

## Do NOT Implement

Do not implement any new functionality.

Do not change:

- File saving
- Templates
- Timestamp filenames
- Automatic saving
- Session timing
- SAVE behavior
- NEW behavior
- CLOSE behavior
- Storage configuration

---

## Testing

Test on the Nokia T20.

### Test 1 — Writing

Type enough text to create many lines.

Expected:

- One visual line above cursor is visible.
- Current line is visible.
- One visual line below cursor is visible.
- Older/further text is hidden in both directions.

### Test 2 — Browsing

Scroll upward.

Expected:

The entire document becomes visible.

### Test 3 — Resume Writing

Start typing.

Expected:

The smaller one-above/one-below writing window is restored.

### Test 4 — Wrapped Text

Type a long paragraph.

Expected:

The one-above/one-below rule applies to visual lines.

### Test 5 — Rotation

Rotate the Nokia T20.

Expected:

Text remains intact and the behavior continues to work.

---

## Completion Criteria

Requirement 04A is complete when:

1. Distraction-Free Writing Mode shows one visual line above the cursor.
2. The current visual line is visible.
3. One visual line below the cursor is visible.
4. Text outside this window is hidden.
5. Browsing Mode continues to show the entire document.
6. Starting to edit restores the reduced writing window.
7. Typewriter behavior continues to work.
8. The underlying document remains intact.
9. The application remains responsive.
10. The application works on the Nokia T20.

---

## After Implementation

Explain:

1. What was changed from Requirement 04.
2. Which files were changed.
3. How the visible-line count is controlled.
4. Whether any dependencies were added.

Do not implement Requirement 05.

Stop after completing this requirement.