# Change Request 10A — Fade Visibility Boundary

## Objective

Refine the existing distraction-free fade behavior.

The document should progressively fade away from the current/cursor line.

The fade must become completely invisible starting from the 6th line away
from the current line, both above and below.

The current line is the center of the fade.

---

# 1. Visibility Model

The current cursor line is the central line.

Count lines outward from the current line.

### Above the current line

    Line -1  → visible, slightly faded
    Line -2  → more faded
    Line -3  → more faded
    Line -4  → strongly faded
    Line -5  → very strongly faded, but still visible
    Line -6  → completely invisible
    Line -7+ → completely invisible

### Below the current line

    Line +1  → visible, slightly faded
    Line +2  → more faded
    Line +3  → more faded
    Line +4  → strongly faded
    Line +5  → very strongly faded, but still visible
    Line +6  → completely invisible
    Line +7+ → completely invisible

---

# 2. Central Line

The current line containing the cursor must be fully visible.

Therefore:

    Line 0 → 100% visibility

The cursor and the text on the current line must remain clearly visible.

---

# 3. Progressive Fade

The visibility should progressively decrease as the distance from the
current line increases.

Conceptually:

    Distance       Visibility
    --------------------------------
    0              Fully visible
    1              Slightly faded
    2              More faded
    3              More faded
    4              Strongly faded
    5              Very strongly faded
    6              Completely invisible
    7+             Completely invisible

The exact alpha values may be chosen during implementation, but the visual
effect must clearly follow this progression.

The transition must not appear as an abrupt change between the first five
lines.

---

# 4. Sixth-Line Boundary

The 6th line away from the current line is the hard visibility boundary.

For example, if the cursor is on line 20:

    Line 14 → invisible
    Line 15 → very strongly faded
    Line 16 → strongly faded
    Line 17 → more faded
    Line 18 → more faded
    Line 19 → slightly faded
    Line 20 → fully visible
    Line 21 → slightly faded
    Line 22 → more faded
    Line 23 → more faded
    Line 24 → strongly faded
    Line 25 → very strongly faded
    Line 26 → invisible

Lines 14 and 26, and everything beyond them, must be completely invisible.

---

# 5. Important Clarification

The previous requirement described faded text as remaining technically
visible throughout the document.

This Change Request supersedes that behavior.

The new rule is:

- Lines 0 through ±5 remain visible at progressively decreasing opacity.
- Lines ±6 and beyond are completely invisible.

Therefore, the document is intentionally hidden outside the 5-line fade
zone around the current line.

---

# 6. Cursor Movement

The fade boundary must follow the current cursor line.

If the cursor moves to another line, recalculate the fade based on the new
current line.

For example, if the cursor moves from line 20 to line 50:

    Line 50 → fully visible
    Lines 49–46 → progressively faded
    Line 45 → invisible
    Lines 51–54 → progressively faded
    Line 55 → invisible

---

# 7. Scrolling / Browsing

When the user scrolls or browses the document, the existing browsing behavior
must continue to work.

The document should become visible for navigation/browsing as currently
implemented.

When the user resumes active writing, the ±5-line fade zone must be restored.

---

# 8. Rotation

Screen rotation must not break the fade.

After rotation:

- The document content must remain intact.
- The cursor position must remain intact.
- The current line must remain the fully visible line.
- The ±1 through ±5 fade must be recalculated.
- The ±6 and beyond boundary must remain invisible.

---

# 9. Existing Typography

Do not change the existing typography as part of this Change Request.

Keep:

    Font: Courier Prime
    Font size: 22sp

Do not change:

- Font
- Font size
- Letter spacing
- Word spacing
- Line height
- Margins

Those are separate concerns.

---

# 10. Testing

## Test 1 — Cursor in Middle of Document

Place the cursor in the middle of a document containing many lines.

Verify:

- Current line is fully visible.
- Lines 1–5 above progressively fade.
- Line 6 above is invisible.
- Lines beyond line 6 above are invisible.
- Lines 1–5 below progressively fade.
- Line 6 below is invisible.
- Lines beyond line 6 below are invisible.

---

## Test 2 — Cursor Movement

Move the cursor several lines up and down.

Verify that the entire fade window follows the cursor.

---

## Test 3 — Scrolling

Scroll through the document.

Verify that browsing behavior remains unchanged.

Resume typing and verify that the ±5-line fade zone returns.

---

## Test 4 — Top of Document

Place the cursor near the beginning of the document.

Verify that the fade calculation handles the absence of lines above correctly.

Do not create blank/artificial lines simply to fill the fade zone.

---

## Test 5 — Bottom of Document

Place the cursor near the end of the document.

Verify that the fade calculation handles the absence of lines below
correctly.

Do not create blank/artificial lines simply to fill the fade zone.

---

## Test 6 — Rotation

Rotate the Nokia T20 while editing.

Verify that the fade boundary remains correct after rotation.

---

# Completion Criteria

Requirement 10A is complete when:

1. The current line is fully visible.
2. Lines 1–5 above progressively fade.
3. Line 6 above is completely invisible.
4. Lines beyond line 6 above are completely invisible.
5. Lines 1–5 below progressively fade.
6. Line 6 below is completely invisible.
7. Lines beyond line 6 below are completely invisible.
8. The fade follows the cursor.
9. Browsing behavior continues to work.
10. The fade returns when writing resumes.
11. Rotation does not break the behavior.
12. Existing typography remains unchanged.

---

# Scope

This Change Request modifies ONLY the distraction-free fade behavior.

Do not modify:

- Saving
- Auto-save
- NEW
- CLOSE
- CANCEL
- Writing-time tracking
- BAS logging
- File naming
- File locations
- Keyboard shortcuts
- Application icon
- Typography
- Any other existing functionality

Stop after implementing this Change Request.