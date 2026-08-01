# Walkthrough - Fix Unresolved Reference 'crashlytics'

The build error `Unresolved reference 'crashlytics'` in `HybridWalkApplication.kt` has been fixed. During the process, other unresolved references (`timber`, `BuildConfig`) and Gradle configuration issues were also identified and resolved.

## Changes Made

### Dependency Management
- Updated `gradle/libs.versions.toml` to include:
    - `firebase-crashlytics` library and plugin.
    - `timber` library for logging.
- Added these dependencies to `app/build.gradle.kts`.

### Build Configuration
- Applied the `com.google.firebase.crashlytics` plugin to both project and app-level build files.
- Enabled `buildConfig` in `app/build.gradle.kts` to resolve the `BuildConfig` reference.
- Corrected the `ksp` configuration block placement in `app/build.gradle.kts` (moved it outside the `android` block).

### Verification Results
- Executed `gradle_sync` successfully.
- Executed `./gradlew :app:compileDebugKotlin` successfully.

> [!TIP]
> The `ksp` block was moved out of the `android` block to comply with the latest KSP Gradle plugin requirements, which resolved a "Suspicious receiver type" warning.
