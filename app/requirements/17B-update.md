# Font Color Selection — Revised

The Project Font Color must support BOTH:

1. Visual color selection using a color wheel / full-spectrum color picker.
2. Manual hexadecimal color entry.

The user must therefore be able to either:

    Pick visually

or enter:

    #RRGGBB

Example:

    #3366CC

Do not limit the user to a small predefined palette.

---

# Base Font Color

The user selects one Base Font Color.

This Base Font Color represents the Project's preferred color identity.

The application must derive theme-safe display variants for:

    Light Theme
    Dark Theme

The user should not normally need to configure two separate colors.

---

# Theme-Aware Font Color

A single raw color may be readable in one theme but nearly invisible in the
other.

Therefore the application must automatically derive an appropriate font color
for each theme.

Conceptually:

    User-selected Base Color
             ↓
       Theme adaptation
        ↙           ↘
Light Theme      Dark Theme
Font Color       Font Color

For example:

    Base:        #3366CC
    Light theme: darker/readable blue
    Dark theme:  lighter/readable blue

The exact values may be calculated dynamically.

---

# Contrast Requirement

The derived font color must have sufficient contrast against the editor
background.

Do not simply use the exact same RGB value in both themes if that results in
poor readability.

Use appropriate luminance/contrast calculations.

Prefer a standard contrast calculation rather than arbitrary hard-coded
brightness offsets.

The color should preserve the visual character/hue of the user's selected
color as much as reasonably possible.

---

# Metadata

Store the user's Base Font Color in:

    Inklings/<Project>/.inklings-project.json

For example:

    {
      "baseFontColor": "#3366CC",
      "isDefault": false
    }

The application may either:

A. derive the light/dark variants dynamically from the base color

or

B. persist the derived variants as metadata

If derived values are persisted, the Base Font Color must remain the source
of truth.

Do not require the user to manage separate Light and Dark theme colors.

---

# Hex Input

The Font Color editor must accept standard six-digit RGB hexadecimal values:

    #RRGGBB

Examples:

    #3366CC
    #AA2244
    #00A060

Validate the input.

Do not accept malformed values silently.

If the user enters an invalid value, keep the previous valid color and show
a simple validation indication.

---

# Color Picker

Provide a visual full-spectrum color picker / color wheel.

The picker should allow much finer choice than a small set of predefined
swatches.

The currently selected Base Font Color should be visible.

Changes made using the picker should update the hex field.

Changes made using the hex field should update the picker.

Both controls represent the same Base Font Color.

---

# Preview

Show a small preview of the selected color.

Preferably show how it will appear in:

    Light Theme
    Dark Theme

For example:

    Light preview: Aa
    Dark preview:  Aa

This allows the user to see how the automatic theme adaptation affects the
chosen color before applying it.

---

# Apply to Editor

When the Project's Base Font Color changes:

- Recalculate theme-safe variants.
- If the current writing session belongs to that Project, update the editor
  immediately.
- Do not modify Markdown content.
- Do not require an app restart.

---

# Theme Change

If Android/app theme changes while the editor is open:

    Light → Dark

or:

    Dark → Light

the application must immediately use the appropriate derived Project font
color.

Do not require the Project to be reopened.