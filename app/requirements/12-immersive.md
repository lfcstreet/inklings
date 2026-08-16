# Requirement 12 — Immersive Full-Screen Writing Mode

## Objective

Make the writing application operate in Android immersive full-screen mode.

The purpose is to create a distraction-free writing environment where the
Android system UI does not occupy space at the top or bottom of the screen.

The writing area should use as much of the physical display as Android
allows.

---

# 1. Hide the Android Status Bar

When the writing screen is active, hide the Android status bar.

This includes the normal system information such as:

- Clock
- Battery indicator
- Notification icons
- Network indicators

The writing area should extend into the space normally occupied by the
status bar.

---

# 2. Hide the Bottom System Navigation UI

Hide the Android navigation/gesture area at the bottom of the display when
possible.

The application should use Android's immersive full-screen mechanism rather
than attempting to draw its own fake navigation area.

The objective is for the writing area to occupy the maximum available
display.

---

# 3. Use Android Immersive Full-Screen APIs

Use the standard Android mechanism for immersive full-screen / system-bar
hiding.

Do NOT implement this by simply drawing a large view over the system bars.

Use the appropriate Android / Jetpack Compose APIs for:

- Hiding the status bar
- Hiding the navigation bar / gesture area
- Allowing the application to use the resulting display area

Follow current Android best practices for system-bar control.

---

# 4. System UI Can Still Be Revealed

Do not attempt to permanently disable Android's system navigation.

Android must retain its normal system-level mechanism for allowing the user
to reveal system UI.

If Android temporarily reveals system bars following the appropriate
system gesture:

    This is acceptable.

The application should continue working normally.

When Android returns the application to immersive mode, the writing area
should again occupy the maximum available display.

Do NOT create custom gestures that interfere with Android's system gestures.

---

# 5. Writing Area

When immersive full-screen mode is active:

    The writing area should expand to use the additional available space.

Do not leave an unnecessary blank area where the status bar or navigation
area previously existed.

The existing writing layout should otherwise remain unchanged.

---

# 6. Existing Typography

Do NOT change typography as part of this requirement.

Keep:

    Font: Courier Prime
    Font size: 22sp

Do not modify:

- Letter spacing
- Word spacing
- Line spacing
- Left margin
- Right margin
- Fade behavior

This requirement is only about available screen/display space.

---

# 7. Existing Fade Behavior

All existing fade functionality must remain unchanged.

In particular, preserve:

- Current sentence visibility
- Previous/succeeding sentence visibility
- Same-line exception from 10C-FIX-01
- Line-based outer fade
- Scroll behavior
- Cursor behavior

Entering or leaving immersive full-screen mode must not change the underlying
fade rules.

---

# 8. Action Buttons / Requirement 11

The Save, New and Close action icons implemented under Requirement 11 must
continue to work in full-screen mode.

They must not become inaccessible because the Android system bars have been
hidden.

Their existing behavior remains unchanged.

In particular:

- Tapping the writing area shows the action buttons according to Requirement
    11.
- Tapping the writing area again hides them.
- Starting to type hides them.
- Tapping outside the action-button area hides them.
- Pressing an action button performs its existing action and hides the
  buttons as already specified.

Do NOT redesign the action buttons as part of Requirement 12.

---

# 9. Keyboard Behavior

The Android on-screen keyboard must continue to work normally.

When the user taps the writing area and the keyboard appears:

- The keyboard must remain accessible.
- The text editor must remain usable.
- The cursor must remain visible.
- The existing typewriter-style cursor positioning must continue to work.

Do not hide or interfere with the Android keyboard.

The keyboard itself is allowed to temporarily occupy part of the screen.

---

# 10. Physical Keyboard

A connected physical keyboard must continue to work normally.

Do not change any keyboard shortcuts or physical-keyboard behavior.

---

# 11. On-Screen Keyboard

Verify the behavior specifically with the Android software keyboard.

The following must continue to work:

- Normal typing
- Double-space → ". "
- Automatic capitalization after double-space
- New-session initial capitalization
- Backspace
- Cursor movement

Requirement 10D-FIX-01 must remain unchanged.

---

# 12. Screen Sizes

The implementation must adapt automatically to different screen sizes.

It must work on:

- Small phone displays
- Large phone displays
- Tablets
- Large tablets / e-ink Android tablets such as the Boox device

Do NOT hard-code:

- Screen width
- Screen height
- Status-bar height
- Navigation-bar height
- Pixel dimensions

Use Android's available window/display dimensions dynamically.

---

# 13. Orientation

Do not introduce orientation restrictions as part of this requirement.

