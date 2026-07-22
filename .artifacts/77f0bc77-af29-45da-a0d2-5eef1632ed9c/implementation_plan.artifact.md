# Implementation Plan - Fix build errors across the project

The project is currently failing to build due to several issues:
1. `HybridScaffold` is missing its mandatory `content` lambda in `DashboardFragment` and `StatsFragment`.
2. `Screen` sealed class has an invalid definition that depends on a non-existent `Context`.
3. `safeNavController()` calls are missing the `navHostId`.
4. `DashboardFragmentDirections` and `StatsFragmentDirections` are unresolved (Safe Args missing/not working).
5. `DashboardViewModel` attempts to reassign a `val` property.

## Proposed Changes

### [Component: UI Utilities]

#### [MODIFY] [Screen.kt](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/java/rpt/tool/hybridwalk/utils/view/Screen.kt)
- Redefine `Screen` to use `@StringRes titleRes: Int` instead of `title: String` and remove the `Context` parameter.
- Update `Dashboard` and `Stats` objects to pass resource IDs.
- Update `HybridScaffold` to resolve the title string using `stringResource(screen.titleRes)`.

### [Component: Dashboard]

#### [MODIFY] [DashboardFragment.kt](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/java/rpt/tool/hybridwalk/ui/dashboard/DashboardFragment.kt)
- Wrap the main UI content (the `Surface` block) inside the `HybridScaffold` trailing lambda.
- Pass `R.id.main_activity_nav_host_fragment` to `safeNavController()`.
- Replace `DashboardFragmentDirections` usage with direct action ID navigation.

#### [MODIFY] [DashboardViewModel.kt](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/java/rpt/tool/hybridwalk/ui/dashboard/DashboardViewModel.kt)
- Fix `toggleWfh` to update `_isWfhActive.value` instead of `isWfhActive.value`.

### [Component: Stats]

#### [MODIFY] [StatsFragment.kt](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/java/rpt/tool/hybridwalk/ui/stats/StatsFragment.kt)
- Wrap the `Surface` content inside `HybridScaffold` trailing lambda.
- Pass `R.id.main_activity_nav_host_fragment` to `safeNavController()`.
- Replace `StatsFragmentDirections` usage with direct action ID navigation.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:compileDebugKotlin` to verify that all reported build errors are resolved.

### Manual Verification
- Deploy the app to a device/emulator to ensure navigation between Dashboard and Stats works as expected.
