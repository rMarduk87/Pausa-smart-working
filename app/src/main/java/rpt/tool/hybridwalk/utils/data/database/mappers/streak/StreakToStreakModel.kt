package rpt.tool.hybridwalk.utils.data.database.mappers.streak

import rpt.tool.hybridwalk.utils.data.appmodels.Streak
import rpt.tool.hybridwalk.utils.data.database.mappers.ModelMapper
import rpt.tool.hybridwalk.utils.data.database.models.StreakModel

class StreakToStreakModel : ModelMapper<Streak, StreakModel> {
    override val destination: Class<StreakModel> = StreakModel::class.java

    override fun map(source: Streak): StreakModel {
        return StreakModel(
            id = source.id,
            currentStreak = source.currentStreak,
            maxStreak = source.maxStreak,
            frozenDaysLeft = source.frozenDaysLeft,
            consecutivePerfectDays = source.consecutivePerfectDays,
            lastCheckedDateEpoch = source.lastCheckedDateEpoch
        )
    }
}