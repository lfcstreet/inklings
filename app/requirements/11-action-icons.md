# Requirement 11 — Action Buttons as Icons

## Objective

Replace the existing text-based action buttons with three compact icon-based
controls:

- SAVE
- NEW
- CLOSE

Do NOT add CANCEL as part of this requirement.

The controls are temporary and distraction-free.

They are shown/hidden by tapping inside the writing area, outside the
action-button area.

A second tap in the same area hides them again.

---

# 1. Visual Reference

The visual design reference for the three buttons is:

    assets/action-buttons-reference.png

This image is a DESIGN REFERENCE ONLY.

It shows the intended:

- Button shape
- Rounded-square design
- Blue outline
- Icon style
- Icon proportions
- Relative button sizes
- Spacing between buttons
- Overall visual appearance

Do NOT package this PNG into the application.

Do NOT display this PNG in the application.

Do NOT use the PNG itself as the button artwork.

Instead, recreate the design using scalable vector icons / Compose vector
graphics and normal Compose UI components.

The final application should visually resemble the reference image.

---

# 2. Three Controls

The controls are:

    SAVE     NEW     CLOSE

They must contain icons only.

Do NOT display text such as:

    Save
    New
    Close

inside or underneath the buttons.

---

# 3. Icons

Use scalable vector icons.

Do NOT use PNG/JPG images for the actual icons.

The three icons should visually correspond to the reference:

### SAVE

Use a simple floppy-disk/save symbol.

### NEW

Use a simple document/page symbol with a plus sign.

### CLOSE

Use a simple X symbol.

All three icons should have:

- Consistent visual weight
- Consistent stroke style
- Consistent proportions
- Clean geometry
- Minimal appearance

The icons should look like a coherent set.

If an appropriate built-in Material/Compose vector icon closely matches the
reference, it may be used.

Otherwise create an appropriate custom vector icon.

---

# 4. Button Shape

Each control should be a rounded square.

The reference image shows:

- Transparent/light interior
- Blue outline
- Rounded corners
- No text
- No gradient
- No shadow
- Minimal visual treatment

Use the reference image as the visual guide.

The exact dimensions must be implemented using Android dp rather than fixed
pixel dimensions.

---

# 5. Button Size

Use a comfortable touch target.

Target:

    approximately 48–56dp × 48–56dp

The exact value can be adjusted slightly during testing.

The icon itself should be smaller than the touch target, approximately:

    24dp

The button must remain easy to tap on the Nokia T20.

Do not make the icons unnecessarily large.

---

# 6. Button Spacing

The three buttons should be displayed horizontally:

    SAVE    NEW    CLOSE

with a small, consistent gap between them.

They should form a compact group rather than a full-width toolbar.

Use the reference image as the visual guide.

---

# 7. Color — Light Theme

The application currently has a blue visual theme.

For the light theme:

- Icon → blue
- Button outline → blue
- Button interior → transparent/light
- No filled blue button background

Use the application's theme color rather than hard-coding the color into
each icon.

The exact blue should visually resemble the blue in:

    assets/action-buttons-reference.png

The reference image is a visual guide; the application theme remains the
source of truth for the actual color.

---

# 8. Color — Dark Theme

The controls must also work correctly in dark theme.

Do NOT create separate PNG artwork for dark mode.

The same vector icons must be used.

Use theme-aware colors so that:

- Icons remain clearly visible.
- Button outlines remain clearly visible.
- Contrast remains comfortable.
- The controls do not disappear against the background.

Use an appropriately lighter/brighter version of the application's blue
theme color when necessary.

Do NOT hard-code a single color that only works in light mode.

---

# 9. Responsive Sizing

Do not use pixel-based sizing.

Use Android dp/sp and Compose responsive layout techniques.

The controls should work on:

- Nokia T20
- Pixel 7a
- Boox Go 10.3

The buttons should NOT scale proportionally with the physical screen size.

A large screen should not result in enormous buttons.

The controls should remain approximately the same comfortable physical size
on all three devices.

---

# 10. Position

When visible, the three buttons must appear:

- At the top portion of the writing area.
- Horizontally centered.
- Together as one compact group.

Conceptually:

                    ┌────┐  ┌────┐  ┌────┐
                    │ 💾 │  │ 📄+│  │  X │
                    └────┘  └────┘  └────┘

                         writing area

Do not place the controls permanently at the bottom of the screen.

Do not place them in a conventional permanent Android toolbar.

The writing area should remain the primary visual focus.

---

# 11. Initial State

When the editor is opened:

    Controls = hidden

The writing area should remain clean and distraction-free.

Do not permanently display the three buttons.

---

# 12. Showing and Hiding — Toggle Behavior

The action buttons are controlled by a toggle.

A normal TAP inside the writing area AND outside the action-button area
toggles the visibility of the controls.

If the controls are hidden:

    Tap inside writing area
    + outside action buttons
        → Controls become visible