If the application already supports orientation changes, immersive mode should
continue to work after:

- Portrait → landscape
- Landscape → portrait

After an orientation change, the application should continue using the
appropriate available display area.

---

# 14. Rotation / Layout Recalculation

When the available display area changes because immersive mode is entered,
exited, or the device orientation changes:

The editor must correctly recalculate its layout.

This includes:

- Text wrapping
- Physical display lines
- Cursor position
- Fade positioning

Do not alter the underlying fade algorithms.

They should simply operate using the newly calculated display layout.

---

# 15. App Launch

When the writing screen is opened, immersive full-screen mode should be
active.

The user should not have to manually enable full-screen mode every time.

---

# 16. Returning to the App

If the application temporarily loses focus and the user returns to it,
restore immersive full-screen mode as appropriate.

Do not repeatedly force immersive mode in a way that interferes with normal
Android system behavior.

---

# 17. No User-Facing Full-Screen Setting

Do NOT add a settings screen or toggle for full-screen mode as part of this
requirement.

For now, immersive full-screen mode is the default behavior of the writing
screen.

A user-facing setting can be added later if required.

---

# Testing

## Test 1 — Pixel

Run the application on the Pixel.

Verify:

- Status bar is hidden.
- Bottom system navigation/gesture area is hidden where Android permits.
- Writing area occupies the additional space.
- Application remains usable.

---

## Test 2 — Boox / Android Tablet

Run the application on the Boox Android tablet.

Verify:

- Status bar is hidden.
- Bottom system navigation UI is hidden where supported.
- Writing area uses the additional screen area.
- No large unused blank area remains.

---

## Test 3 — On-Screen Keyboard

In full-screen mode:

1. Tap the writing area.
2. Bring up the Android keyboard.
3. Type normally.

Verify:

- Keyboard appears normally.
- Cursor remains usable.
- Writing area adjusts correctly.
- Full-screen behavior does not prevent text entry.

---

## Test 4 — Physical Keyboard

Connect a physical keyboard.

Verify that normal typing and existing keyboard shortcuts continue to work.

---

## Test 5 — Action Icons

Verify that the Save, New and Close icons from Requirement 11 remain usable
in full-screen mode.

---

## Test 6 — Android System Gesture

Use the appropriate Android system gesture to temporarily reveal system UI.

Verify that:

- Android system UI appears normally.
- The application does not crash.
- The writing area remains usable.
- Immersive mode is restored appropriately afterward.

---

## Test 7 — Rotation

Test:

    Portrait → Landscape
    Landscape → Portrait

Verify:

- Full-screen remains active.
- Text reflows correctly.
- Cursor remains correctly positioned.
- Fade behavior remains correct.

---

## Test 8 — Fade Regression

Verify that entering immersive mode does NOT alter the existing fade
behavior.

The same sentence-based and line-based visibility rules must apply.

---

# Completion Criteria

Requirement 12 is complete when:

1. The Android status bar is hidden while writing.
2. The bottom navigation/gesture UI is hidden where Android permits.
3. The writing area uses the additional available display space.
4. Android's standard immersive full-screen mechanism is used.
5. Android system gestures remain functional.
6. The action icons from Requirement 11 continue to work.
7. The on-screen keyboard continues to work normally.
8. The physical keyboard continues to work normally.
9. Requirement 10D-FIX-01 continues to work.
10. Existing fade behavior is unchanged.
11. Cursor positioning remains correct.
12. Text wrapping remains correct.
13. The implementation adapts to different screen sizes.
14. The implementation works on both phone and tablet-sized displays.
15. Orientation changes do not break full-screen mode.
16. No hard-coded screen dimensions are introduced.
17. No new full-screen setting is added.
18. No unrelated functionality is changed.

---

# Required Source-Code Comments

Add a concise source-code comment explaining that immersive full-screen mode
is intentional and is used to provide a distraction-free writing
environment.

If Android/Compose requires special handling for system-bar behavior, add
appropriate comments explaining why that handling is necessary.

---

# After Implementation

Provide a short summary explaining:

1. Which Android/Compose API is being used for immersive full-screen mode.
2. How the status bar is hidden.
3. How the bottom navigation/gesture UI is handled.
4. How Android system gestures remain available.
5. How the implementation handles different screen sizes.
6. How keyboard behavior is preserved.
7. How the Requirement 11 action icons remain accessible.
8. How orientation changes are handled.
9. Which files were changed.
10. Confirmation that no existing writing, fade, typography, or file-saving
    behavior was changed.

Stop after implementing this requirement.