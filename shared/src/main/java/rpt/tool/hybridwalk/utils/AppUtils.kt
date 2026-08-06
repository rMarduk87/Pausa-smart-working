package rpt.tool.hybridwalk.utils

import android.content.res.Resources
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
            return (dp * Resources.getSystem().displayMetrics.density).toInt()
        }


        const val USERS_SHARED_PREF : String = "user_pref"
        const val IS_WFH : String = "is_wfh"
        const val SHOW_ACHIEVEMENT : String = "show_achievement"
        const val STEP_GOAL : String = "step_goal"
        const val INACTIVITY_THRESHOLD : String = "inactivity_threshold"

        const val HAS_EARLY_BIRD_STEPS : String ="has_early_bird_steps"
        const val HAS_NIGHT_OWL_STEPS : String ="has_night_owl_steps"
        const val PRIMARY_COLOR : String ="primary_color"
        const val DAILY_CHALLENGE_ID : String ="daily_challenge_id"
        const val DAILY_CHALLENGE_DATE : String ="daily_challenge_date"
        const val DAILY_CHALLENGE_COMPLETED : String ="daily_challenge_completed"
        const val DAILY_CHALLENGE_COMPLETION_DATE : String ="daily_challenge_completion_date"

        const val STEP_OFFSET : String = "step_offset"
        const val LAST_SAVED_DATE : String = "last_saved_date"
        const val IS_GOAL_ALREADY_NOTIFIED : String = "is_goal_already_notified"
    }
}