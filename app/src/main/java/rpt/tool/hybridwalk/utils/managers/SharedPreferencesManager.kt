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
        return ctx.getSharedPreferences(AppUtils.USERS_SHARED_PREF, Context.MODE_PRIVATE)
    }

    private val sharedPreferences by lazy { createSharedPreferences() }

    var isWfh: Boolean
        get() = sharedPreferences.getBoolean(AppUtils.IS_WFH, true)
        set(value) = sharedPreferences.edit { putBoolean(AppUtils.IS_WFH, value) }

}