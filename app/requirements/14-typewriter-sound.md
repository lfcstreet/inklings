# Requirement 14 — Typewriter Sounds and Settings

## Objective

Add an optional typewriter sound effect to the writing experience.

Three audio files have been added to the project:

```
assets/typewriterKS1.wav
assets/typewriterKS2.wav
assets/typewriterSPACE.wav
```

Use these existing files. Do not create, download, replace, or regenerate
the audio files.

The sound behavior is:

* Normal keys → randomly use `typewriterKS1.wav` or `typewriterKS2.wav`
* Space bar → always use `typewriterSPACE.wav`
* Backspace → always use `typewriterKS2.wav`

This should make normal typing sound more like a real typewriter while
giving the space bar and backspace their own distinct sounds.

Also add a fourth action button called **Settings** that opens a small
settings panel where the user can turn the typewriter sounds ON or OFF.

---

# 1. Existing Audio Files

The following files already exist:

```
assets/typewriterKS1.wav
assets/typewriterKS2.wav
assets/typewriterSPACE.wav
```

Use these files as the source audio.

The implementation should place/package them in the appropriate Android
resource location required by the project's existing architecture.

Do not modify the WAV files.

Do not create additional audio files.

---

# 2. Normal Keys

For normal character/key input, randomly select between:

```
typewriterKS1.wav
typewriterKS2.wav
```

This applies to:

* Letters
* Numbers
* Punctuation
* Symbols

Do not use `typewriterSPACE.wav` for these keys.

---

# 3. Randomized Normal-Key Selection

Do NOT always use the same sound for normal keys.

For each applicable normal key action, randomly select between:

```
typewriterKS1.wav
typewriterKS2.wav
```

Use a simple anti-repetition rule:

* Randomly select KS1 or KS2.
* Do not allow the same sound more than 2 consecutive times.
* If the randomly selected sound would create a third consecutive identical
  sound, force the other sound.

For example:

```
KS1 KS2 KS1 KS1 KS2 KS2 KS1
```

is acceptable.

But:

```
KS1 KS1 KS1
```

is NOT acceptable.

Do not implement complicated audio randomization.

---

# 4. Space Bar

When the user enters a space, always play:

```
assets/typewriterSPACE.wav
```

Do NOT randomly select KS1 or KS2 for spaces.

There should be exactly one space-bar sound for each actual Space key press.

This applies to both:

* Physical keyboard
* Android on-screen keyboard

---

# 5. Backspace

When the user presses Backspace and an actual deletion occurs, always play:

```
assets/typewriterKS2.wav
```

Do NOT randomly select KS1 or KS2 for Backspace.

Do NOT use `typewriterSPACE.wav` for Backspace.

This applies to both:

* Physical keyboard
* Android on-screen keyboard

If Backspace is pressed when there is nothing to delete, do not play a
sound because no actual deletion occurred.

---

# 6. Actual Text Editing Actions Only

Do NOT play sounds for actions that do not represent actual typing or
deletion.

No sound for:

* Cursor movement
* Arrow keys
* Home
* End
* Page Up
* Page Down
* Scrolling
* Text selection
* Copy
* Paste
* Cut
* Tapping the writing area
* Opening Settings
* Closing Settings
* Save
* New
* Close

The sound should represent actual text insertion or actual deletion.

---

# 7. Paste

Pasting text should NOT produce one typewriter sound per pasted character.

For example, if the user pastes:

```
This is a large block of text.
```

do not play multiple typewriter sounds.

Paste should be silent.

The same applies to programmatic insertion of text that is not caused by
actual typing.

---

# 8. Double-Space → ". " Feature

The existing double-space behavior remains unchanged.

When:

```
Space + Space
```

is converted by the application into:

```
. + Space
```

the sound behavior should represent the user's actual key actions without
creating artificial duplicate sounds because of the internal text
transformation.

For example, two actual Space key presses should produce:

```
typewriterSPACE.wav
typewriterSPACE.wav
```

If the application internally replaces those spaces with:

```
. + Space
```

do NOT additionally play a sound merely because the application inserted or
replaced the period.

The audio should correspond to the user's actual interaction rather than
every internal text mutation.

---

# 9. Capitalization

The sound feature is completely independent of Requirement 10D-FIX-02.

