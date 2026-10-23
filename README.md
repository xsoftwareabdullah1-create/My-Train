# My Train

A lightweight native Android train-information app inspired by the features of train enquiry apps. This is an independent project and is not affiliated with Google, Where is my Train, or Indian Railways.

## Tech stack
- Java for Android app logic
- XML for Android layouts
- Android SDK / Gradle

The exact internal source-language stack of Where is my Train is not publicly documented in its official product description, so this project uses native Java + XML rather than claiming to copy its internals.

## Current scope
- Train number/name input
- Quick links for official National Train Enquiry System (NTES)
- Live status and PNR are not fabricated; real in-app results require a permitted train-data API
- GitHub Actions workflow to build a debug APK

## API needed?
Yes, for live train running status, schedules, live station, PNR and seat availability inside the app. Indian Railways provides passenger-facing enquiry services through NTES, but does not publish a general public developer API in the official sources checked. See https://enquiry.indianrail.gov.in/mntes/ and the Ministry of Railways' NTES description at https://www.pib.gov.in/PressReleasePage.aspx?PRID=2298355&lang=1&reg=48.

Options:
1. No API key for initial prototype: open official NTES in the browser for live status.
2. In-app live data: select a third-party provider, review its pricing/terms/limits, and keep its API key on a backend proxy—not inside the APK.
3. Timetables/offline search: a licensed/open dataset can be bundled or cached; timetable data alone does not provide live train location.

Do not put private API keys in Android source code or commit them to GitHub.

## Build
Open this repository in Android Studio and build the app module, or use the included GitHub Actions workflow to build a debug APK. Android SDK 35 is used by the workflow.

## Disclaimer
Train data and timings can change. Verify important journey details with official railway sources. This app does not book tickets.
