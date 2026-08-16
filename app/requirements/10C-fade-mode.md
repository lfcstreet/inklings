# Change Request 10C — Sentence-Based Visibility with Line-Based Outer Fade

## Objective

Implement a new distraction-free fade mode combining sentence-based visibility
with a line-based fade outside the active three-sentence region.

This requirement has TWO distinct mechanisms:

1. Sentence-based visibility for the current, previous, and next sentences.
2. Line-based fading for everything outside those sentences.

Do NOT confuse these two mechanisms.

---

# 1. Sentence-Based Core

The current sentence containing the cursor is:

    100% visible

The immediately preceding sentence is:

    approximately 30% visible

The immediately succeeding sentence is:

    approximately 30% visible

These three sentences form the central visibility region.

Sentence boundaries are determined by periods (`.`).

---

# 2. Important — Sentences Can Span Multiple Lines

This is purely sentence-based for the central three sentences.

A sentence may occupy one visual line or many visual lines.

For example:

    This is a very long sentence which wraps onto
    several visual lines and continues here until
    it finally reaches its period.

If this is the current sentence, EVERY visual line belonging to this
sentence must be 100% visible.

Likewise, if a previous sentence spans five visual lines, ALL five of those
visual lines must be approximately 30% visible.

Do NOT fade a sentence internally based on its visual lines.

---

# 3. Central Visibility

The visibility should therefore be:

    Previous sentence → ~30%
    Current sentence  → 100%
    Next sentence     → ~30%

This is determined entirely by sentence relationship to the cursor.

---

# 4. Outer Fade

Everything outside the previous/current/next sentence region is subject to
a SECOND, separate fade mechanism.

This outer fade is LINE-BASED.

It is based on the physical visual lines on the screen/document, NOT on
sentences.

Text farther away from the central three-sentence region should progressively
fade toward complete invisibility.

Target behavior:

    Immediately outside central region → faint but visible
    Further away                     → progressively more faint
    Approximately 3 lines away       → completely invisible

The outer fade should therefore reach:

    0% visibility

after approximately three physical lines.

---

# 5. Critical Distinction

DO NOT implement the outer fade by counting sentences.

DO NOT assign different opacity levels to sentences farther away.

The outer fade must operate on physical/display lines.

For example, if the next sentence is very long and occupies 10 visual lines:

    Next sentence
        line 1 ─┐
        line 2  │
        line 3  │
        ...
        line 10 ┘

ALL of those lines remain approximately 30% visible because they belong to
the immediately succeeding sentence.

The outer line-based fade begins only OUTSIDE the central three-sentence
region.

---

# 6. Example

Suppose the document contains:

    Sentence A. Sentence B is very long and wraps across
    several visual lines. Sentence B continues here.
    Sentence B continues here. Sentence B ends here.
    Sentence C is also a long sentence and continues
    across several visual lines. Sentence C ends here.
    Sentence D. Sentence E. Sentence F.

If the cursor is inside Sentence C:

    Sentence B → ~30%
    Sentence C → 100%
    Sentence D → ~30%

Sentence A, E, F and all other content outside these three sentences must
be handled by the OUTER LINE-BASED FADE.

The fade distance is measured in physical lines away from the boundary of
the central three-sentence region.

---

# 7. Outer Fade Direction

The outer fade operates independently above and below the central region.

Above:

    central region
          ↓
    immediately outside → faint
          ↓
    next line → fainter
          ↓
    approximately 3 lines away → invisible

Below:

    central region
          ↓
    immediately outside → faint
          ↓
    next line → fainter
          ↓
    approximately 3 lines away → invisible

The fade should be smooth rather than appearing as an abrupt cutoff.

---

# 8. Existing Requirement 10A

The previous Requirement 10A implementation must NOT be deleted.

Requirement 10A is the original progressive LINE-BASED fade.

Retain it as a separate reusable fade mode/function.

It is currently NOT active.

Do not overwrite or modify it to implement 10C.

---

# 9. Fade Mode Separation

