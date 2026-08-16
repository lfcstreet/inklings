# Requirement 15 — Countdown Timer

## Objective

Add a countdown timer to the writing application.

The timer is intended to support focused writing sessions while remaining
visually unobtrusive and consistent with the existing distraction-free
interface.

Add a **fifth action button** for the timer.

The existing four action buttons remain:

```
Save
New
Close
Settings
```

The new timer button is:

```
Timer
```

The Timer button is positioned on the **right side, centered vertically** in
the writing area.

---

# 1. Timer Action Button

Add a fifth action button on the right side of the writing area.

The timer button should have the same general visual design as the existing
action buttons.

Use:

* Rounded-square action-button treatment.
* Same general size as the other action buttons.
* Same theme treatment.
* Appropriate vector icon.
* No text label.

The Timer button is visually distinct because it represents a running
countdown.

---

# 2. Timer Icon — Stopped State

When the timer is not running, display:

**A play icon inside a circle.**

Conceptually:

```
◯▶
```

The play symbol should be a clean vector icon.

Do not use an emoji.

The circular element is important because it will become the timer's visual
progress indicator when the timer is running.

---

# 3. Timer Icon — Running State

When the timer is running:

* Change the play icon to a **pause icon**.
* Keep the circular timer indicator visible.
* The circular indicator should visually represent the remaining time.

Conceptually:

```
◯Ⅱ
```

The transition from Play to Pause should happen immediately when the timer
starts.

---

# 4. Timer Icon — Paused State

When the timer is paused:

* Display the play icon again.
* Preserve the remaining time.
* Preserve the current countdown position.
* Do not reset the timer.

Tapping the button again resumes from the remaining time.

Example:

```
25:00
  ↓
Start
  ↓
24:31
  ↓
Pause
  ↓
24:31
  ↓
Play
  ↓
24:30 ...
```

---

# 5. Timer Toggle Behavior

The Timer button is a toggle:

### First tap

If the timer is stopped or paused:

```
Start/resume timer
```

Change:

```
Play → Pause
```

### Second tap

If the timer is running:

```
Pause timer
```

Change:

```
Pause → Play
```

The selected duration is not changed by tapping the button.

---

# 6. Long Press Reset

A **long press** on the Timer button resets the timer.

Reset means:

* Stop the countdown.
* Return the remaining time to the currently selected duration.
* Return the button to the Play state.
* Reset the circular progress indicator to 100%.
* Remove any running/blinking timer animation.
* Do not change the configured timer duration.

Example:

If Settings contains:

```
Timer Duration = 30 minutes
```

and the timer currently shows:

```
17:42
```

then a long press resets it to:

```
30:00
```

The timer remains stopped after the reset.

---

# 7. Timer Duration Setting

Add a new setting to the existing Settings panel.

The Settings panel now contains:

```
Typewriter Sounds    [ON/OFF]

Timer Duration       [30 minutes ▼]
```

The timer duration must be selectable from:

```
1 minute
10 minutes
15 minutes
30 minutes
45 minutes
60 minutes
```

Use a dropdown/select control appropriate for Android.

Do not allow arbitrary durations.

---

# 8. Default Timer Duration

The default timer duration should be:

```
30 minutes
```

unless an existing project preference framework requires another default.

---

# 9. Persist Timer Duration

The selected Timer Duration must persist across:

* Closing the application.
* Reopening the application.
* Creating a new writing session.

For example, if the user selects:

```
45 minutes
```

then closes and reopens the application, the setting should still be:

```
45 minutes.
```

Changing the setting does not automatically start the timer.

---

# 10. Changing Duration While Timer Is Stopped

If the timer is stopped and the user changes the Timer Duration in Settings:

Immediately use the newly selected duration as the timer's duration.

For example:

```
Current setting: 30 minutes
Timer stopped:   30:00
```

Change setting to:

```
45 minutes
```

The timer should now show:

```
45:00
```

The timer remains stopped.

---

