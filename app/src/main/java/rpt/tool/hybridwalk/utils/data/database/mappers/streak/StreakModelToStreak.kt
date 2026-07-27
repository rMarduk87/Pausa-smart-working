package rpt.tool.hybridwalk.utils.data.database.mappers.streak

import rpt.tool.hybridwalk.utils.data.appmodels.Streak
import rpt.tool.hybridwalk.utils.data.database.mappers.ModelMapper
import rpt.tool.hybridwalk.utils.data.database.models.StreakModel

class StreakModelToStreak : ModelMapper<StreakModel, Streak> {
    override val destination: Class<Streak> = Streak::class.java

    override fun map(source: StreakModel): Streak {
        return Streak(
            id = source.id,
            currentStreak = source.currentStreak,
            maxStreak = source.maxStreak,
            frozenDaysLeft = source.frozenDaysLeft,
            consecutivePerfectDays = source.consecutivePerfectDays,
            lastCheckedDateEpoch = source.lastCheckedDateEpoch
        )
    }
}