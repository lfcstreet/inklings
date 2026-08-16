# Requirement 10D — Automatic Capitalization After Double-Space Period

## Objective

Extend the existing double-space punctuation behavior.

The application already converts:

    two consecutive spaces

into:

    ". "

This requirement adds automatic capitalization of the first letter typed
after that newly created sentence ending.

---

# 1. Existing Behavior — KEEP

The existing double-space behavior must remain unchanged.

When the user types two consecutive spaces:

    [space][space]

the application automatically converts them to:

    .[space]

For example:

    I went home[space][space]

becomes:

    I went home.

with the cursor positioned after the automatically inserted space:

    I went home. |

---

# 2. New Behavior

After the application automatically creates:

    .[space]

the next alphabetic character typed by the user should automatically be
capitalized.

Example:

    User types:

    I went home  th

The existing behavior produces:

    I went home. th

The new behavior must produce:

    I went home. Th

The user does NOT need to press Shift.

---

# 3. Only the First Character

Automatic capitalization applies only to the first alphabetic character
typed after the automatically inserted:

    .[space]

Once that character has been entered, normal typing resumes.

Example:

    User types:

    i went home  th

Result:

    i went home. Th

If the user continues:

    i went home. This is good.

the remaining characters are entered normally.

Do NOT automatically capitalize every word.

---

# 4. Only Apply to the Automatically Created Sentence Ending

The capitalization behavior must be associated specifically with the
existing double-space transformation.

It must NOT automatically capitalize text merely because:

- The user manually typed ". "
- The user moved the cursor after an existing period.
- The user inserted text somewhere else.
- The user pasted text.
- The user pressed Enter.
- The user started typing after scrolling.
- The user moved the cursor to another sentence.

This requirement is specifically for:

    double-space
        ↓
    automatic ". "
        ↓
    next alphabetic character
        ↓
    capitalize

---

# 5. Alphabetic Character

The capitalization should occur when the next character typed is an
alphabetic character.

For example:

    .  t
       ↓
    .  T

If the user enters another non-alphabetic character first, do not blindly
capitalize that character.

Do not introduce unwanted punctuation or text.

---

# 6. User Corrections

The user must remain able to edit the automatically capitalized character
normally.

For example:

    I went home. This

The user can press Backspace and replace/edit the character normally.

Do not continuously force the character to uppercase after the user has
manually edited it.

---

# 7. Existing Sentence Detection

Do not modify the existing sentence-detection logic used by the fade
functionality.

This requirement only affects text input immediately following the existing
double-space punctuation transformation.

The sentence-based fade and line-based fade behavior remain unchanged.

---

# 8. Interaction With Fade

Do not change any fade behavior.

After the new sentence begins, the existing sentence-based visibility logic
should continue to determine:

- Current sentence → 100%
- Previous sentence → approximately 30%
- Succeeding sentence → approximately 30%
- Same-line exception → as defined in Requirement 10C-FIX-01
- Outer lines → existing line-based fade

---

# 9. Interaction With Cursor Movement

If the user moves the cursor away from the position immediately following
the automatically inserted ". ":

    Do not apply the pending capitalization to unrelated typing.

The capitalization behavior belongs to the specific typing sequence directly
following the automatic double-space transformation.

---

# 10. Interaction With New / Save / Close

Do not change:

- SAVE
- NEW
- CLOSE
- Auto-save
- File creation
- File naming
- Writing-time tracking
- Keyboard shortcuts

This requirement is only about text input.

---

# Testing

## Test 1 — Basic Case

Type:

    I went home[space][space]this

Expected:

    I went home. This

---

## Test 2 — Capitalization Only Once

Type:

    I went home[space][space]this is good

Expected:

    I went home. This is good

Do not produce:

    I Went Home. This Is Good

---

## Test 3 — Multiple Sentences

Type:

    This is sentence one[space][space]this is sentence two[space][space]this
    is sentence three

Expected:

    This is sentence one. This is sentence two. This is sentence three

---

## Test 4 — Existing Manual Period

Type a manually created:

    . 

then type a lowercase letter.

Verify that the application does NOT automatically capitalize it.

The new behavior must be tied to the existing double-space transformation.

---

## Test 5 — Cursor Movement

Create:

    First sentence. second sentence.

Move the cursor elsewhere and type.

Verify that automatic capitalization does not unexpectedly occur.

---

## Test 6 — Backspace / Editing

Create:

    First sentence. Second sentence.

Move back and edit the second sentence.

Verify that the application does not continuously force the first character
to uppercase.

---

## Test 7 — Existing Double-Space Behavior

Verify that the existing transformation:

    [space][space] → .[space]

continues to work exactly as before.

---

# Completion Criteria

Requirement 10D is complete when:

1. Existing double-space → ". " behavior continues to work.
2. The first alphabetic character typed immediately after the automatically
   inserted ". " is automatically capitalized.
3. Only that first character is affected.
4. Subsequent typing behaves normally.
5. Manually typed ". " does not trigger this behavior.
6. Cursor movement does not cause unexpected capitalization.
7. Editing/backspacing does not cause continuous forced capitalization.
8. Existing fade behavior is unchanged.
9. Existing save/new/close behavior is unchanged.
10. No unrelated functionality is changed.

---

# After Implementation

Provide a short summary explaining:

1. How the application detects that ". " was created by the double-space
   transformation.
2. How the pending capitalization state is tracked.
3. How the first alphabetic character is capitalized.
4. How the capitalization state is cleared after the first character.
5. How cursor movement and editing are handled.
6. Which files were changed.

Stop after implementing this requirement.