# 11. Changing Duration While Timer Is Running

Do not unexpectedly alter an active countdown.

If the timer is currently running and the user changes the configured Timer
Duration:

* Do not silently restart the active timer.
* Do not jump the active countdown to the new duration.

The newly selected duration should become the duration used by the next reset
or next newly started timer session.

If the application needs to close the Settings panel after changing the
duration, that is acceptable.

---

# 12. Circular Progress Indicator

When the timer is running, the circle around the timer icon becomes a
**circular progress indicator**.

At the beginning:

```
100% of the circle is visible.
```

As time passes:

```
The visible circular progress decreases.
```

At the end:

```
0% remains.
```

The progress must correspond continuously to the percentage of time remaining.

For example, approximately halfway through a 30-minute timer, approximately
50% of the circular progress should remain.

---

# 13. Direction of Circular Progress

The circular progress should reduce smoothly in a consistent direction.

Use a standard circular countdown/progress treatment.

Do not make the indicator jump between arbitrary positions.

The progress should update continuously or sufficiently smoothly that it
appears continuous to the user.

---

# 14. Remaining Time Display

While the timer is running, display the remaining time somewhere near the
timer button.

The remaining time should be clearly readable without distracting from the
writing.

Preferred format:

```
MM:SS
```

Examples:

```
29:58
15:42
01:07
00:03
```

For the 60-minute setting:

```
60:00
```

Use a compact display.

Do not place a large timer in the middle of the writing area.

The remaining-time display should remain visually subordinate to the writing
area.

---

# 15. Timer Animation

While the timer is running, the timer indicator may use a subtle visual
animation.

The animation should communicate that the timer is active.

A **subtle blinking/pulsing color animation** may be used.

Requirements:

* It must remain unobtrusive.
* It must not distract from typing.
* It must not cause noticeable performance problems.
* It must not make the entire writing area blink.
* Only the timer indicator and/or its immediate visual elements should
  animate.

The animation should become more noticeable as the timer approaches zero if
appropriate.

Do not make the normal running state excessively flashy.

---

# 16. Near-Completion State

When the remaining time becomes very small, the timer may increase the visual
urgency of the timer indicator.

For example, the circular indicator can blink/pulse more noticeably during
the final few seconds.

Keep this subtle enough that it does not interfere with writing.

Do not use a full-screen warning.

Do not show a dialog.

---

# 17. Timer Completion

When the countdown reaches:

```
00:00
```

the timer is finished.

Immediately:

* Stop the countdown.
* Change the timer back to the Play state.
* Stop the circular countdown.
* Stop the running animation.
* Keep the timer at `00:00` until reset or restarted.

---

# 18. Completion Flash

When the timer reaches zero, the text currently being written should flash
**three times**.

The purpose is to provide a clear visual indication that the writing session
has ended.

The flash should affect the writing text area, not the entire application
screen.

Implement exactly three flashes.

Conceptually:

```
Normal
Flash
Normal
Flash
Normal
Flash
Normal
```

The flash should be brief and visually clear.

Do not make it excessively bright or distracting.

---

# 19. What Counts as "Text Being Written"

The completion flash should affect the writing content/text area.

If there is text currently present:

```
Flash the writing text three times.
```

If the document is empty:

```
Do not create artificial text or visual content solely for the flash.
```

The timer should still complete normally.

---

# 20. No Sound at Timer Completion

Do not add a new timer-specific audio file.

The timer completion should use the visual three-flash indication only.

Do not play:

* A beep.
* A notification sound.
* A typewriter sound.

The existing Typewriter Sounds setting remains unrelated to timer completion.

---

# 21. Timer and Writing

The timer is independent of whether the user is currently typing.

The timer should continue counting down while:

* The user is typing.
* The user is temporarily idle.
* The user scrolls.
* The user moves the cursor.
* The user interacts with the writing area.

The timer measures elapsed wall-clock time.

Do not pause the timer merely because the user stops typing.

