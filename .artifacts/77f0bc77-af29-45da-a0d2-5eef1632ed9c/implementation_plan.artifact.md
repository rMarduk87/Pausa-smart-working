# Implementation Plan - Add Settings Fragment

This plan covers adding a new Settings screen to the app, allowing users to customize their daily step goal and inactivity reminder threshold. The settings will be persisted using `SharedPreferencesManager`.

## Proposed Changes

### [Component: Data & Managers]

#### [MODIFY] [AppUtils.kt](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/java/rpt/tool/hybridwalk/utils/AppUtils.kt)
- Add keys for `STEP_GOAL` and `INACTIVITY_THRESHOLD`.

#### [MODIFY] [SharedPreferencesManager.kt](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/java/rpt/tool/hybridwalk/utils/managers/SharedPreferencesManager.kt)
- Add `stepGoal` (Int) and `inactivityThreshold` (Long/Int) properties.

#### [MODIFY] [RepositoryManager.kt](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/java/rpt/tool/hybridwalk/utils/managers/RepositoryManager.kt)
- Use `SharedPreferencesManager.stepGoal` when creating a new `DailyRecord` in `incrementSteps`.

### [Component: UI Shell]

#### [MODIFY] [Screen.kt](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/java/rpt/tool/hybridwalk/utils/view/Screen.kt)
- Add `Settings` object to `Screen` sealed class.
- Update `HybridScaffold` to include the Settings tab in the `NavigationBar`.

#### [MODIFY] [main_nav_graph.xml](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/res/navigation/main_nav_graph.xml)
- Add `settingsFragment` destination.
- Add actions to navigate between Dashboard, Stats, and Settings.

#### [MODIFY] [strings.xml](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/res/values/strings.xml)
- Add strings for "Impostazioni", "Obiettivo passi", "Promemoria inattività", and other UI elements.

### [Component: Settings Feature]

#### [NEW] [SettingsViewModel.kt](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/java/rpt/tool/hybridwalk/ui/settings/SettingsViewModel.kt)
- Manage state for step goal and inactivity threshold.

#### [NEW] [SettingsFragment.kt](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/java/rpt/tool/hybridwalk/ui/settings/SettingsFragment.kt)
- Implement UI using Jetpack Compose, matching the dark theme and style of Dashboard/Stats.
- Include interactive components for adjusting settings.

### [Component: Existing Screens Update]

#### [MODIFY] [DashboardFragment.kt](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/java/rpt/tool/hybridwalk/ui/dashboard/DashboardFragment.kt)
- Update navigation logic in `HybridScaffold` to handle the new Settings tab.

#### [MODIFY] [StatsFragment.kt](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/java/rpt/tool/hybridwalk/ui/stats/StatsFragment.kt)
- Update navigation logic in `HybridScaffold` to handle the new Settings tab.

### [Component: Background Services]

#### [MODIFY] [StepTrackerService.kt](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/java/rpt/tool/hybridwalk/utils/services/StepTrackerService.kt)
- Use `SharedPreferencesManager.inactivityThreshold` instead of the hardcoded `INACTIVITY_THRESHOLD`.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:compileDebugKotlin` to ensure no regression.

### Manual Verification
1. Open the app and navigate to the new Settings tab.
2. Change the daily step goal and verify it reflects on the Dashboard.
3. Change the inactivity threshold.
4. Verify that settings persist after app restart.
