# Distraction-Free Markdown Writer

A minimal Android writing application designed to provide a distraction-free,
typewriter-like writing experience.

The application is intentionally simple. It is not intended to be a
general-purpose Markdown editor.

## Target Devices

The application must ultimately work on all three of these devices:

1. Nokia T20
2. Google Pixel 7a
3. BOOX Go 10.3

### Development Device

The Nokia T20 will be the primary development and testing device.

The application should be built and tested on the Nokia T20 throughout
development.

The Pixel 7a and BOOX Go 10.3 will also be tested before the final release.

A release APK will eventually be generated and installed on all three devices
for final testing.

## Technology

- Android
- Kotlin
- Jetpack Compose

Avoid third-party libraries unless they are genuinely necessary.

## Core Concept

The application creates a new writing session when the application starts.

The user can also create a new writing session at any time using the NEW
button.

The user writes Markdown as plain text.

Each session produces:

1. A Markdown file containing the writing.
2. A separate Markdown file containing information about the writing session.

The application does not provide an Open/Reopen function.

To continue writing after a session has ended, the user creates a new session
using the NEW button or by starting the application again.

## Main Actions

The writing screen will eventually provide three actions:

- SAVE
- NEW
- CLOSE

### SAVE

Saves the current Markdown document while keeping the current writing session
open.

### NEW

Finishes the current writing session and immediately starts a new writing
session.

The current session must be saved and its timing information recorded before
the new session begins.

The new session receives a new timestamp-based filename and starts with the
configured main template.

### CLOSE

Finishes the current writing session, saves it, records its timing
information, and exits the writing screen/application.

There is no Open/Reopen function.

## Planned Features

- [x] 01 - Basic writing screen
- [ ] 02 - Courier Prime font
- [ ] 03 - Typewriter-style cursor positioning
- [ ] 04 - Hide lines more than two visual lines above cursor
- [ ] 05 - Markdown file creation
- [ ] 06 - Timestamp-based filenames
- [ ] 07 - Markdown templates
- [ ] 08 - Configurable storage directories
- [ ] 09 - Automatic save every 2 minutes
- [ ] 10 - Session timing
- [ ] 11 - Save, New and Close behavior
- [ ] 12 - Cross-device testing
- [ ] 13 - Final polish and release APK

## Development Approach

The application will be developed incrementally.

Each numbered requirement should be implemented separately.

Do not implement future requirements unless explicitly requested.

The numbered requirement files are the source of truth for each development
step.

Keep the application buildable and runnable after every step.

The Nokia T20 should be used to test each development step whenever practical.

The Pixel 7a and BOOX Go 10.3 should be tested periodically and especially
before the final release.

## Requirements Folder

The requirements are stored separately from the application source code:

    requirements/
    ├── README.md
    ├── 01-basic-writing-screen.md
    ├── 02-courier-prime.md
    ├── 03-typewriter-cursor.md
    └── ...

README.md contains the overall project requirements and development plan.

The numbered requirement files describe individual implementation stages.

The requirements are the source of truth for the application.

## Important Principle

Prefer simplicity over additional functionality.

Do not add features merely because they are common in text editors.

The goal is a distraction-free writing tool that feels more like a digital
typewriter than a conventional Android text editor.