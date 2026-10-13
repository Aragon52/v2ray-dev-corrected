// Top-level build file.
//
// Toolchain pinned to versions that officially support compileSdk = 36:
//   AGP 8.10.1  (compileSdk 36 support), Kotlin 2.1.20 (+ matching Compose
//   compiler plugin), Gradle 8.11.1 (required by AGP 8.10.x).
plugins {
    id("com.android.application") version "8.10.1" apply false
    id("org.jetbrains.kotlin.android") version "2.1.20" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20" apply false
}