Do NOT modify capitalization behavior as part of this requirement.

The sounds must work with both:

* Android IME input
* Physical keyboard input

---

# 10. Physical Keyboard

Verify sound behavior with a physical keyboard.

Examples:

```
A → random KS1 or KS2
Space → typewriterSPACE.wav
Backspace → typewriterKS2.wav
```

Do not depend on Gboard or any other specific keyboard.

Keyboard shortcuts from Requirement 13 must NOT trigger sounds.

For example:

```
Ctrl+S
```

must save silently.

The same applies to:

```
Ctrl+N
Ctrl+Q
```

---

# 11. Android On-Screen Keyboard

Verify sound behavior with the Android software keyboard.

Typing a character should produce either KS1 or KS2.

Space should always produce:

```
typewriterSPACE.wav
```

Backspace should always produce:

```
typewriterKS2.wav
```

Do not implement keyboard-specific logic for Gboard.

Use the normal Android/Compose text-input mechanisms.

---

# 12. Settings Action Button

Add a fourth action button:

```
Settings
```

The action-button set is now:

```
Save
New
Close
Settings
```

The Settings button must be an icon, not a text button.

Use a simple **gear/cog icon**.

The icon should visually match the existing Save/New/Close action buttons.

---

# 13. Settings Icon Design

The Settings icon should follow the existing action-button visual design.

Use:

* A rounded-square action button.
* The same overall dimensions as Save/New/Close.
* The same positioning and spacing as the existing action buttons.
* A simple, clean gear/cog vector icon.
* Light-blue theme treatment consistent with the existing action icons.
* No text label.
* No emoji.
* No PNG required for the Settings icon.

Use an Android vector drawable / appropriate Compose vector icon so that it
scales cleanly across different screen sizes and resolutions.

The icon must remain visually appropriate in both light and dark themes,
following the application's existing theme colors.

Do not introduce a completely new visual style for Settings.

---

# 14. Settings Button Behavior

When the user taps Settings:

```
Open the Settings panel.
```

The panel should be a small, unobtrusive box/panel rather than a full-screen
settings page.

The Settings button itself must NOT produce a typewriter sound.

---

# 15. Settings Panel

The panel should contain only:

```
Typewriter Sounds    [ON/OFF]
```

Use an appropriate Android toggle/switch control.

Do not add additional settings.

The panel should visually follow the existing application theme.

---

# 16. Settings Panel Size and Appearance

Keep the Settings panel compact.

It should not take over the entire screen.

It should appear naturally in relation to the action-button area.

The user should still be able to clearly see that this is a small settings
control rather than a separate screen.

Use the same general rounded-corner and theme treatment already established
for the application.

---

# 17. Master Sound Toggle

The setting is a single master switch controlling all three audio files.

When ON:

```
typewriterKS1.wav    → enabled
typewriterKS2.wav    → enabled
typewriterSPACE.wav  → enabled
```

When OFF:

```
All three sounds disabled.
```

Do not create separate toggles for individual sounds.

---

# 18. Default Setting

The default state should be:

```
ON
```

unless the project already has an established preference framework requiring
a different default.

On first installation, typewriter sounds should normally be enabled.

---

# 19. Persist the Setting

The ON/OFF preference must persist across:

* Closing the application
* Reopening the application
* Creating a new writing session

If the user turns sounds OFF and later reopens the application, sounds should
remain OFF.

Use an appropriate Android persistent preference mechanism already suitable
for the project.

Do not create a text file solely to store this preference.

---

# 20. Settings Panel Close Behavior

The Settings panel should close when the user taps outside the panel.

It should also close when the user taps the Settings button again, if
consistent with the existing action-button behavior.

Do not create a separate full-screen settings page.

---

# 21. Sound Playback Performance

Audio playback must have very low latency.

The sound should occur essentially at the same time as the corresponding
typing action.

Do NOT:

* Block text input while playing audio.
* Load the WAV file from storage for every keystroke.
* Wait for one sound to finish before processing the next key.

Load/prepare the three sound resources appropriately so that rapid typing
remains responsive.

---

# 22. Rapid Typing

The application must remain completely responsive while typing rapidly.

Verify:

* No input lag.
* No dropped characters.
* No cursor lag.
* No application freeze.
* No delayed text insertion.

