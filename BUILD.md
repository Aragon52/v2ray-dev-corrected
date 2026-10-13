# Building Ai V2Ray Client

This project builds an Android APK. You have two ways to get a binary:

- **Path A — GitHub Actions (recommended, no local toolchain):** push the repo
  and let GitHub's runners compile and sign the APK. You just download the
  artifact. No PC, no AIDE, no Android Studio required.
- **Path B — Android Studio (local):** open the folder and build normally.

> **Do not use AIDE.** AIDE cannot build this project: it has no support for the
> Kotlin DSL build scripts, the Compose compiler plugin, or AGP 8.x. Any failure
> there is AIDE's, not this code's.

---

## Path A — GitHub Actions

### 1. Push the repo

```bash
git init
git add .
git commit -m "Ai V2Ray Client"
git branch -M main
git remote add origin https://github.com/<you>/<repo>.git
git push -u origin main
```

Pushing to `main` triggers `.github/workflows/android-release.yml`, which:
1. verifies the bundled Xray `.aar` checksum,
2. builds the debug APK,
3. builds the release APK + AAB (signed if secrets are set),
4. uploads all of them as workflow artifacts **and** attaches them to a
   per-build GitHub Release.

### 2. (Optional) Enable signing

Without secrets the release APK/AAB are produced **unsigned**. To sign them,
create an upload keystore and add four repository secrets.

Create a keystore (run locally, keep it secret — never commit it):

```bash
keytool -genkeypair -v \
  -keystore ai-v2ray-upload.jks \
  -alias ai-v2ray \
  -keyalg RSA -keysize 4096 -validity 25000
```

Base64-encode it and add it plus three passwords under
**Settings → Secrets and variables → Actions → New repository secret**:

| Secret | Value |
|---|---|
| `KEYSTORE_BASE64` | output of `base64 -w0 ai-v2ray-upload.jks` |
| `KEYSTORE_PASSWORD` | keystore password |
| `KEY_ALIAS` | `ai-v2ray` |
| `KEY_PASSWORD` | key password |

Re-run the workflow (or push). The artifacts will now be signed.

### 3. Download the APK

- Actions run → **Artifacts** (`app-release-apk`, `app-debug-apk`,
  `app-release-aab`), or
- **Releases** → the build tagged `build-<n>`.

Install the debug/release APK on your device directly (enable "install unknown
apps"). Upload the AAB to Google Play.

---

## Path B — Android Studio

Prerequisites: Android Studio (Ladybug or newer), JDK 17+.

```bash
# 1. Add your SDK path
echo "sdk.dir=/absolute/path/to/Android/Sdk" > local.properties

# 2. Debug build (no signing needed)
./gradlew :app:assembleDebug
#    -> app/build/outputs/apk/debug/app-debug.apk
```

For a signed release build, create `keystore.properties` in the project root
(do **not** commit it):

```properties
storeFile=/absolute/path/ai-v2ray-upload.jks
storePassword=...
keyAlias=ai-v2ray
keyPassword=...
```

Then:

```bash
./gradlew :app:assembleRelease    # -> .../apk/release/app-release.apk
./gradlew :app:bundleRelease      # -> .../bundle/release/app-release.aab
```

---

## The native Xray core (`app/libs/libv2ray.aar`)

The app talks to the Xray core through the gomobile binding
[`2dust/AndroidLibXrayLite`](https://github.com/2dust/AndroidLibXrayLite),
shipped as a prebuilt AAR in `app/libs/`:

- File: `app/libs/libv2ray.aar` (release tag **v26.10.10**)
- SHA-256 (verified in CI): `15db760bfbc24c0492ee997784b48a7b5e1bc5032950d617cd252fc2195f7ac7`
- License: the AAR / Xray core are governed by their own licenses (Xray core is
  MPL-2.0). Verify licensing before distribution.

To upgrade, download a newer `libv2ray.aar` from the project's Releases and
replace the file, then update `.aar.sha256`:

```bash
curl -L -o app/libs/libv2ray.aar \
  https://github.com/2dust/AndroidLibXrayLite/releases/download/<tag>/libv2ray.aar
sha256sum app/libs/libv2ray.aar > .aar.sha256
```

The AAR bundles `arm64-v8a`, `armeabi-v7a`, `x86`, `x86_64` native libraries and
the geo asset files (`geoip.dat`, `geosite.dat`, …).

---

## Programming interface (for reference)

The real binding (verified against the generated `libv2ray` sources) is:

```kotlin
// one-time, before starting the core
go.Seq.setContext(appContext)
libv2ray.Libv2ray.initCoreEnv(assetPath, xudpBaseKey)

// lifecycle
val cb = object : libv2ray.CoreCallbackHandler {
    override fun startup(): Long = 0
    override fun shutdown(): Long = 0
    override fun onEmitStatus(level: Long, message: String?): Long = 0
}
val controller = libv2ray.Libv2ray.newCoreController(cb)
controller.startLoop(configJson, tunFd)   // tunFd from VpnService.Builder.establish().fd, or 0
controller.stopLoop()
```

The config must contain a `"protocol": "tun"` inbound — the core reads the tun
fd from the `xray.tun.fd` env var that `startLoop()` sets. `V2RayConfigParser`
generates exactly that shape.

---

## Version pins

| Component | Version |
|---|---|
| Gradle | 8.11.1 |
| Android Gradle Plugin | 8.10.1 |
| Kotlin / Compose compiler plugin | 2.1.20 |
| Compose BOM | 2024.09.03 |
| compileSdk / targetSdk / minSdk | 36 / 36 / 24 |
| AndroidLibXrayLite (AAR) | v26.10.10 |