If the controls are visible:

    Tap inside writing area
    + outside action buttons
        → Controls become hidden

Therefore the same type of tap acts as both SHOW and HIDE.

This is intentional.

Do NOT require a special swipe or gesture to show the controls.

---

# 13. Tap vs Scroll

The implementation MUST distinguish a normal tap from a scrolling/dragging
gesture.

A normal tap should toggle the controls.

A touch that becomes a drag/scroll must NOT toggle the controls.

For example:

    Tap
        → toggle controls

    Touch + drag
        → normal scrolling
        → do NOT toggle controls

This distinction is important because the writing area is scrollable.

Do not implement button visibility using a generic touch handler that causes
normal scrolling to toggle the buttons.

The existing scrolling behavior must remain intact.

---

# 14. Tap and Cursor Placement

A tap inside the writing area must continue to perform the normal editor
behavior, including cursor placement.

Showing/hiding the controls must NOT prevent the user from placing the cursor.

The action-button visibility mechanism must not interfere with:

- Cursor placement
- Text selection
- Text editing
- Scrolling
- Keyboard interaction

---

# 15. Action Button Area

The three action buttons have their own touch area.

A tap on an action button must NOT also be interpreted as a tap on the
underlying writing area.

For example:

    Tap SAVE
        → SAVE action
        → hide controls

It must NOT:

    → trigger the writing-area toggle
    → move the cursor underneath the button
    → perform any unrelated writing-area action

---

# 16. SAVE

When the user taps the SAVE icon:

Perform the existing SAVE behavior.

Do not change the existing save implementation.

The icon is only replacing the existing text-based button.

After the save action:

    Controls → hidden

---

# 17. NEW

When the user taps the NEW icon:

Perform the existing NEW behavior.

Do not change the existing new-session implementation.

The icon is only replacing the existing text-based button.

After the NEW action:

    Controls → hidden

The appropriate new writing session should become active.

---

# 18. CLOSE

When the user taps the CLOSE icon:

Perform the existing CLOSE behavior.

Do not change the existing close implementation.

The icon is only replacing the existing text-based button.

After the CLOSE action:

    Controls → hidden

---

# 19. Starting to Type

When the user starts typing:

    Controls → hidden

This should happen immediately when text input begins.

The controls must not remain visible while the user is actively typing.

Typing must otherwise continue normally.

---

# 20. Tap Outside the Writing Area

If the controls are visible and the user taps outside the writing area:

    Controls → hidden

The tap must not accidentally trigger:

- SAVE
- NEW
- CLOSE
- Cursor placement
- Text editing

---

# 21. Keyboard Shortcuts

The existing keyboard shortcuts for:

- SAVE
- NEW
- CLOSE

must continue to work.

Do not remove or change the existing keyboard shortcuts.

The icon buttons and keyboard shortcuts should invoke the same underlying
actions rather than implementing separate versions of the functionality.

---

# 22. Accessibility

Even though the visible controls contain icons only, each control must have
an appropriate accessibility/content description.

Use:

    SAVE
    NEW
    CLOSE

as the semantic descriptions.

Do not display these labels visually.

---

# 23. Fade Interaction

The controls must not interfere with the existing writing fade behavior.

When the action controls are visible:

- The document remains in its existing state.
- The fade behavior is not permanently disabled.
- The controls are a temporary UI overlay.

When the controls disappear, the editor returns to the same writing state.

Do not modify the fade implementation as part of this requirement.

---

# 24. Typography

Do not change typography as part of this requirement.

Keep:

    Font: Courier Prime
    Font size: 22sp

Do not change:

- Font
- Font size
- Letter spacing
- Word spacing
- Line spacing
- Margins

Typography is handled separately.

---

# 25. Rotation

The controls must work correctly after screen rotation.

Verify:

- Controls are initially hidden.
- A tap inside the writing area toggles them.
- A second tap in the same area hides them.
- Scrolling does not toggle them.
- Controls appear at the top and remain horizontally centered.
- Buttons retain appropriate size.
- Icons remain correctly rendered.
- Light/dark theme behavior remains correct.

Do not lose the current writing content during rotation.

---

# 26. Do Not Change

Do NOT change:

- SAVE functionality
- NEW functionality
- CLOSE functionality
- Keyboard shortcuts
- File creation
- File naming
- File locations
- Writing-time tracking
- BAS logging
- Fade behavior
- Typography
- Application icon
- Auto-save behavior
- Double-space period behavior
- Existing scrolling behavior
- Existing cursor behavior

This requirement is specifically about replacing the existing action-button
UI and implementing its temporary visibility behavior.

---

# Testing

## Test 1 — Initial State

Open the application.

Expected:

    No action buttons visible.

---

## Test 2 — Show Controls

Tap inside the writing area, outside the action-button area.

Expected:

    SAVE    NEW    CLOSE

appears at the top portion of the writing area, horizontally centered.

---

## Test 3 — Hide Controls

With the controls visible, tap inside the writing area again, outside the
action-button area.

