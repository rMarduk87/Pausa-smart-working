# Fix Kotlin/Compose Binary Incompatibility

The build is failing with an `IllegalStateException` during Kotlin compilation because the Compose Compiler (bundled with Kotlin 2.4.10) cannot find a matching signature for the `remember` inline method in the Compose Runtime library. This is caused by a version mismatch between the very new Kotlin version (2.4.10) and an outdated Compose BOM (2024.10.01).

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/gradle/libs.versions.toml)
- Update `composeBom` from `2024.10.01` to `2026.06.01` to ensure compatibility with Kotlin 2.4.10.
- Update `ksp` from `2.3.10` to `2.4.10` (if available) or the closest compatible version to match the Kotlin version.
- Ensure `kotlin` is set to a stable version (2.4.10 is stable according to lookup).

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/build.gradle.kts)
- Apply the missing `kotlinAndroid` and `kotlin-compose` plugins in the `plugins` block. The absence of these plugins (or their incorrect application) might be contributing to the compiler's inability to correctly resolve Compose intrinsics like `remember`.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to verify that the project compiles successfully.
- Run `./gradlew :app:compileDebugKotlin` specifically to verify the fix for the reported error.

### Manual Verification
- Verify that `DashboardFragment` can be opened in the IDE without any unresolved references in the Compose code.
