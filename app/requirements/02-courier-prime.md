# Requirement 02 — Courier Prime Font

## Objective

Change the writing editor from the default Android font to **Courier Prime**.

This requirement is ONLY about adding and using the Courier Prime font.

Do not implement any other planned functionality.

---

## Font

The text editor must use:

**Courier Prime**

Courier Prime must be bundled with the application.

The application must not depend on an internet connection to obtain the font.

The font should therefore be included as an application resource.

Do not download the font dynamically at runtime.

---

## Font Usage

Courier Prime must be used for:

- All text displayed inside the writing editor
- The text being entered by the user
- The cursor/insertion point should align correctly with the Courier Prime
  character grid

The SAVE, NEW and CLOSE buttons do not need to use Courier Prime unless
required by the implementation.

The user must not be given an option to change the editor font.

There will be only one editor font.

---

## Font File

Obtain a properly licensed version of the Courier Prime font suitable for
bundling with an Android application.

Place the font in the appropriate Android project resource directory.

Do not commit an unlicensed or questionable copy of the font.

Document the font's license/source in the project if required by its license.

---

## Text Editor Behavior

All behavior implemented in Requirement 01 must remain unchanged.

The editor must continue to support:

- Multiple lines
- Enter/newlines
- Backspace
- Delete
- Cursor movement
- Touch cursor positioning
- Text selection
- Normal scrolling

Do not implement typewriter-style scrolling yet.

---

## Do NOT Implement Yet

Do NOT implement:

- Typewriter cursor positioning
- Hiding lines above the cursor
- Fading
- Markdown rendering
- File creation
- File saving
- Timestamp filenames
- Templates
- Configurable directories
- Automatic saving
- Session timing
- SAVE functionality
- NEW functionality
- CLOSE functionality
- E-Ink-specific behavior
- Settings
- File opening
- File browser

These will be implemented in later requirements.

---

## Dependencies

Do not add a third-party font library or other dependency merely to load the
font.

Use Android/Jetpack Compose's existing font resource mechanisms where
possible.

If an additional dependency is genuinely required, explain:

1. Why it is required.
2. Why the existing Android/Compose functionality is insufficient.
3. What dependency was added.

Do not add dependencies without explaining them.

---

## Code Requirements

Keep the implementation simple.

Modify only the code necessary to load and apply Courier Prime to the
editor.

Do not unnecessarily restructure the application.

Do not change the existing UI architecture unless there is a clear technical
reason.

---

## Visual Verification

Run the application on the Nokia T20.

Verify that:

1. Text visibly uses Courier Prime.
2. Characters have the expected monospaced appearance.
3. Different characters occupy the expected fixed-width spacing.
4. The cursor is positioned correctly relative to the text.
5. Multiple lines render correctly.
6. Text selection still works.
7. The keyboard still works normally.
8. SAVE, NEW and CLOSE remain unchanged.

Pay particular attention to the cursor position and character alignment.

---

## Completion Criteria

This requirement is complete when:

1. Courier Prime is bundled with the application.
2. The editor uses Courier Prime.
3. The application does not need internet access to load the font.
4. Existing Requirement 01 behavior still works.
5. The application builds successfully.
6. The application runs successfully on the Nokia T20.
7. No unnecessary dependencies were introduced.

---

## After Implementation

Explain:

1. Where the Courier Prime font file was placed.
2. How the font is loaded.
3. Which source files were changed.
4. How the font is applied to the editor.
5. Whether any dependencies were added.
6. Where the font license/source information is documented.
7. How to verify the font is actually being used.

Do not implement Requirement 03.

Stop after completing this requirement.