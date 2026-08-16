# Change Request 10B — Typography Spacing and Writing Density

## Objective

Refine the editor typography so that the writing experience matches the
desired visual appearance shown in the supplied reference image.

The reference image is:

    assets/typography-reference.png

This image is a visual reference for the desired:

- Character spacing
- Word spacing
- Line spacing
- Overall text density
- Horizontal writing width
- General typography rhythm

Use the reference image together with the requirements below.

The goal is to reproduce the visual feel of the reference, not to modify or
recreate the image itself.

---

# 1. Reference Image

The typography reference image is stored in the project at:

    assets/typography-reference.png

Use this image as the primary visual reference when tuning the editor
typography.

The image must NOT be modified.

Do NOT:

- Resize the image as part of the application
- Add the image to the application UI
- Display the image inside the application
- Generate a replacement image
- Alter the image

It exists only as a development/design reference.

When implementing this requirement, visually compare the editor with the
reference image.

---

# 2. Reference Appearance

The reference demonstrates the desired balance between:

- Monospaced text
- Comfortable character spacing
- Natural word spacing
- Relatively generous line spacing
- Clean and uncluttered appearance
- Comfortable horizontal margins
- A relatively narrow writing column
- Good horizontal and vertical breathing room

The text should feel comfortable to read and write without appearing
double-spaced.

The overall visual rhythm of the reference is more important than matching
an arbitrary numerical value exactly.

---

# 3. Font

Continue using:

    Courier Prime

Do not replace the font.

Do not introduce additional fonts.

---

# 4. Font Size

The current font size is:

    22sp

Keep the font size at:

    22sp

Do NOT increase or decrease the font size as part of this Change Request.

---

# 5. Letter Spacing

Do not artificially expand the individual characters.

The character spacing should remain natural for Courier Prime.

Target:

    Default / 0 additional letter spacing

unless a very small adjustment is demonstrably necessary to match the
reference image.

Do not introduce visibly expanded letter spacing.

---

# 6. Word Spacing

Word spacing should remain comfortable and natural.

The space between words should be clearly distinguishable from the spacing
between individual characters.

Do not add excessive word spacing.

Prefer the font's natural word spacing unless Android's text rendering
requires a small adjustment.

---

# 7. Line Spacing

Adjust the line spacing to provide the relaxed vertical rhythm shown in the
reference image.

The lines should not appear cramped.

They should have enough vertical space that each line is clearly separated
from the next.

As an initial target:

    Font size: approximately 22sp
    Line height: approximately 31–33sp

Use the reference image and the Nokia T20 display to determine the final
visual value.

The exact value may be adjusted slightly during testing.

Do NOT make the text look double-spaced.

The objective is relaxed but compact line spacing.

---

# 8. Horizontal Writing Width

Increase the left and right margins of the writing area slightly from the
current implementation.

The objective is to create a comfortable writing column with more breathing
room on both sides.

Use the reference image as the visual guide.

Do not make the margins excessively large.

The writing column should feel deliberate and comfortable rather than
stretching across the entire screen.

---

# 9. Text Alignment

Keep the text left aligned.

Do not center the text.

Do not justify the text.

The typewriter cursor behavior should remain independent of text alignment.

---

# 10. Typewriter Position

Do not change the existing typewriter behavior.

The current writing line should continue to remain approximately centered
vertically.

Changing line spacing must not cause the typewriter positioning behavior to
break.

The cursor should continue to appear at the intended writing position.

---

# 11. Interaction With Fade

This Change Request must work correctly with the existing distraction-free
fade behavior.

Do NOT modify the fade logic as part of this requirement.

The current fade rules are:

- Current line: fully visible
- Lines 1–5 above: progressively faded
- Line 6 above: invisible
- Lines 1–5 below: progressively faded
- Line 6 below: invisible

The typography change must work correctly with these existing fade rules.

Changing line height must not change the number of lines in the fade zone.

---

# 12. Visible Writing Area

After changing the line height, verify that the visible writing area still
feels comfortable.

The application should not compensate for increased line spacing by making
the text smaller.

The font remains:

    Courier Prime
    22sp

The objective is a more relaxed writing rhythm, not maximum text density.

---

# 13. Nokia T20

The Nokia T20 is the primary development and testing device.

The typography should be tuned primarily using the Nokia T20 display.

Do not create a device-specific font size.

The font remains 22sp on all supported devices.

---

# 14. Testing

## Test 1 — Compare With Reference

Open the application on the Nokia T20 and compare the editor visually with:

    assets/typography-reference.png

Compare:

- Character spacing
- Word spacing
- Line spacing
- Text density
- Horizontal margins
- Writing-column width
- Overall visual rhythm

The result should feel similar to the reference.

Do not attempt to match the screenshot pixel-for-pixel.

---

## Test 2 — Line Spacing

Enter several paragraphs of text.

Verify that:

- Lines do not appear cramped.
- Adjacent lines are clearly separated.
- The vertical rhythm feels relaxed.
- The text does not look double-spaced.
- The typography remains comfortable for extended writing.

---

## Test 3 — Horizontal Margins

Enter a long paragraph.

Verify that the text has comfortable left and right margins.

Compare the writing width with the reference image.

The writing column should feel deliberate rather than stretching across the
entire screen.

---

## Test 4 — Typewriter Mode

Type continuously for several paragraphs.

Verify that:

- The current line remains positioned correctly.
- Line spacing does not cause cursor-positioning problems.
- The typewriter behavior remains stable.
- The cursor remains approximately centered vertically.

---

## Test 5 — Fade

Verify that changing the line height has not altered the fade behavior.

The fade must still follow the current line correctly:

- Lines 1–5 above progressively fade.
- Line 6 above is invisible.
- Lines 1–5 below progressively fade.
- Line 6 below is invisible.

---

## Test 6 — Rotation

Rotate the Nokia T20.

Verify:

- Text remains intact.
- Font remains Courier Prime.
- Font remains 22sp.
- Line spacing remains correct.
- Left/right margins remain correct.
- Typewriter positioning remains correct.
- Fade remains correct.

---

# 15. Do Not Change

Do NOT change:

- Courier Prime
- 22sp font size
- Fade logic
- File handling
- SAVE
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
- Double-space period behavior

Only typography spacing, line height, and horizontal margins are in scope.

---

# Completion Criteria

Requirement 10B is complete when:

1. Courier Prime remains the application font.
2. Font size remains 22sp.
3. Character spacing looks natural.
4. Word spacing looks natural.
5. Line spacing is more relaxed and readable.
6. The initial line-height target is approximately 31–33sp, adjusted if
   necessary based on the reference image and actual device appearance.
7. Left and right margins are increased slightly.
8. The resulting typography has a similar visual rhythm to
   `assets/typography-reference.png`.
9. The writing column feels comfortable rather than excessively wide.
10. Typewriter positioning remains correct.
11. The existing fade behavior remains unchanged.
12. Rotation does not break the typography.
13. No unrelated functionality is changed.

---

# After Implementation

Provide a short summary explaining:

1. The final line-height value selected.
2. The final left and right margins selected.
3. Whether any letter-spacing adjustment was necessary.
4. Whether any word-spacing adjustment was necessary.
5. How the result compares visually with
   `assets/typography-reference.png`.
6. Which files were changed.
7. Confirmation that existing functionality was not changed.

Stop after implementing this Change Request.