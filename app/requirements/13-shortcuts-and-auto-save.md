# Requirement 13 — Keyboard Shortcuts and Silent Auto-Save

## Objective

Add keyboard shortcuts for the three main actions and change the automatic
save interval to every 1 minute.

The shortcuts must work with a physical keyboard.

The actions must happen immediately.

There must be:

- No action-icon popup
- No confirmation dialog
- No additional notification
- No visual confirmation message

The existing action itself should simply be performed.

---

# 1. Keyboard Shortcuts

Implement the following shortcuts:

| Shortcut | Action |
|---|---|
| Ctrl+S | Save |
| Ctrl+N | New |
| Ctrl+Q | Close |

These shortcuts should invoke the same underlying actions as the
corresponding Save, New and Close icons.

Do not duplicate the action logic.

The keyboard shortcuts and icons should call the same underlying functions.

---

# 2. Ctrl+S — Save

When the user presses:

    Ctrl+S

perform the normal Save action immediately.

The behavior must be identical to pressing the Save icon.

There must be:

- No confirmation dialog
- No popup
- No action-icon display
- No toast
- No notification
- No additional UI

Simply perform the save.

---

# 3. Ctrl+N — New

When the user presses:

    Ctrl+N

perform the normal New action immediately.

The behavior must be identical to pressing the New icon.

There must be:

- No confirmation dialog
- No popup
- No action-icon display
- No notification

The new session should be created immediately according to the existing New
functionality.

The existing new-session initial capitalization behavior from
Requirement 10D-FIX-01 must remain intact.

---

# 4. Ctrl+Q — Close

When the user presses:

    Ctrl+Q

perform the normal Close action immediately.

The behavior must be identical to pressing the Close icon.

There must be:

- No confirmation dialog
- No popup
- No action-icon display
- No notification

The existing Close behavior must be preserved.

---

# 5. Action Icons Must NOT Appear

Pressing a keyboard shortcut must NOT cause the Save/New/Close action icons
to appear.

For example:

    Ctrl+S

must simply save.

Do NOT:

    show action buttons
    → require user interaction
    → perform save

Instead:

    Ctrl+S
       ↓
    Save immediately

The same applies to Ctrl+N and Ctrl+Q.

---

# 6. No Confirmation

Do not introduce confirmation dialogs for any of these shortcuts.

Examples:

    Ctrl+S → Save immediately

    Ctrl+N → Create new session immediately

    Ctrl+Q → Close immediately

The existing action behavior should determine what happens.

Do not add a new confirmation mechanism as part of this requirement.

---

# 7. Physical Keyboard

These shortcuts are specifically intended for a physical keyboard.

Verify all three shortcuts using a connected physical keyboard:

    Ctrl+S
    Ctrl+N
    Ctrl+Q

The shortcuts must work while the text editor has focus.

---

# 8. Android / Compose Keyboard Handling

Use the appropriate Android / Jetpack Compose keyboard shortcut mechanism.

Do not depend solely on raw character detection.

The implementation must correctly distinguish:

    Ctrl+S
    Ctrl+N
    Ctrl+Q

from ordinary typing.

Do not interfere with normal text entry.

---

# 9. Case

The shortcuts should work regardless of whether the physical keyboard reports
the letter as uppercase or lowercase.

These should both be treated as the same shortcut where applicable:

    Ctrl+S
    Ctrl+s

    Ctrl+N
    Ctrl+n

    Ctrl+Q
    Ctrl+q

---

# 10. Auto-Save Interval

Change the existing automatic save interval to:

    Every 1 minute

The application should automatically save the current document once every
minute while a writing session is active.

---

# 11. Silent Auto-Save

Auto-save must be completely silent.

When an automatic save occurs:

- No notification
- No toast
- No popup
- No dialog
- No action-icon display
- No status message
- No sound
- No visible indication that interrupts writing

The user should simply continue writing.

---

# 12. Auto-Save Uses Existing Save Logic

Auto-save must use the same underlying save functionality as the normal
Save action.

Do NOT create a separate file format or separate save mechanism.

The existing:

- File naming
- File location
- Markdown format
- Directory structure
- Save behavior

must remain unchanged.

---

# 13. Auto-Save Timing

The automatic save timer should operate at:

    1 minute

The timer should not create unnecessary duplicate saves when the application
is not being used.

If the application is active and a writing session exists, perform the
automatic save according to the 1-minute interval.

---

# 14. Empty Session

Do not create unnecessary files simply because an empty New session exists.

If the user creates a New session and has not entered any content, auto-save
should not create an unwanted empty file unless the existing application
logic explicitly requires it.

The existing file-creation behavior should be preserved.

---

# 15. Interaction With Manual Save

If the user manually presses:

    Ctrl+S

or taps the Save icon,

perform the normal save immediately.

The auto-save mechanism should continue operating normally afterward.

Do not show any notification that a manual or automatic save occurred.

---

