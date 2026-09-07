package rpt.tool.hybridwalk.utils.managers

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import rpt.tool.hybridwalk.utils.AppUtils

object SharedPreferencesManager {

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        if (!::sharedPreferences.isInitialized) {
            sharedPreferences = context.applicationContext.getSharedPreferences(
                AppUtils.USERS_SHARED_PREF,
                Context.MODE_PRIVATE
            )
        }
    }

    var isWfh: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.IS_WFH, false)
        set(value) = sharedPreferences.edit { putBoolean(AppUtils.IS_WFH, value) }

    var stepGoal: Int
        get() = sharedPreferences.getInt(AppUtils.STEP_GOAL, 7000)
        set(value) = sharedPreferences.edit { putInt(AppUtils.STEP_GOAL, value) }

    var inactivityThreshold: Long
        get() = sharedPreferences.getLong(AppUtils.INACTIVITY_THRESHOLD, 60L * 60L * 1000L)
        set(value) = sharedPreferences.edit { putLong(AppUtils.INACTIVITY_THRESHOLD, value) }

    var showAchievement: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.SHOW_ACHIEVEMENT, true)
        set(value) = sharedPreferences.edit { putBoolean(AppUtils.SHOW_ACHIEVEMENT, value) }

    var hasEarlyBirdSteps: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.HAS_EARLY_BIRD_STEPS, false)
        set(value) = sharedPreferences.edit { putBoolean(AppUtils.HAS_EARLY_BIRD_STEPS, value) }

    var hasNightOwlSteps: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.HAS_NIGHT_OWL_STEPS, false)
        set(value) = sharedPreferences.edit { putBoolean(AppUtils.HAS_NIGHT_OWL_STEPS, value) }

    var primaryColorHex: String
        get() = sharedPreferences.getString(AppUtils.PRIMARY_COLOR, "#81B29A") ?: "#81B29A"
        set(value) = sharedPreferences.edit { putString(AppUtils.PRIMARY_COLOR, value) }

    var dailyChallengeId: Int
        get() = sharedPreferences.getInt(AppUtils.DAILY_CHALLENGE_ID, -1)
        set(value) = sharedPreferences.edit { putInt(AppUtils.DAILY_CHALLENGE_ID, value) }

    var dailyChallengeDateEpoch: Long
        get() = sharedPreferences.getLong(AppUtils.DAILY_CHALLENGE_DATE, 0L)
        set(value) = sharedPreferences.edit { putLong(AppUtils.DAILY_CHALLENGE_DATE, value) }

    var isDailyChallengeCompleted: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.DAILY_CHALLENGE_COMPLETED, false)
        set(value) = sharedPreferences.edit {
            putBoolean(
                AppUtils.DAILY_CHALLENGE_COMPLETED,
                value
            )
        }

    var dailyChallengeCompletionDate: String
        get() = sharedPreferences.getString(AppUtils.DAILY_CHALLENGE_COMPLETION_DATE, "") ?: ""
        set(value) = sharedPreferences.edit {
            putString(
                AppUtils.DAILY_CHALLENGE_COMPLETION_DATE,
                value
            )
        }

    var stepOffset: Int
        get() = sharedPreferences.getInt(AppUtils.STEP_OFFSET, 0)
        set(value) = sharedPreferences.edit { putInt(AppUtils.STEP_OFFSET, value) }

    var lastSavedDateEpochDay: Long
        get() = sharedPreferences.getLong(AppUtils.LAST_SAVED_DATE, 0L)
        set(value) = sharedPreferences.edit { putLong(AppUtils.LAST_SAVED_DATE, value) }

    var isGoalAlreadyNotified: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.IS_GOAL_ALREADY_NOTIFIED, false)
        set(value) = sharedPreferences.edit { putBoolean(AppUtils.IS_GOAL_ALREADY_NOTIFIED, value) }
}