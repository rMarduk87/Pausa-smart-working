package rpt.tool.hybridwalk.utils.data.appmodels

import androidx.annotation.Keep
import rpt.tool.hybridwalk.utils.data.AppModel
import rpt.tool.hybridwalk.utils.data.DbModel
import rpt.tool.hybridwalk.utils.data.database.mappers.addMapper
import rpt.tool.hybridwalk.utils.data.database.mappers.streak.StreakToStreakModel
import rpt.tool.hybridwalk.utils.data.database.models.StreakModel
import java.io.Serializable

@Suppress("UNCHECKED_CAST")
@Keep
data class Streak(
    val id: Int = 1,
    val currentStreak: Int = 0,
    val maxStreak: Int = 0,
    val frozenDaysLeft: Int = 0,
    val consecutivePerfectDays: Int = 0,
    val lastCheckedDateEpoch: Long = 0L
) : AppModel(), Serializable {

    init {
        addMapper(StreakToStreakModel())
    }

    override fun <T : DbModel> toDBModel(): T {
        return mappers.single { it.destination == StreakModel::class.java }.map(this) as T
    }
}