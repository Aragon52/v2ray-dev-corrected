# Pre-release checks for Google Play

Run these checks BEFORE you click "Review and rollout" in Play Console.

## 1. VpnService declaration form (MANDATORY)

Play Console → App content → Sensitive permissions and APIs →

Confirm the form for `android.permission.BIND_VPN_SERVICE` is filled in. Your
answers should match the in-app disclosure. Without this declaration Google
severs the app from Play distribution.

Reference: https://support.google.com/googleplay/android-developer/answer/12564964

## 2. Privacy policy URL

Play Console → Store presence → Store settings → Privacy policy

Paste the HTTPS URL where you host `PRIVACY_POLICY.md`. The URL must be HTTPS
and reachable to Google's crawler.

## 3. Data Safety form

Play Console → App content → Data safety

Suggested answers (mirror the privacy policy exactly):

| Question | Answer |
|---|---|
| "Does your app collect or share any of the required user data types?" | **No** |
| "Is all of the user data collected by your app encrypted in transit?" | **Yes** (the VPN tunnel uses TLS by default configuration) |
| "Do you provide a way for users to request that their data is deleted?" | **N/A — no data is collected** |
| "Are you using any advertising ID?" | **No** |
| "Does your app share data with third parties?" | **No** |
| "Is this functionality required for app functionality, or optional?" | **Functionality required** (vpn_tunnel subtype) |

Reference: https://support.google.com/googleplay/android-developer/answer/10787469

## 4. Target API level

`compileSdk = 36`, `targetSdk = 36`. Required by Play from 2026-08-31 onward.

Reference: https://developer.android.com/google/play/requirements/target-sdk

## 5. App content ratings

The questionnaire asks about violence, sexuality, profanity, controlled
substances, etc. Answer "None" for all categories.

## 6. Government app declaration

You are NOT declaring this as a government app. The app is a private
consumer utility.

## 7. Ads declaration

The app does NOT serve ads and does NOT contain an ad SDK. Mark "No ads."

Reference: https://support.google.com/googleplay/android-developer/answer/12564964
(Google explicitly forbids VPN apps that tamper with advertising unless an
explicit user setting enables it — this app contains no ad-blocking code.)

## 8. Account login

The app does NOT use accounts.

## 9. Target audience

Choose "Everyone" (no age restriction).

## 10. Upload artefacts

Upload both:
- `app/build/outputs/apk/release/app-release.apk` (or AAB); signed with your own keystore
- The screenshot set from the design bundle (zip at the same Play-upload time)