Expected:

    Controls disappear.

---

## Test 4 — Toggle Repeatedly

Perform:

    Tap writing area → visible
    Tap writing area → hidden
    Tap writing area → visible
    Tap writing area → hidden

Verify that the toggle is reliable.

---

## Test 5 — Scrolling

With controls hidden, touch and drag through the writing area.

Expected:

    Normal scrolling occurs.
    Controls do NOT appear.

Repeat with controls visible.

Expected:

    Normal scrolling occurs.
    Controls do NOT toggle merely because of the scroll gesture.

---

## Test 6 — Cursor Placement

Tap at different positions in the writing area.

Verify that:

- The controls toggle.
- The cursor can still be positioned correctly.
- Text editing remains normal.

---

## Test 7 — SAVE

Show the controls.

Tap SAVE.

Verify:

- Existing save behavior occurs.
- Controls disappear.

---

## Test 8 — NEW

Show the controls.

Tap NEW.

Verify:

- Existing new-session behavior occurs.
- Controls disappear.

---

## Test 9 — CLOSE

Show the controls.

Tap CLOSE.

Verify:

- Existing close behavior occurs.
- Controls disappear.

---

## Test 10 — Typing

Show the controls.

Start typing.

Expected:

    Controls disappear immediately.

Verify that typing continues normally.

---

## Test 11 — Outside Writing Area

Show the controls.

Tap outside the writing area.

Expected:

    Controls disappear.

---

## Test 12 — Action Button Isolation

Show the controls.

Tap directly on SAVE, NEW, or CLOSE.

Verify that the tap:

- Activates only the selected action.
- Does not toggle the writing-area controls separately.
- Does not move the cursor underneath the button.

---

## Test 13 — Keyboard

Verify that the existing keyboard shortcuts still perform:

- SAVE
- NEW
- CLOSE

---

## Test 14 — Light Theme

Verify:

- Blue icon
- Blue outline
- Good contrast
- Clean appearance
- Reference image's visual style is followed

---

## Test 15 — Dark Theme

Verify:

- Icons remain visible.
- Borders remain visible.
- Theme-aware blue is used.
- No PNG-specific light-theme artwork is being used.

---

## Test 16 — Nokia T20

Verify:

- Buttons are comfortably tappable.
- Buttons are not too large.
- Buttons are centered.
- Icons are crisp.
- Tap-to-toggle works reliably.
- Scrolling works normally.

---

## Test 17 — Pixel 7a

Verify the same behavior on the Pixel 7a.

The controls should remain appropriately sized.

---

## Test 18 — Boox Go 10.3

Verify that:

- Buttons remain appropriately sized.
- Icons remain crisp.
- Controls remain centered.
- The controls do not become excessively large because of the larger
  display.

---

## Test 19 — Rotation

Rotate the device and repeat:

- Show controls
- Hide controls
- SAVE
- NEW
- CLOSE
- Scroll
- Type

Verify that everything remains functional.

---

# Completion Criteria

Requirement 11 is complete when:

1. Text-based SAVE/NEW/CLOSE buttons have been replaced by icon buttons.
2. The icons are implemented as scalable vectors.
3. The visual design follows `assets/action-buttons-reference.png`.
4. The PNG reference itself is not packaged or displayed by the application.
5. The buttons are rounded squares.
6. The controls use the application's blue theme.
7. Light theme is supported.
8. Dark theme is supported.
9. Buttons use responsive dp-based sizing.
10. Buttons work comfortably on the Nokia T20.
11. Buttons work on the Pixel 7a.
12. Buttons work on the Boox Go 10.3.
13. Controls are hidden initially.
14. A deliberate tap inside the writing area but outside the action-button
    area toggles the controls.
15. A second such tap hides the controls.
16. Scrolling does not toggle the controls.
17. Dragging is never interpreted as a tap for button visibility.
18. Starting to type hides the controls.
19. Tapping outside the writing area hides the controls.
20. Tapping SAVE, NEW, or CLOSE performs the existing action and hides the
    controls.
21. Action-button taps do not trigger the writing-area toggle.
22. Normal cursor placement continues to work.
23. Normal text editing continues to work.
24. Existing keyboard shortcuts continue to work.
25. Accessibility descriptions are provided.
26. Rotation does not break the controls.
27. No unrelated functionality is changed.

---

# After Implementation

Provide a short summary explaining:

1. Which vector icons were used/created.
2. How the visual design was matched to
   `assets/action-buttons-reference.png`.
3. How light and dark themes are handled.
4. The final button and icon sizes.
5. How tap-vs-scroll detection is implemented.
6. How the tap-to-toggle behavior is implemented.
7. How typing hides the controls.
8. How action-button taps are isolated from the writing area.
9. Which files were changed.
10. Confirmation that the PNG reference is not packaged into the
    application.
11. Confirmation that SAVE, NEW, CLOSE, and keyboard shortcuts continue to
    use the existing functionality.

Stop after implementing this requirement.