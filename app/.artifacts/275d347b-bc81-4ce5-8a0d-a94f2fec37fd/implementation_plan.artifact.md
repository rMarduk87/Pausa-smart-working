# Flash Challenge Interaction and Completion Plan

This plan outlines the steps to make the "Flash Challenge" (Sfida Flash) card interactive, allowing users to mark it as completed and view its status on the dashboard.

## User Review Required

> [!NOTE]
> The dialog will be implemented using Jetpack Compose's `AlertDialog` to match the dashboard's modern UI style.

## Proposed Changes

### [Utils Component]

#### [MODIFY] [AppUtils.kt](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/java/rpt/tool/hybridwalk/utils/AppUtils.kt)
- Add constants for daily challenge completion status and date.

#### [MODIFY] [SharedPreferencesManager.kt](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/java/rpt/tool/hybridwalk/utils/managers/SharedPreferencesManager.kt)
- Add properties to store and retrieve the challenge completion status and the date it was completed.

### [Resources Component]

#### [MODIFY] [strings.xml](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/res/values/strings.xml) and [values-it/strings.xml](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/res/values-it/strings.xml)
- Add strings for the challenge completion dialog (question, confirmation, completion date label).

### [Dashboard Component]

#### [MODIFY] [DashboardViewModel.kt](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/java/rpt/tool/hybridwalk/ui/dashboard/DashboardViewModel.kt)
- Expose `isChallengeCompleted` and `challengeCompletionDate` states.
- Add `completeChallenge()` method to persist the completion.
- Reset the completion status in `assignDailyChallenge()` if the date has changed.

#### [MODIFY] [DashboardFragment.kt](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/java/rpt/tool/hybridwalk/ui/dashboard/DashboardFragment.kt)
- Make the Flash Challenge card clickable.
- Show an `AlertDialog` when the card is clicked.
- Display the completion status and date on the card if the challenge is finished.

## Verification Plan

### Automated Tests
- Run `:app:compileDebugKotlin` to verify the build.

### Manual Verification
1. Open the Dashboard.
2. Click on the Flash Challenge card.
3. Confirm completion in the dialog.
4. Verify the card updates to show "Challenge passed" and the current date.
5. Close and reopen the app to ensure the state is persisted.