# 16. Interaction With New

When:

    Ctrl+N

creates a new session, the auto-save mechanism must correctly associate
itself with the new active session.

The previous session's auto-save activity must not continue writing to the
new session.

The new session starts with the existing initial-capitalization behavior.

---

# 17. Interaction With Close

When:

    Ctrl+Q

closes the writing session, the auto-save mechanism must stop operating for
that session.

Do not allow a background auto-save to occur after the session has been
closed.

Existing Close behavior remains authoritative.

---

# 18. Action Icons

Requirement 11 already defines the Save, New and Close icons.

Do NOT redesign or change the icons as part of Requirement 13.

The only requirement here is that:

    Icon action
        =
    Keyboard shortcut action

Both must invoke the same underlying functionality.

---

# 19. Full-Screen Mode

Requirement 12 must remain unchanged.

Keyboard shortcuts must continue to work while the application is in
immersive full-screen mode.

The user should not need to exit full-screen mode to use:

    Ctrl+S
    Ctrl+N
    Ctrl+Q

---

# Testing

## Test 1 — Ctrl+S

Type some text.

Press:

    Ctrl+S

Expected:

- File is saved.
- No confirmation.
- No popup.
- No notification.
- Action icons do not appear.

---

## Test 2 — Ctrl+N

Press:

    Ctrl+N

Expected:

- New session is created immediately.
- No confirmation.
- No popup.
- No notification.
- Action icons do not appear.
- New session has initial capitalization enabled.

---

## Test 3 — Ctrl+Q

Press:

    Ctrl+Q

Expected:

- Normal Close action occurs immediately.
- No confirmation.
- No popup.
- No notification.
- Action icons do not appear.

---

## Test 4 — Lowercase Shortcut

Verify:

    Ctrl+s
    Ctrl+n
    Ctrl+q

perform the same actions.

---

## Test 5 — Auto-Save

Start a writing session and enter text.

Wait for one minute.

Expected:

- Current file is automatically saved.
- No visible notification occurs.
- User can continue typing without interruption.

---

## Test 6 — Auto-Save Repeats

Continue working beyond one minute.

Verify that the application continues to
automatically save at approximately 1-minute intervals.

---

## Test 7 — Silent Auto-Save

During an automatic save verify that there is:

- No toast
- No popup
- No dialog
- No notification
- No action-icon appearance
- No sound
- No interruption to typing

---

## Test 8 — Manual Save + Auto-Save

Press:

    Ctrl+S

Then continue typing.

Verify that subsequent automatic saves continue normally.

---

## Test 9 — New + Auto-Save

Create a new session using:

    Ctrl+N

Enter text.

Verify that auto-save operates on the new session and does not continue
saving the previous session.

---

## Test 10 — Close + Auto-Save

Close the session using:

    Ctrl+Q

Verify that no subsequent auto-save occurs for the closed session.

---

## Test 11 — Full-Screen

While in immersive full-screen mode:

    Ctrl+S
    Ctrl+N
    Ctrl+Q

must continue to work normally.

---

# Completion Criteria

Requirement 13 is complete when:

1. Ctrl+S performs Save immediately.
2. Ctrl+N performs New immediately.
3. Ctrl+Q performs Close immediately.
4. All three shortcuts work with a physical keyboard.
5. Lowercase/uppercase shortcut reporting does not affect functionality.
6. Shortcut actions do not display the action icons.
7. Shortcut actions do not display confirmation dialogs.
8. Shortcut actions do not display notifications or popups.
9. Auto-save occurs every 1 minute.
10. Auto-save is completely silent.
11. Auto-save uses the existing Save logic.
12. Auto-save does not create unwanted empty files.
13. Auto-save correctly follows the active writing session.
14. Auto-save stops when the session is closed.
15. Existing file naming and file-location behavior is unchanged.
16. Requirement 11 icon actions continue to work.
17. Requirement 12 full-screen mode remains unchanged.
18. Existing fade, typography, cursor, and text-input behavior remains
    unchanged.
19. No unrelated functionality is changed.

---

# Required Source-Code Comments

Add concise comments explaining:

1. The keyboard shortcut mappings:
   Ctrl+S → Save
   Ctrl+N → New
   Ctrl+Q → Close

2. That keyboard shortcuts intentionally invoke the same underlying actions
   as the corresponding UI icons.

3. That auto-save intentionally runs silently every 1 minute.

---

# After Implementation

Provide a short summary explaining:

1. How Ctrl+S, Ctrl+N and Ctrl+Q are implemented.
2. Which Android/Compose keyboard mechanism is being used.
3. How shortcut actions share the existing icon action logic.
4. How the 1-minute auto-save timer is implemented.
5. How auto-save is kept completely silent.
6. How auto-save is associated with the active writing session.
7. How auto-save is stopped when the session is closed.
8. Which files were changed.
9. Confirmation that no unrelated functionality was changed.

Stop after implementing this requirement.