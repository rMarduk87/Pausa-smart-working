# Implementation Plan - Fix Unresolved reference 'cleanValue' in AchievementRepository.kt

The build error is caused by a missing extension function `cleanValue()` on `String`, used in `AchievementRepository.kt` for parsing CSV data. Additionally, a bug was found in the column mapping for the `earned` field.

## User Review Required
None.

## Proposed Changes

### [Component: Data Repositories]

#### [MODIFY] [AchievementRepository.kt](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/java/rpt/tool/hybridwalk/utils/data/repositories/AchievementRepository.kt)
1. Add a private extension function `String.cleanValue()` to trim whitespace and remove surrounding quotes.
2. Fix the column index for `earned` and `date` in the `Achievement` constructor (use index 7 for `earned` and index 8 for `date`).

## Verification Plan

### Automated Tests
- Run `./gradlew :app:compileDebugKotlin` to verify the build error is resolved.

### Manual Verification
- None (build fix).
