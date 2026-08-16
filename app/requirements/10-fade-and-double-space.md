# Requirement 10 — Editor Fade and Double-Space Period

## Objective

Make two changes to the editor:

1. Refine the distraction-free fading behavior around the current line.
2. Automatically insert a period when the user presses the space bar twice.

Do not change any other editor functionality.

---

# Part 1 — Distraction-Free Fading

## 1. General Behavior

The editor must keep the entire document present.

Text outside the immediate writing area must NOT become invisible or be
removed from the display.

Instead, text should become progressively more faded as it gets farther
from the current writing line.

The document should remain technically visible.

A user who deliberately looks closely at the faded text should still be
able to make it out.

The effect is intended to reduce distraction, NOT hide the document.

---

## 2. Current Line

The current line where the cursor is located must remain fully visible.

The current line must not be faded.

---

## 3. Lines Above

The two lines immediately above the current line form the upper fade zone.

Use the following visual hierarchy:

    Current line       → fully visible
    1 line above      → slightly faded
    2 lines above     → more strongly faded
    Further above     → very strongly faded

The text further above must still technically remain visible.

It must NOT become completely invisible.

---

## 4. Lines Below

Apply the same behavior below the current line:

    Current line       → fully visible
    1 line below      → slightly faded
    2 lines below     → more strongly faded
    Further below     → very strongly faded

Again, text must remain technically visible.

Do NOT completely hide the text.

---

## 5. Gradual Fade

The transition should feel gradual rather than like a hard boundary.

Conceptually:

    FULL VISIBILITY
          │
          │ Current line
          │
    Slightly faded
          │
    More faded
          │
    Very faded
          │
    Still technically visible
          │
    ...rest of document

Do not use a sudden cutoff where text changes directly from normal visibility
to invisible.

---

## 6. Important Requirement — Text Must Remain Visible

This is important.

The application must NOT use a visibility state equivalent to:

    invisible
    gone
    alpha = 0

for the surrounding document text.

The faded text should remain rendered.

The goal is:

    "I have to squint to read it."

The goal is NOT:

    "I cannot see it at all."

The entire document therefore remains present while the current writing area
has the strongest visual emphasis.

---

## 7. Writing Mode

When the user is actively typing:

- The current line is fully visible.
- The two lines above fade progressively.
- The two lines below fade progressively.
- Text farther away remains very faint but technically visible.

This creates the distraction-free writing effect.

---

## 8. Browsing / Scrolling

When the user scrolls through the document or moves the cursor to another
part of the document, the surrounding document should become clearly
visible again.

This allows the user to read and navigate the document normally.

When the user resumes typing, the distraction-free fading behavior should
return.

---

## 9. Cursor Movement

If the user places the cursor on an earlier or later line:

- That line becomes the fully visible current line.
- The two lines above it form the upper fade zone.
- The two lines below it form the lower fade zone.
- The rest of the document remains very faint.

The fade must follow the cursor position.

It must not remain fixed to the original writing position.

---

## 10. Rotation

The fading behavior must survive screen rotation.

After rotation:

- The document content must remain intact.
- The cursor position must remain correct.
- The fade must be recalculated around the current line.
- No text should disappear because of rotation.

---

# Part 2 — Double-Space Period

## 11. Objective

Implement the common writing behavior where pressing the space bar twice
automatically inserts a period followed by a space.

Example:

User types:

    This is a sentence

Then presses:

    SPACE SPACE

The editor should produce:

    This is a sentence. 

The cursor should be positioned after the automatically inserted trailing
space, ready for the next word.

---

## 12. Double-Space Detection

When the user presses the space bar twice consecutively:

    SPACE + SPACE

replace the second space with:

    . + SPACE

Result:

    "  "

becomes:

    ". "

---

## 13. Examples

Input:

    Hello SPACE SPACE

Result:

    Hello. |

where `|` represents the cursor.

Input:

    This is a test SPACE SPACE Next

Result:

    This is a test. Next

---

## 14. Only Consecutive Spaces

The automatic period should only be triggered when the user enters two
consecutive spaces.

For example:

    word SPACE SPACE

should produce:

    word. 

