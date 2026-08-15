# Requirement 01 — Basic Writing Screen

## Objective

Create the first working version of the Distraction-Free Markdown Writer.

For this requirement, implement ONLY the basic writing screen.

Do not implement any of the later application features.

---

## Existing Project

The Android Studio project has already been created using:

- Kotlin
- Jetpack Compose
- Empty Activity

Use the existing project structure.

Do not unnecessarily restructure the project.

Do not add third-party libraries unless absolutely necessary.

---

## Screen

When the application starts, display a single writing screen.

The screen must contain:

1. A large multi-line text editing area.
2. A SAVE button.
3. A NEW button.
4. A CLOSE button.

For this requirement, the buttons do not need to perform any action.

They are only placeholders for the final application functionality.

---

## Text Editor

The text editor must support normal text editing, including:

- Multiple lines
- Enter/newlines
- Backspace
- Delete
- Cursor movement
- Touch cursor positioning
- Text selection

Normal scrolling is acceptable at this stage.

Do NOT implement typewriter-style scrolling yet.

---

## Font

Use the default Android font for this requirement.

Courier Prime will be added in a later requirement.

---

## Layout

Keep the interface extremely minimal.

The writing area should occupy most of the screen.

Place SAVE, NEW and CLOSE unobtrusively at the bottom of the screen.

The application should work on different screen sizes.

Do not optimize specifically for E-Ink yet.

---

## Do NOT Implement Yet

The following are intentionally excluded from this requirement:

- Courier Prime
- Typewriter cursor positioning
- Hiding old lines
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

Do not implement any of these features even if they seem easy to add.

---

## Code Requirements

Keep the implementation simple and readable.

Do not introduce unnecessary abstractions.

Do not add dependencies without a clear reason.

Use the existing Compose setup created by Android Studio.

Avoid changing the project's architecture unless there is a clear technical
reason.

---

## Completion Criteria

This requirement is complete when:

1. The project builds successfully.
2. The application launches successfully.
3. A writing screen is displayed.
4. The user can type multiple lines.
5. Normal text editing works.
6. Text selection works.
7. SAVE, NEW and CLOSE buttons are visible.
8. The application contains no unnecessary functionality.
9. The application runs successfully on the Nokia T20.

The Nokia T20 is the primary development device for this project.

The application should remain compatible with the Google Pixel 7a and BOOX
Go 10.3, but detailed cross-device testing will happen in a later stage.

---

## After Implementation

Explain:

1. Which files were changed.
2. What was changed in each file.
3. Which Kotlin concepts were introduced.
4. Which Jetpack Compose concepts were introduced.
5. Whether any dependencies were added.
6. How to run and test the application on the Nokia T20.

Do not implement Requirement 02.

Stop after completing this requirement.