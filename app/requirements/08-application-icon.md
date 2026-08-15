# Requirement 07B — Application Icon

## Objective

Add the supplied application icon to the Android project and configure it
as the application's launcher icon.

The icon has already been created separately.

The source image is located at:

    assets/app-icon.png

Do NOT create a new icon.

Do NOT redesign the supplied icon.

---

## 1. Source Asset

Use:

    assets/app-icon.png

as the authoritative source artwork.

The source PNG must not be modified.

Do not:

- Redraw it
- Change its colors
- Add text
- Add effects
- Add shadows
- Add borders
- Crop the artwork
- Stylize the artwork
- Replace the artwork

Android-specific processing required to create launcher resources is allowed.

---

## 2. Android Launcher Icon

Configure the application so that the supplied artwork is used as the
Android application launcher icon.

The icon should appear:

- On the Android home screen
- In the application launcher/app drawer
- Wherever Android displays the application launcher icon

Use the standard Android launcher/adaptive-icon mechanism appropriate for the
current project.

---

## 3. Adaptive Icon

If the current Android project uses adaptive icons, configure the supplied
artwork appropriately as an adaptive launcher icon.

Ensure that the important artwork remains within the Android adaptive-icon
safe area.

Do not unnecessarily crop the supplied artwork.

Do not add visual effects that are not present in the source image.

If Android requires separate foreground/background resources, create them
from the supplied artwork using the standard Android approach.

---

## 4. Source Image Preservation

Keep:

    assets/app-icon.png

unchanged in the project.

Do not overwrite or replace the source image.

If Android requires additional launcher resources, those resources should be
derived from the source image.

The original PNG remains the master source asset.

---

## 5. Application Configuration

Update the application configuration so that the launcher icon is correctly
referenced.

Use the existing Android project structure and conventions.

Do not make unrelated changes to:

- AndroidManifest.xml
- Gradle configuration
- Application package configuration
- Application name

Only make the changes necessary to configure the launcher icon.

---

## 6. Existing Functionality

Do not change any existing application functionality.

The following must continue to work exactly as before:

- Courier Prime
- 20sp editor text
- Typewriter cursor
- Typewriter scrolling
- Browsing mode
- Distraction-free writing
- One visual line above the cursor
- Current visual line
- One visual line below the cursor
- SAVE
- NEW
- CLOSE
- Markdown file creation
- Markdown file saving
- Rotation/state preservation

This requirement is ONLY about the application icon.

---

## 7. Testing

The Nokia T20 is the primary development and testing device.

After implementing the icon:

1. Build the application successfully.
2. Install the application on the Nokia T20.
3. Verify that the new icon appears in the launcher/app drawer.
4. Verify that the icon looks correct at normal launcher size.
5. Verify that the artwork is not unexpectedly cropped.
6. Launch the application using the icon.
7. Verify that the application itself works normally.

Also verify that the icon is displayed correctly in Android Studio's
application installation/run flow.

---

## 8. Do NOT Implement

Do NOT:

- Generate another icon
- Use an AI image generator
- Redesign the icon
- Modify the supplied PNG
- Change the application name
- Add a splash-screen redesign
- Add an in-app icon
- Add an icon settings screen
- Change unrelated UI
- Add unnecessary dependencies
- Change existing application functionality

---

## Completion Criteria

Requirement 07B is complete when:

1. `assets/app-icon.png` is included in the project.
2. The supplied artwork is used as the launcher icon.
3. The Android launcher icon configuration is correct.
4. Adaptive icon requirements are handled appropriately.
5. The application builds successfully.
6. The application installs successfully on the Nokia T20.
7. The new icon appears in the launcher/app drawer.
8. The artwork is not unexpectedly cropped.
9. The original `assets/app-icon.png` remains unchanged.
10. No existing application functionality has been changed.

---

## After Implementation

Provide a short summary explaining:

1. Where the source icon is located.
2. Which Android resource files were created.
3. How the launcher icon is configured.
4. Whether an adaptive icon is being used.
5. How the source PNG was preserved.
6. Which project files were modified.
7. Confirmation that the application builds successfully.

Do not implement any additional functionality.

Stop after completing this requirement.