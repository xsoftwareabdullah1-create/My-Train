# My Train API setup

## Provider reviewed: Qrail.in

Pricing and features checked on 23 October 2026 from the provider's developer page:

- Free: INR 0, 100 requests/day; train schedules, PNR status and trains-between-stations.
- Pro: INR 1,999/year; adds running status and live station, 10,000 requests/day.
- Business: INR 4,999/year; adds fare and availability, 50,000 requests/day.

Source: https://www.qrail.in/developers

These are provider-published claims, not a guarantee of uptime or data accuracy. Review the provider's current terms and privacy policy yourself before launching publicly; its terms page could not be retrieved reliably during this review. Do not promise live status while using the free tier.

## Architecture

The Android app must call your own HTTPS backend, not Qrail directly. The backend holds QRAIL_API_KEY as a server environment secret and proxies requests through /api/live/{five-digit-train-number}. This avoids embedding the private key in the APK or Git history.

## Deploy

1. Create a Qrail account and select a plan that permits your intended use.
2. Deploy this repository on a Node.js host using render.yaml, or deploy the server directory manually.
3. Add QRAIL_API_KEY in the host's environment-variable settings. Never commit the key.
4. Test https://YOUR-SERVICE/health. providerConfigured should be true.
5. Test https://YOUR-SERVICE/api/live/12565. Do not repeatedly poll; respect your plan's quotas.
6. Configure the Android app to use your deployed HTTPS base URL before expecting in-app live data.

## Important limitations

- This repository does not include an API key or a deployed backend URL.
- Live train data has not been verified against a real API account.
- Do not present stale or missing updates as live; show the provider timestamp and provide a link to official NTES for verification.
- The API is unofficial. Verify accuracy and terms before relying on it.
- Do not use the service to book tickets or collect sensitive PNR/passenger data until the provider's terms and data-handling requirements are reviewed.