---

# 22. Timer and Application Backgrounding

The timer should use elapsed time rather than simply decrementing a counter
only while the UI is actively rendering.

If the application is temporarily backgrounded, the timer should correctly
account for elapsed time when the application returns to the foreground.

Do not allow the timer to gain or lose significant time merely because the
screen was temporarily not rendering.

If Android terminates the application completely, normal application restart
behavior may apply; do not introduce a complex background timer service as
part of this requirement.

---

# 23. Timer and New

Pressing **New** should create the new writing session as currently defined.

The timer should not automatically start.

The timer should be reset to the currently selected Timer Duration for the
new writing session.

Example:

```
Timer Duration = 30 minutes
```

Current timer:

```
12:15
```

Press New.

New session:

```
Timer = 30:00
State = stopped
```

The user must explicitly press the Timer Play button to start it.

---

# 24. Timer and Close

Pressing **Close** should behave exactly as currently defined.

The timer must not prevent the document from being closed.

No confirmation dialog should be introduced solely because the timer is
running.

---

# 25. Timer and Save

Pressing **Save** should not affect the timer.

The timer continues running if it was running.

Save should not:

* Pause the timer.
* Reset the timer.
* Restart the timer.

---

# 26. Timer and Auto-Save

The existing automatic save behavior remains unchanged.

Auto-save must not:

* Pause the timer.
* Reset the timer.
* Restart the timer.
* Produce a timer notification.

---

# 27. Timer and Settings

Opening Settings must not pause or reset the timer.

The timer should continue running while the Settings panel is open.

Changing the configured duration while the timer is running must follow the
rules in Section 11.

---

# 28. Timer Button Visibility

The Timer button follows the same general display behavior as the existing
action buttons.

It should appear/disappear according to the established action-button
visibility behavior.

The timer button is positioned independently on the **right side center**.

It should not be placed alongside the Save/New/Close/Settings group.

---

# 29. Action Button Layout

The layout should therefore conceptually be:

```
LEFT / TOP ACTION AREA

[Save] [New] [Close] [Settings]


                   [ Timer ]
                   [  ◯▶  ]
                   [ 30:00 ]
```

The exact positioning should be responsive rather than hard-coded to one
screen size.

The Timer button should remain vertically centered on the right side of the
writing area.

---

# 30. Large and Small Screens

The timer must work on different Android screen sizes and aspect ratios.

It must work correctly on:

* Small Android phones.
* Larger Android tablets.
* The Xiaomi Pad / similar tablets.
* The Boox T20-class large screen.

Do not use fixed screen coordinates.

Use responsive layout constraints.

The timer must not overlap:

* Writing text.
* Cursor.
* Existing action buttons.
* Settings panel.

---

# 31. Light and Dark Themes

The timer must follow the existing application theme.

The circular indicator, play/pause icon, remaining-time text, and animation
must remain clearly visible in both:

* Light theme.
* Dark theme.

Do not hard-code colors that only work in one theme.

Use the application's existing theme/color system where possible.

---

# 32. Timer State Model

The timer should have these states:

```
STOPPED / READY
RUNNING
PAUSED
COMPLETED
```

Expected behavior:

### STOPPED / READY

* Play icon.
* Full circular progress.
* Selected duration displayed.
* No countdown running.

### RUNNING

* Pause icon.
* Circular progress decreases.
* Remaining time displayed.
* Optional subtle animation active.

### PAUSED

* Play icon.
* Circular progress remains at current position.
* Remaining time remains unchanged.
* No countdown occurs.

### COMPLETED

* Play icon.
* Timer shows `00:00`.
* Progress is empty.
* Running animation stops.
* Three-flash completion indication occurs once.

A long press from any applicable non-running state should reset the timer to
the configured duration.

---

# 33. Long Press Detection

A normal tap must perform the Play/Pause toggle.

A long press must perform Reset.

Do not trigger both actions for a long press.

For example:

```
Long press
```

must NOT:

