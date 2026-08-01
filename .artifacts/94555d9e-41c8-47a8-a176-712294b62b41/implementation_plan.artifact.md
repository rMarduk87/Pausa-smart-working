# Fix dependency resolution for material-icons-extended

The project is failing to sync because `androidx.compose.material:material-icons-extended` is declared without a version in `libs.versions.toml`, and the Compose BOM (Bill of Materials) is not applied to the `implementation` configuration in `app/build.gradle.kts`.

## Proposed Changes

### [app module](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/build.gradle.kts)

#### [MODIFY] [build.gradle.kts](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/build.gradle.kts)
- Add `implementation(platform(libs.androidx.compose.bom))` to the `dependencies` block. This will allow Compose libraries (including `material-icons-extended`) to resolve their versions from the BOM.

### [Gradle Configuration](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/gradle/libs.versions.toml)

#### [MODIFY] [libs.versions.toml](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/gradle/libs.versions.toml)
- Update `composeBom` to the latest stable version `2026.06.01` to ensure all components are up to date.
- Clean up the redundant `compose-icons-extended` entry if it's not used.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to verify that the project builds and dependencies are resolved.
- Perform a Gradle Sync in Android Studio.

### Manual Verification
- Check that the `material-icons-extended` library is correctly indexed and available for use in the IDE.
