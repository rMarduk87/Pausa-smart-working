# Implementation Plan - Generate Achievement Earned Dialog Layout

The goal is to create the missing `dialog_achievement_earned.xml` layout and its dependencies (styles, drawables) while maintaining the app's dark navy style.

## User Review Required

> [!IMPORTANT]
> The app uses a dark navy theme (`#0A192F`). The dialog will follow this style with a rounded background and colored strokes that match the specific achievement's color (set programmatically).

## Proposed Changes

### [Component: UI Resources]

#### [NEW] [bg_achievement_dialog.xml](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/res/drawable/bg_achievement_dialog.xml)
- Rounded rectangle with `#0A192F` background and a default stroke (which will be modified by code).

#### [NEW] [bg_achievement_icon.xml](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/res/drawable/bg_achievement_icon.xml)
- Circular or highly rounded background for the achievement icon/emoji.

#### [NEW] [bg_achievement_button.xml](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/res/drawable/bg_achievement_button.xml)
- Rounded background for the "AWESOME" button.

#### [NEW] [ic_close.xml](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/res/drawable/ic_close.xml)
- A simple 'X' icon for the close button.

#### [NEW] [dialog_achievement_earned.xml](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/res/layout/dialog_achievement_earned.xml)
- The main layout file using `ConstraintLayout`.
- `root_layout`: ID for the outer container.
- `btnClose`: ID for the close icon.
- `iconContainer`: ID for the icon background.
- `txtAchievementIcon`: ID for the emoji/icon text.
- `txtUnlocked`: ID for the "UNLOCKED!" text.
- `txtAchievementTitle`: ID for the title.
- `txtAchievementDesc`: ID for the description.
- `btnOk`: ID for the confirmation button.

#### [MODIFY] [themes.xml](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/res/values/themes.xml)
- Add `CustomDialogTheme` with `android:windowBackground` set to `@android:color/transparent`.

#### [MODIFY] [strings.xml](file:///C:/Users/Riccardo.Pezzolati/Pausa-smart-working/app/src/main/res/values/strings.xml)
- Add `<string name="unlocked">UNLOCKED!</string>`
- Add `<string name="awesome">AWESOME</string>`

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to ensure all resources compile correctly.

### Manual Verification
- Render the layout using `render_compose_preview` (if possible, though this is XML) or ask the user to verify the UI on a device/emulator.
