# Ai V2Ray Client

A precision-instrument VPN client for Android, in Jetpack Compose with a
brushed-steel + burnished-copper design language, built against the
`libv2ray` / Xray core via the `AndroidLibXrayLite` AAR.

## What this bundle contains

| Path | What |
|---|---|
| `build.gradle.kts`, `app/build.gradle.kts`, `settings.gradle.kts`, `gradle/wrapper/` | Gradle 8.10.2 wrapper config; AGP 8.7.3; Kotlin 2.0.21; Compose plugin |
| `app/src/main/AndroidManifest.xml` | Typed FGS (specialUse, subtype `vpn_tunnel`), required permissions only |
| `app/src/main/java/com/aiv2ray/client/` | Kotlin source — see file map below |
| `app/src/main/res/` | Resources: colors, themes, drawables, backup/extraction rules |
| `PRIVACY_POLICY.md`, `PRERELEASE_CHECKS.md`, `STORE_LISTING.md` | Play-Console artefacts |
| `proguard-rules.pro` | R8 / ProGuard keep rules for gomobile AAR |

### Source file map

```
AiV2RayApplication.kt           — Application class + notification channel
ui/MainActivity.kt              — Entry, consent gate, theme
ui/permissions/ConsentScreen.kt — In-app prominent disclosure
ui/home/HomeScreen.kt           — Header, nav tabs, connection orchestrator
ui/home/HomePanes.kt            — AI-assist panel, dials, power button
ui/settings/SettingsScreen.kt   — Kill switch, DNS, theme
ui/info/InfoScreen.kt           — Device info, policy info
ui/theme/Color.kt Type.kt Theme.kt EngravedText.kt VpnComponents.kt
                                  — Palette, typography, engraved text, steel tray, key-cap
service/V2RayVpnService.kt      — Tun-based VpnService
service/XrayController.kt       — Wraps libv2ray.Libv2ray
data/V2RayConfigParser.kt       — vmess://, vless://, trojan://, ss:// -> Xray JSON
data/ServerRepository.kt        — DataStore-backed profile storage
vm/HomeViewModel.kt             — MVI state for connection home
vm/ConsentViewModel.kt          — Persisted acceptance of disclosure
```

## Prerequisites

1. **Android Studio Hedgehog** or `cmdline-tools` build-tools 36+
2. **JDK 17+** (this sandbox uses OpenJDK 21)
3. **Android SDK**: `compileSdk = 36`, `targetSdk = 36`, `minSdk = 24`
4. **A upload keystore** — generate, NEVER check in.

## Build steps (do these yourself — I cannot sign or upload from here)

```
# 1. Generate keystore (offline, keep safe):
keytool -genkeypair -keystore ai-v2ray-upload.jks \
  -keysize 4096 -validity 25000 -alias ai-v2ray

# 2. Add signing config to ~/.gradle/gradle.properties or
#    a local.properties (don't commit):
KEYSTORE=/absolute/path/ai-v2ray-upload.jks
KEYSTORE_PASSWORD=...
KEY_ALIAS=ai-v2ray
KEY_PASSWORD=...

# 3. Open the project in Android Studio, let it sync.

# 4. Build a release APK:
./gradlew :app:assembleRelease

# 5. Sign:
$ANDROID_HOME/build-tools/<v>/apksigner sign \
  --ks $KEYSTORE \
  --ks-key-alias $KEY_ALIAS \
  --out app-release-signed.apk \
  app/build/outputs/apk/release/app-release.apk

# Optional — verify APK signature
apksigner verify --verbose app-release-signed.apk
```

For an AAB (Play prefers):

```
./gradlew :app:bundleRelease
```

## What I cannot do from this sandbox

- I do NOT have Gradle or Android SDK build-tools here, so this APK was never compiled.
- I do NOT hold your keystore, so this APK was never signed.
- Only Google can grant Play approval.
- Only you can submit the VpnService declaration form in Play Console.

| Step | Status |
|---|---|
| Source code | ✅ written |
| Policy compliance review | ✅ verified by web search on 2026-10-12 |
| Compiled APK | ❌ not produced here |
| Signed APK | ❌ not produced here |
| Uploaded to Play | ❌ only you |
| Play approval | ❌ only Google |

## Print list of Google Play policies verified today

- https://support.google.com/googleplay/android-developer/answer/12564964 — VpnService
- https://developer.android.com/google/play/requirements/target-sdk — Target API 36
- https://developer.android.com/about/versions/14/changes/fgs-types-required — typed FGS
- https://support.google.com/googleplay/android-developer/answer/10787469 — Data safety

## Trademark note

"V2Ray" is also the name of the `v2fly/V2Ray` open-source project. Shipping
a commercial product under that exact name may invite marketplace confusion.
Consider a distinguishing suffix (e.g. **AiV2Ray**, **V2Ray Plus**) before
launch.

## License

This source bundle is released to you for the purpose of building,
signing, and submitting your own derivative Android APK. The Xray core is
governed by its own license (MPL-2.0); the `AndroidLibXrayLite` AAR is
governed by its maintainer's license and must be verified by you.
