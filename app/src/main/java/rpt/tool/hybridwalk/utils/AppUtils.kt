package rpt.tool.hybridwalk.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppUtils {
    companion object {
        fun getCurrentDate(): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = Date()
            return dateFormat.format(currentDate)
        }

        fun dpToPx(dp: Int): Int {
            return (dp * android.content.res.Resources.getSystem().displayMetrics.density).toInt()
        }


        const val USERS_SHARED_PREF : String = "user_pref"
        const val IS_WFH : String = "is_wfh"
        const val SHOW_ACHIEVEMENT : String = "show_achievement"
        const val STEP_GOAL : String = "step_goal"
        const val INACTIVITY_THRESHOLD : String = "inactivity_threshold"

    }
}