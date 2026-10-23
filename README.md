# My Train

A lightweight native Android train-information app inspired by common railway enquiry features. This is an independent project and is not affiliated with Google, Where is my Train, or Indian Railways.

## Tech stack
- Java for Android application logic and UI
- Android SDK / Gradle
- Node.js backend proxy for third-party railway data
- GitHub Actions to build a debug APK

The exact internal source-language stack of Where is my Train is not publicly documented, so this project uses native Android Java rather than claiming to copy its internals.

## Current scope
- Five-digit train number search
- Android UI can call a configured HTTPS backend for live status
- Backend proxy keeps the third-party API key out of the APK
- Official NTES and Indian Railways links for verification
- GitHub Actions workflow builds and uploads a debug APK artifact

## Third-party API reviewed

Qrail.in publishes these plan details on its developer page (checked 23 October 2026):
- Free: INR 0, 100 requests/day for PNR status, train schedules and trains between stations.
- Pro: INR 1,999/year, adds running status and live station, 10,000 requests/day.
- Business: INR 4,999/year, adds fare and availability, 50,000 requests/day.

Source: https://www.qrail.in/developers

The provider is unofficial. Review its current terms and privacy policy before public launch. The provider's terms page could not be retrieved reliably during this review. Live running status requires the paid Pro tier according to the published plan comparison; the free tier does not include it.

Detailed setup: [API_SETUP.md](API_SETUP.md)

## Configure the API

1. Register with Qrail and choose a plan appropriate for your intended use.
2. Deploy the Node.js service in the server directory.
3. Set QRAIL_API_KEY as a secret environment variable on the hosting service. Never commit it.
4. Check the service /health endpoint and confirm providerConfigured is true.
5. In My Train, enter the deployed HTTPS backend URL and a five-digit train number.
6. Verify important results against official NTES: https://enquiry.indianrail.gov.in/mntes/

No API key or deployed backend URL is included in this repository. Live status has not been tested with a real provider account yet.

## Build
Open this repository in Android Studio and build the app module, or use the included GitHub Actions workflow to build a debug APK. Android SDK 35 is used by the workflow.

## Disclaimer
Train data may be delayed or inaccurate. Do not treat a provider's last reported position as guaranteed GPS tracking. This app does not book tickets.
