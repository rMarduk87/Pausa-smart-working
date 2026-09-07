package rpt.tool.hybridwalk.utils.data.database.enums

import androidx.annotation.Keep
import androidx.annotation.StringRes
import rpt.tool.hybridwalk.shared.R


@Keep
enum class AchievementType(val id: Int, @param:StringRes val descriptionResId: Int, val description: String) {
    STEPS(1, R.string.ach_type_steps, "steps"),
    WFH(2, R.string.ach_type_wfh, "wfh"),
    GYM(3, R.string.ach_type_gym, "gym"),
    STREAK(4, R.string.ach_type_streak, "streak"),
    GOAL(5, R.string.ach_type_goal, "goal"),
    APP(6, R.string.ach_type_app, "app");

    companion object {
        fun fromId(id: Int): AchievementType = entries.firstOrNull { it.id == id } ?: STEPS
    }
}