But:

    word SPACE [other editing action] SPACE

must not necessarily trigger the automatic period.

Do not treat arbitrary spaces elsewhere in the document as a double-space
period.

---

## 15. Do Not Trigger at the Beginning

Do not insert a period simply because two spaces occur at the beginning of
the document.

The automatic period behavior is intended for normal sentence-ending
double-space input.

---

## 16. Existing Periods

Do not create duplicate punctuation.

For example, if the user has already typed:

    Hello.

then presses space twice, do not produce:

    Hello.. 

The implementation should avoid adding an unnecessary second period.

---

## 17. Markdown Compatibility

The resulting text must remain normal Markdown text.

The automatic period must be inserted into the actual document content.

It must be saved normally in the `.md` file.

---

# Existing Functionality

Do not change the following:

- Courier Prime
- 22sp font size
- Typewriter cursor positioning
- SAVE
- NEW
- CLOSE
- CANCEL
- Automatic saving
- Main Markdown file creation
- Main Markdown filename
- Main Markdown directory
- BAS writing-time logging
- 10-minute idle rule
- Application icon
- Rotation/state preservation
- Keyboard shortcuts already implemented or planned

---

# Testing

The Nokia T20 is the primary testing device.

## Test 1 — Current Line

Place the cursor in the middle of a document.

Verify:

- Current line is fully visible.
- One line above is somewhat faded.
- Two lines above are more strongly faded.
- One line below is somewhat faded.
- Two lines below are more strongly faded.
- Text farther away is very faint but still technically visible.

---

## Test 2 — Faded Text Is Not Invisible

Look carefully at text several lines away from the cursor.

Verify that the text can still be seen.

It should require deliberate attention/squinting to read.

It must not completely disappear.

---

## Test 3 — Cursor Movement

Move the cursor to another part of the document.

Verify that the fade follows the cursor.

---

## Test 4 — Scrolling

Scroll through the document.

Verify that the document becomes clearly visible while browsing.

Resume typing.

Verify that the distraction-free fade returns.

---

## Test 5 — Rotation

Rotate the Nokia T20 while editing.

Verify:

- Text remains intact.
- Cursor remains correct.
- Fade is correctly recalculated.

---

## Test 6 — Double Space

Type:

    Hello world

Press SPACE twice.

Expected:

    Hello world. 

with the cursor immediately after the trailing space.

---

## Test 7 — Multiple Sentences

Type:

    This is sentence one SPACE SPACE This is sentence two SPACE SPACE

Expected:

    This is sentence one. This is sentence two. 

---

## Test 8 — Normal Single Spaces

Type:

    This is a normal sentence

Verify that normal single spaces remain unchanged.

---

## Test 9 — Existing Period

Type:

    Hello.

Then press space twice.

Verify that the editor does not produce:

    Hello..

---

## Test 10 — Saving

Use SAVE and CLOSE.

Open the resulting Markdown file externally.

Verify that:

- The periods inserted by double-space are present.
- The document contains normal Markdown text.
- No editor-specific characters or formatting have been introduced.

---

# Completion Criteria

Requirement 10 is complete when:

1. The current line is fully visible.
2. The two lines above progressively fade.
3. The two lines below progressively fade.
4. Text farther away remains very faint but technically visible.
5. No surrounding text becomes completely invisible.
6. The fade follows the cursor position.
7. Browsing/scrolling still allows the document to become clearly visible.
8. The fade returns when writing resumes.
9. Rotation does not break the fade behavior.
10. Two consecutive spaces produce a period followed by a space.
11. The cursor is positioned correctly after the automatic period.
12. Existing punctuation is not duplicated.
13. The resulting document remains valid normal Markdown text.
14. Existing application functionality remains unchanged.

---

# After Implementation

Provide a short summary explaining:

1. How the fade levels are implemented.
2. How the application ensures faded text remains technically visible.
3. How the fade follows the cursor.
4. How browsing mode interacts with the fade.
5. How double-space detection works.
6. How the automatic period is inserted.
7. Which files were changed.
8. Confirmation that existing functionality was not changed.

Do not implement any additional functionality.

Stop after completing Requirement 10.