```
Pause → Reset
```

It should simply reset the timer to the configured duration.

---

# 34. Timer Precision

The timer should use an appropriate monotonic/elapsed-time mechanism rather
than relying solely on UI frame updates.

The displayed countdown should remain accurate.

Small display/update variations of approximately one second are acceptable,
but the timer must not noticeably drift.

---

# 35. No Unnecessary Notifications

Do not show:

* Toast notifications.
* Dialogs.
* Confirmation messages.
* System notifications.

when:

* Starting the timer.
* Pausing the timer.
* Resuming the timer.
* Resetting the timer.
* Changing the duration.

The visual timer itself communicates the state.

The three-flash effect is the only completion notification.

---

# 36. Interaction With Existing Keyboard Shortcuts

Existing shortcuts remain unchanged:

```
Ctrl+S → Save
Ctrl+N → New
Ctrl+Q → Close
```

Do not add a keyboard shortcut for starting or stopping the timer as part of
this requirement.

Keyboard shortcuts should not interfere with timer operation.

---

# 37. No Changes to Existing Writing Behavior

Do NOT modify:

* Font.
* Font size.
* Letter spacing.
* Word spacing.
* Line spacing.
* Margins.
* Cursor positioning.
* Fade behavior.
* Sentence behavior.
* File naming.
* File location.
* Save behavior.
* New behavior.
* Close behavior.
* Auto-save.
* Writing-time tracking.
* Typewriter sounds.
* Capitalization.
* Keyboard shortcuts.

This requirement adds the countdown timer only.

---

# Testing

## Test 1 — Default State

Launch the application.

Verify:

```
Timer = 30:00
State = stopped
Icon = Play
Circular progress = 100%
```

---

## Test 2 — Start

Tap the Timer button once.

Verify:

```
Icon → Pause
Timer starts counting down
Circular progress begins decreasing
```

---

## Test 3 — Pause

While running, tap the Timer button again.

Verify:

```
Icon → Play
Countdown stops
Remaining time is preserved
Circular progress is preserved
```

---

## Test 4 — Resume

Tap the Timer button again.

Verify that the countdown resumes from exactly the remaining time.

---

## Test 5 — Long Press Reset

Start the timer.

Allow it to reach, for example:

```
27:31
```

Long press the Timer button.

Verify:

```
Timer → 30:00
Icon → Play
Progress → 100%
Timer is stopped
```

---

## Test 6 — Duration Setting

Open Settings.

Change:

```
30 minutes → 45 minutes
```

Verify that the setting is stored.

Reset the timer.

Verify:

```
45:00
```

---

## Test 7 — All Durations

Verify that the dropdown contains exactly:

```
1 minute
10 minutes
15 minutes
30 minutes
45 minutes
60 minutes
```

No other durations are required.

---

## Test 8 — Persistence

Select:

```
45 minutes
```

Close the application.

Reopen it.

Verify:

```
Timer Duration = 45 minutes
```

---

## Test 9 — Completion

Use the 1-minute timer for testing.

Start it and allow it to reach:

```
00:00
```

Verify:

* Timer stops.
* Play icon is displayed.
* Circular progress is empty.
* Exactly three text-area flashes occur.
* No dialog appears.
* No notification appears.
* No sound is played.

---

## Test 10 — Empty Document Completion

Start the timer with an empty document.

Allow it to reach zero.

Verify:

* Timer completes normally.
* No artificial text appears.
* No crash occurs.

---

## Test 11 — New

Start a timer.

Press New.

Verify:

```
New writing session
Timer reset to selected duration
Timer stopped
```

---

## Test 12 — Save

Start the timer.

Press Save.

Verify:

* File is saved normally.
* Timer continues running.
* Timer is not reset.

---

## Test 13 — Auto-Save

Start the timer.

Allow auto-save to occur.

Verify:

* Auto-save works normally.
* Timer continues running.
* Timer is not reset.

---

## Test 14 — Settings While Running

