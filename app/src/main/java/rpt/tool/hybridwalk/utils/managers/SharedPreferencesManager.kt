package rpt.tool.hybridwalk.utils.managers

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import rpt.tool.hybridwalk.HybridWalkApplication
import rpt.tool.hybridwalk.utils.AppUtils


object SharedPreferencesManager {
    private val ctx: Context
        get() = HybridWalkApplication.instance

    private fun createSharedPreferences(): SharedPreferences {
        return ctx.getSharedPreferences(AppUtils.USERS_SHARED_PREF,
            Context.MODE_PRIVATE)
    }

    private val sharedPreferences by lazy { createSharedPreferences() }

    var isWfh: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.IS_WFH, false)
        set(value) = sharedPreferences.edit { putBoolean(AppUtils.IS_WFH, value) }

    var stepGoal: Int
        get() = sharedPreferences.getInt(AppUtils.STEP_GOAL, 7000)
        set(value) = sharedPreferences.edit { putInt(AppUtils.STEP_GOAL, value) }

    var inactivityThreshold: Long
        get() = sharedPreferences.getLong(AppUtils.INACTIVITY_THRESHOLD,
            60L * 60L * 1000L)
        set(value) = sharedPreferences.edit { putLong(AppUtils.INACTIVITY_THRESHOLD,
            value) }

    var showAchievement : Boolean
        get() = sharedPreferences.getBoolean(AppUtils.SHOW_ACHIEVEMENT, false)
        set(value) = sharedPreferences.edit { putBoolean(AppUtils.SHOW_ACHIEVEMENT, value) }

    var hasEarlyBirdSteps: Boolean
        get() = sharedPreferences.getBoolean("has_early_bird_steps", false)
        set(value) = sharedPreferences.edit { putBoolean("has_early_bird_steps", value) }

    var hasNightOwlSteps: Boolean
        get() = sharedPreferences.getBoolean("has_night_owl_steps", false)
        set(value) = sharedPreferences.edit { putBoolean("has_night_owl_steps", value) }
}