package rpt.tool.hybridwalk.utils.data.database.enums

import androidx.annotation.Keep
import androidx.annotation.StringRes
import rpt.tool.hybridwalk.shared.R

@Keep
enum class UnitType(val id: Int, @param:StringRes val descriptionResId: Int, val description: String) {

    STEPS(1, R.string.unit_type_steps, "steps"),
    DAYS(2, R.string.unit_type_days, "days"),
    SESSIONS(3, R.string.unit_type_sessions, "sessions"),
    GOALS(4, R.string.unit_type_goals, "goals"),
    MILESTONES(5, R.string.unit_type_milestones, "milestones"),
    UNLOCKED(6, R.string.unit_type_unlocked, "unlocked");

    companion object {
        fun fromId(id: Int): UnitType = entries.firstOrNull { it.id == id } ?: STEPS
    }
}