The audio system must never take priority over text input.

If necessary, allow sound playback to overlap slightly rather than delaying
the typing experience.

---

# 23. Audio Synchronization

The sound should be triggered as close as reasonably possible to the actual
key event.

Do not build a queue that allows sounds to accumulate.

For example, if the user types rapidly:

```
abcdefghijklmnopqrstuvwxyz
```

the sounds should occur approximately with the typing.

They should not continue playing several seconds after the user has stopped.

---

# 24. Audio Resource Management

Use an Android audio mechanism appropriate for short, low-latency sound
effects.

The three WAV files should be loaded/prepared once where practical rather
than repeatedly loading them for every keystroke.

Avoid creating a large number of audio-player objects for every key.

Release audio resources appropriately when the application is no longer
using them.

---

# 25. Device Audio Settings

Respect normal Android audio behavior.

Do not:

* Change the device volume.
* Automatically increase the volume.
* Override system audio controls.
* Force sound when the device configuration suppresses audio.

The application should behave as a normal Android application with respect
to audio.

---

# 26. Full-Screen Mode

Requirement 12 remains unchanged.

The sound feature must work correctly while the application is in immersive
full-screen mode.

The Settings panel must remain usable in full-screen mode.

---

# 27. Action Button Visibility

Requirement 11 remains the basis for action-button behavior.

The four buttons are now:

```
Save
New
Close
Settings
```

They should appear according to the existing tap behavior.

When the user starts typing, they should disappear according to the existing
Requirement 11 behavior.

Do not redesign Save/New/Close as part of this requirement.

---

# 28. Keyboard Shortcuts

Requirement 13 remains unchanged.

The following continue to work:

```
Ctrl+S → Save
Ctrl+N → New
Ctrl+Q → Close
```

These shortcuts must not produce typewriter sounds.

Do not add a keyboard shortcut for Settings as part of this requirement.

---

# 29. No Changes to Existing Writing Behavior

Do NOT modify:

* Font
* Font size
* Letter spacing
* Word spacing
* Line spacing
* Margins
* Cursor positioning
* Fade behavior
* Sentence behavior
* File naming
* File location
* Save behavior
* New behavior
* Close behavior
* Auto-save
* Writing-time tracking
* Keyboard shortcuts
* Capitalization

This requirement adds optional audio and the Settings control only.

---

# Testing

## Test 1 — Normal Characters

Type:

```
abcdef
```

Verify that each character produces either KS1 or KS2.

Verify that both KS1 and KS2 are being used over multiple keystrokes.

---

## Test 2 — Anti-Repetition

Type a long sequence of normal characters.

Verify that the same normal-key sound is never used more than twice
consecutively.

Example:

```
KS1 KS2 KS1 KS1 KS2 KS2 KS1
```

is acceptable.

Example:

```
KS1 KS1 KS1
```

is NOT acceptable.

---

## Test 3 — Space

Press Space.

Expected:

```
typewriterSPACE.wav
```

Verify that KS1 and KS2 are never used for a Space.

---

## Test 4 — Backspace

Type several characters and press Backspace.

Expected:

```
typewriterKS2.wav
```

Verify that KS1 and `typewriterSPACE.wav` are never used for Backspace.

Also press Backspace when there is nothing to delete.

Expected:

```
No sound.
```

---

## Test 5 — Physical Keyboard

Using a physical keyboard verify:

```
Character → random KS1 or KS2
Space → typewriterSPACE.wav
Backspace → typewriterKS2.wav
```

---

## Test 6 — On-Screen Keyboard

Using the Android on-screen keyboard verify the same behavior.

---

## Test 7 — Paste

Paste a block of text.

Verify that the pasted text does not generate one sound per pasted
character.

Paste should be silent.

---

## Test 8 — Double-Space

Test:

```
Space + Space
```

Verify that the existing:

```
. + Space
```

behavior remains correct.

Verify that the two actual Space presses produce two
`typewriterSPACE.wav` sounds and that internal text replacement does not
create extra sounds.

---

## Test 9 — Settings ON

Open Settings.

Verify:

```
Typewriter Sounds = ON
```

Type normally.

Verify that the appropriate sounds play.

---

## Test 10 — Settings OFF

Open Settings.

Turn:

```
Typewriter Sounds = OFF
```

