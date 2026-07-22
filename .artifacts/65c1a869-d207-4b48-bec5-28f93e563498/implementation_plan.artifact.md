# Fix Squashed DashboardFragment Layout

The `DashboardFragment` appears "squashed" because its content is too tall for some screen sizes and lacks scrollability. Additionally, a redundant XML `BottomNavigationView` in `activity_main.xml` overlaps with the screen content, further reducing available space and causing layout confusion.

## Proposed Changes

### [app]

#### [MODIFY] [activity_main.xml](file:///Users/marduk87/Sviluppo/Pausa-smart-working/app/src/main/res/layout/activity_main.xml)
- Remove the redundant `BottomNavigationView` as navigation is handled via Compose's `HybridScaffold`.

#### [MODIFY] [DashboardFragment.kt](file:///Users/marduk87/Sviluppo/Pausa-smart-working/app/src/main/java/rpt/tool/hybridwalk/ui/dashboard/DashboardFragment.kt)
- Add `verticalScroll` to the main `Column` in `DashboardScreen` to allow content to be accessible on all screen sizes without being squashed.
- Ensure proper imports for scrolling functionality.

#### [MODIFY] [StatsScreen.kt](file:///Users/marduk87/Sviluppo/Pausa-smart-working/app/src/main/java/rpt/tool/hybridwalk/utils/view/StatsScreen.kt)
- Add `verticalScroll` to `StatsScreen` as a preventive measure to ensure the chart and labels aren't compressed on smaller screens.

## Verification Plan

### Manual Verification
- Deploy the app and verify that `DashboardFragment` is no longer squashed.
- Ensure that the bottom navigation bar (Compose version) is functional and not overlapped by an XML counterpart.
- Verify that both Dashboard and Stats screens are scrollable if the content exceeds the screen height.
