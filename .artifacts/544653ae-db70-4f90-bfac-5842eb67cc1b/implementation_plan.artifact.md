# Fix 'Unresolved reference: desugar' Sync Error

The project is failing to sync because the `desugar` library reference in `app/build.gradle.kts` cannot be resolved. This is likely due to a naming mismatch or incorrect accessor generation from the Version Catalog (`libs.versions.toml`).

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/gradle/libs.versions.toml)
- Rename the library alias `desugar_jdk_libs` to `desugar-jdk-libs` to ensure consistent accessor generation (`libs.desugar.jdk.libs`).
- Optionally rename the version reference to `desugar-jdk-libs` for consistency, though not strictly required.

#### [MODIFY] [build.gradle.kts](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/build.gradle.kts)
- Ensure the reference `libs.desugar.jdk.libs` is correct (it currently is, so if the rename in `libs.versions.toml` works, this will resolve).

## Verification Plan

### Automated Tests
- Run `gradle sync` (or a build task) to verify that the unresolved reference error is gone.
- Run `./gradlew :app:assembleDebug` to ensure the project builds successfully with desugaring enabled.