Keep fade behavior modular.

Conceptually:

    FadeMode
        ├── Requirement 10A — ProgressiveLineFade
        └── Requirement 10C — SentenceCoreWithLineOuterFade

The exact implementation architecture is up to the developer.

The important requirement is that both modes remain independently selectable.

---

# 10. Current Active Mode

For now:

    SentenceCoreWithLineOuterFade

is the active mode.

Requirement 10A remains implemented but inactive.

Do NOT add a user-facing fade setting yet.

A future requirement will allow the user to select the fade mode.

---

# 11. Required Source-Code Comments

Add explicit comments in the relevant source code explaining:

- Requirement 10A is intentionally retained.
- Requirement 10A is currently inactive.
- Requirement 10A is available for future reuse.
- Requirement 10C uses sentence-based visibility for the central three
  sentences.
- Requirement 10C uses a separate line-based fade outside those sentences.
- A future setting will allow the user to select between fade modes.

These comments are required.

---

# 12. Scrolling / Browsing

All existing scrolling and browsing behavior remains unchanged.

When browsing/scrolling:

- The document should become fully readable as it currently does.
- The user should be able to navigate the document normally.

When active writing resumes, the 10C fade must return.

---

# 13. Cursor Movement

The central sentence region must follow the cursor.

Whenever the cursor moves:

    Previous sentence → ~30%
    Current sentence  → 100%
    Next sentence     → ~30%

The outer line-based fade must then be recalculated around this new region.

---

# 14. Sentence Editing

Sentence boundaries must be recalculated when text changes.

This includes:

- Adding a period
- Deleting a period
- Editing text around a period
- Creating a new sentence
- Deleting a sentence

Do not use stale sentence boundaries.

---

# 15. Double-Space Period

The existing double-space behavior remains unchanged.

When two spaces are entered:

    SPACE + SPACE

the application creates:

    . + SPACE

The resulting period must be recognized as a sentence boundary.

---

# 16. Rotation

Rotation must not break the behavior.

After rotation:

- Text remains intact.
- Cursor position remains intact.
- Sentence boundaries remain correct.
- Current sentence remains 100%.
- Previous/next sentences remain approximately 30%.
- Outer line-based fade is recalculated correctly.

---

# 17. Typography

Do not change typography.

Keep:

    Courier Prime
    22sp

Do not change:

- Font
- Font size
- Letter spacing
- Word spacing
- Line height
- Margins

---

# Completion Criteria

Requirement 10C is complete when:

1. The current sentence is 100% visible.
2. The immediately preceding sentence is approximately 30% visible.
3. The immediately succeeding sentence is approximately 30% visible.
4. The visibility of these three sentences is determined ONLY by sentence
   boundaries.
5. Multi-line sentences retain the same opacity across all their visual
   lines.
6. Everything outside those three sentences is faded using PHYSICAL LINE
   distance.
7. The outer fade is independent of sentence boundaries.
8. The outer fade reaches approximately 0% visibility within about 3
   physical lines.
9. The fade follows the cursor.
10. Scrolling/browsing behavior remains unchanged.
11. Sentence boundaries update when editing.
12. Double-space-generated periods are recognized.
13. Rotation works correctly.
14. Requirement 10A remains intact as a separate reusable fade mode.
15. Requirement 10A remains inactive.
16. Required source-code comments are present.
17. No unrelated functionality is changed.

---

# After Implementation

Provide a short summary explaining:

1. How the current sentence is identified.
2. How the previous and next sentences are identified.
3. How multi-line sentences are handled.
4. How the 30% / 100% / 30% sentence visibility is implemented.
5. How the outer line-based fade is implemented.
6. How the outer fade reaches complete invisibility in approximately three
   physical lines.
7. How Requirement 10A was preserved.
8. Where the required source-code comments were added.
9. Which fade mode is currently active.
10. Which files were changed.
11. Confirmation that no unrelated functionality was changed.

Do not implement a fade-mode setting yet.

Stop after implementing this Change Request.