Type characters, spaces and backspaces.

Expected:

```
No sounds.
```

No notification or confirmation should appear.

---

## Test 11 — Persistence

Turn sounds OFF.

Close the application.

Reopen it.

Verify that sounds remain OFF.

Turn them ON again and verify that sounds return.

---

## Test 12 — Settings Panel

Verify:

* Settings opens the panel.
* The panel is small and unobtrusive.
* The ON/OFF control works.
* Tapping outside closes the panel.
* The Settings icon itself produces no sound.
* Writing remains usable.

---

## Test 13 — Keyboard Shortcuts

Test:

```
Ctrl+S
Ctrl+N
Ctrl+Q
```

Verify that none produces a typewriter sound.

---

## Test 14 — Rapid Typing

Type rapidly using a physical keyboard.

Verify:

* No input lag.
* No dropped characters.
* No cursor lag.
* No application freeze.
* Audio does not build up a delayed queue.

---

## Test 15 — Full-Screen

Verify that:

* Sounds work in full-screen mode.
* Settings can be opened.
* The sound toggle works.
* Existing action-button behavior remains unchanged.

---

# Completion Criteria

Requirement 14 is complete when:

1. `assets/typewriterKS1.wav` is used for normal keys.
2. `assets/typewriterKS2.wav` is used for normal keys.
3. `assets/typewriterSPACE.wav` is used for Space.
4. No additional audio files are required.
5. Normal keys randomly use KS1 or KS2.
6. The same normal-key sound is never played more than twice consecutively.
7. Space always uses `typewriterSPACE.wav`.
8. Backspace always uses `typewriterKS2.wav`.
9. Backspace produces no sound when no deletion occurs.
10. Both physical and on-screen keyboards work.
11. Paste does not produce one sound per pasted character.
12. Internal double-space punctuation transformation does not produce
    artificial duplicate sounds.
13. A fourth Settings action icon exists.
14. The Settings icon is a clean gear/cog vector icon.
15. The Settings icon visually matches Save/New/Close.
16. Settings opens a small settings panel.
17. The panel contains one Typewriter Sounds ON/OFF toggle.
18. The default setting is ON.
19. The setting persists across application restarts.
20. Turning sounds OFF immediately disables all three sounds.
21. Turning sounds ON enables all three sounds.
22. Settings itself produces no sound.
23. Sound playback does not block text input.
24. Rapid typing remains responsive.
25. Sounds do not accumulate in a delayed queue.
26. Keyboard shortcuts remain silent.
27. Device volume/system audio settings are respected.
28. Full-screen mode continues to work.
29. Existing Save/New/Close behavior remains unchanged.
30. Existing fade behavior remains unchanged.
31. Existing typography remains unchanged.
32. Existing file and auto-save behavior remains unchanged.
33. Existing capitalization behavior remains unchanged.
34. No unrelated functionality is changed.

---

# Required Source-Code Comments

Add concise comments explaining:

1. Why two different WAV samples are used for normal keys.
2. How the randomized normal-key sound selection works.
3. Why the same normal-key sample is limited to a maximum of two consecutive
   plays.
4. Why the dedicated Space sound is used.
5. Why KS2 is specifically used for Backspace.
6. Why the sounds are preloaded/prepared for low-latency playback.
7. Why audio playback must never block text input.
8. That the sound preference is persisted.
9. That keyboard shortcuts intentionally do not produce sounds.
10. That the audio feature is independent of capitalization and fade logic.
11. That the Settings icon is a vector so it scales correctly across screen
    sizes and themes.

---

# After Implementation

Provide a short summary explaining:

1. Where the three WAV files are being used from.
2. Which Android audio mechanism is being used.
3. How KS1/KS2 are randomized for normal keys.
4. How the anti-repetition rule works.
5. How the dedicated Space sound works.
6. How KS2 is used for Backspace.
7. How low-latency playback is achieved.
8. How paste is handled.
9. How the Settings button was added.
10. How the Settings gear icon was implemented.
11. How the Typewriter Sounds preference is persisted.
12. What the default setting is.
13. How the feature behaves with the physical keyboard.
14. How it behaves with the Android on-screen keyboard.
15. Which files were changed.
16. Confirmation that no unrelated functionality was changed.

Stop after implementing this requirement.