Start the timer.

Open Settings.

Verify:

* Timer continues running.
* Settings can be used normally.

---

## Test 15 — Physical Keyboard

Use a physical keyboard while the timer is running.

Verify:

* Typing works normally.
* Timer continues independently.
* Keyboard shortcuts continue to work.
* Timer does not interfere with text input.

---

## Test 16 — Large Screen

Test on the large tablet.

Verify:

* Timer remains on the right side center.
* Timer does not overlap writing.
* Timer does not overlap action buttons.
* Circular indicator scales correctly.
* Remaining time remains readable.

---

## Test 17 — Small Screen

Test on a smaller Android screen.

Verify the same behavior.

---

## Test 18 — Light/Dark Theme

Test in both light and dark themes.

Verify:

* Timer is clearly visible.
* Progress indicator is clearly visible.
* Play/pause icon is clearly visible.
* Remaining time is readable.
* Animation remains appropriate.

---

# Completion Criteria

Requirement 15 is complete when:

1. A fifth Timer action button exists.
2. It is positioned on the right side center.
3. It follows the established action-button visibility behavior.
4. It uses a play icon inside a circular element when stopped.
5. Tapping it starts the timer.
6. The icon changes to Pause while running.
7. Tapping it while running pauses the timer.
8. Tapping it while paused resumes the timer.
9. Long pressing resets the timer.
10. Reset returns it to the currently selected duration.
11. A circular progress indicator decreases as time passes.
12. The circular progress corresponds to remaining time.
13. Remaining time is displayed in MM:SS format.
14. A subtle running animation is supported.
15. The animation does not interfere with writing.
16. The timer duration is configurable in Settings.
17. The available durations are exactly 1, 10, 15, 30, 45 and 60 minutes.
18. The default duration is 30 minutes.
19. The selected duration persists across application restarts.
20. Changing the duration while stopped updates the timer's duration.
21. Changing the duration while running does not silently alter the active
    countdown.
22. Timer completion occurs at 00:00.
23. The timer stops at completion.
24. Exactly three flashes occur in the writing text area at completion when
    text exists.
25. No completion dialog is shown.
26. No completion notification is shown.
27. No timer-specific sound is played.
28. The timer continues independently of typing activity.
29. The timer correctly accounts for elapsed time when the application
    temporarily leaves the foreground.
30. New resets the timer to the configured duration and leaves it stopped.
31. Save does not affect the timer.
32. Auto-save does not affect the timer.
33. Settings does not pause the timer.
34. Existing keyboard shortcuts remain unchanged.
35. Existing writing behavior remains unchanged.
36. The timer works on small and large Android screens.
37. The timer works in light and dark themes.
38. No unrelated functionality is changed.

---

# Required Source-Code Comments

Add concise comments explaining:

1. The Timer state model.
2. How elapsed time is calculated.
3. Why the timer uses elapsed/monotonic time rather than relying solely on
   UI frame updates.
4. How the circular progress represents remaining time.
5. How the Play/Pause toggle works.
6. How long-press Reset is distinguished from a normal tap.
7. How the configured duration is persisted.
8. How changing the duration while a timer is running is handled.
9. How timer completion triggers exactly three flashes.
10. That timer completion intentionally has no sound or dialog.
11. That the timer operates independently of typing activity.

---

# After Implementation

Provide a short summary explaining:

1. Which files were changed.
2. Where the Timer button was added.
3. How the Play/Pause toggle works.
4. How long-press Reset works.
5. How the circular progress indicator works.
6. How remaining time is displayed.
7. How the timer duration is configured in Settings.
8. How the duration is persisted.
9. How timer completion is handled.
10. How the three text flashes are implemented.
11. How the timer behaves with New, Save and Auto-Save.
12. How the timer behaves when the application temporarily leaves the
    foreground.
13. How the timer scales across different screen sizes.
14. Confirmation that no unrelated functionality was changed.

Stop after implementing this requirement.
