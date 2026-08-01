package rpt.tool.hybridwalk

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test that simulates a user interacting with the full application flow.
 */
@RunWith(AndroidJUnit4::class)
class AppUserFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun dashboard_toggleInteractions() {
        // Wait for dashboard to load and verify "Today" title
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.today)).assertIsDisplayed()

        // Verify "Smart Working Mode" toggle is present
        val wfhTitle = composeTestRule.activity.getString(R.string.wfh_mode)
        composeTestRule.onNodeWithText(wfhTitle).assertIsDisplayed()

        // Verify "Rest Day / Gym" toggle is present
        val gymTitle = composeTestRule.activity.getString(R.string.rest_gym_day)
        composeTestRule.onNodeWithText(gymTitle).assertIsDisplayed()
        
        // Note: Clicking toggles might trigger system dialogs (permissions) which can block UI tests
        // unless handled with UiAutomator or pre-granted. For this simulation, we verify presence.
    }

    @Test
    fun navigation_fullBottomBarFlow() {
        // 1. Dashboard is start destination - verify "Keep the pace" subtitle
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.keep_pace)).assertIsDisplayed()

        // 2. Go to Progress (Stats)
        val statsLabel = composeTestRule.activity.getString(R.string.progress)
        composeTestRule.onNodeWithText(statsLabel).performClick()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.stats_title)).assertIsDisplayed()

        // 3. Go to Achievements
        val achievementsLabel = composeTestRule.activity.getString(R.string.achievements)
        composeTestRule.onNodeWithText(achievementsLabel).performClick()
        // Verify a unique element in Achievement screen
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.recalculate)).assertIsDisplayed()

        // 4. Go to Streak
        val streakLabel = composeTestRule.activity.getString(R.string.streak_nav)
        composeTestRule.onNodeWithText(streakLabel).performClick()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.streak_titles)).assertIsDisplayed()

        // 5. Go to Settings
        val settingsLabel = composeTestRule.activity.getString(R.string.settings)
        composeTestRule.onNodeWithText(settingsLabel).performClick()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.customize_experience)).assertIsDisplayed()

        // 6. Return to Dashboard
        val todayLabel = composeTestRule.activity.getString(R.string.today)
        composeTestRule.onNodeWithText(todayLabel).performClick()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.keep_pace)).assertIsDisplayed()
    }

    @Test
    fun settings_contentVerification() {
        // Navigate to Settings
        val settingsLabel = composeTestRule.activity.getString(R.string.settings)
        composeTestRule.onNodeWithText(settingsLabel).performClick()

        // Verify key sections are visible
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.theme_color)).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.daily_step_goal)).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.inactivity_reminder)).assertIsDisplayed()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.export_data)).assertIsDisplayed()
    }
}
